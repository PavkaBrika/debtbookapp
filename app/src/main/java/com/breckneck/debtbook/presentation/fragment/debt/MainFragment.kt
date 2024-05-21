package com.breckneck.debtbook.presentation.fragment.debt

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.animation.Animation.AnimationListener
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.viewmodel.MainActivityViewModel
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.util.*
import com.breckneck.deptbook.domain.util.Filter
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val TAG = "MainFragment"

    private val vm by viewModel<MainFragmentViewModel>()
    private val mainActivityVM by activityViewModel<MainActivityViewModel>()

    private lateinit var filterButton: ImageView
    private lateinit var humanAdapter: HumanAdapter

    interface OnButtonClickListener {
        fun onHumanClick(idHuman: Int, currency: String, name: String)

        fun onAddButtonClick()

        fun onTickVibration()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "MainFragment created")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setFragmentResultListener("mainFragmentKey") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.init()
            else if (mainActivityVM.isNeedDebtDataUpdate.value == true)
                vm.init()
            else if (mainActivityVM.isNeedUpdateDebtSums.value == true)
                vm.getMainSums()
        }
        Log.e(TAG, "MainFragment create view")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        if (vm.isSortDialogOpened.value == true)
            showHumanSortDialog()
        if (vm.isChangeDebtNameDialogOpened.value == true)
            openChangeDebtNameDialog(
                humanDomain = vm.changedHuman.value!!,
                position = vm.changedHumanPosition.value!!
            )

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        val humanClickListener = object : HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
                buttonClickListener?.onHumanClick(
                    idHuman = humanDomain.id,
                    currency = humanDomain.currency,
                    name = humanDomain.name
                )
                Log.e(TAG, "Click on human with id = ${humanDomain.id}")
            }

            override fun onHumanLongClick(humanDomain: HumanDomain, position: Int) {
                openChangeDebtNameDialog(humanDomain = humanDomain, position = position)
            }
        }
        humanAdapter = HumanAdapter(listOf(), humanClickListener)
        recyclerView.adapter = humanAdapter

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)
        val mainRecyclerViewSecondHintTextView: TextView = view.findViewById(R.id.mainRecyclerViewSecondHintTextView)

        val emptyDebtsLayout: ConstraintLayout = view.findViewById(R.id.emptyDebtsLayout)
        val debtsLayout: ConstraintLayout = view.findViewById(R.id.debtsLayout)
        val debtProgressBar: ProgressBar = view.findViewById(R.id.debtProgressBar)

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener {
            buttonClickListener?.onAddButtonClick()
        }

        filterButton = view.findViewById(R.id.filterHumanButton)
        filterButton.setOnClickListener {
            showHumanSortDialog()
        }

        vm.humanFilter.observe(viewLifecycleOwner) {
            changeFilterButtonColor(it)
        }

        vm.resultHumanList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                humanAdapter.updateHumansList(it)
                Log.e(TAG, "adapter link success")
            }
        }

        vm.humanOrder.observe(viewLifecycleOwner) {
            vm.sortHumans()
        }

        vm.debtListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.LOADING -> {
                    debtsLayout.visibility = View.GONE
                    emptyDebtsLayout.visibility = View.GONE
                    debtProgressBar.visibility = View.VISIBLE
                }
                ListState.FILLED -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(debtsLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    debtsLayout.visibility = View.VISIBLE
                    emptyDebtsLayout.visibility = View.GONE
                    debtProgressBar.visibility = View.GONE
                }
                ListState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyDebtsLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    emptyDebtsLayout.visibility = View.VISIBLE
                    debtsLayout.visibility = View.GONE
                    debtProgressBar.visibility = View.GONE
                }
            }
        }

        val overallPositiveSumTextView: TextView =
            view.findViewById(R.id.overallPositiveSumTextView)
        val overallNegativeSumTextView: TextView =
            view.findViewById(R.id.overallNegativeSumTextView)

        vm.mainSums.observe(viewLifecycleOwner) {
            overallPositiveSumTextView.text = it.first
            if (it.first == "") {
                overallPositiveSumTextView.visibility = View.GONE
            } else {
                overallPositiveSumTextView.visibility = View.VISIBLE
                overallPositiveSumTextView.text = it.first
            }

            overallNegativeSumTextView.text = it.second
            if (it.second == "") {
                overallNegativeSumTextView.textSize = 0F
            } else {
                overallNegativeSumTextView.visibility = View.VISIBLE
                overallNegativeSumTextView.text = it.second
            }
        }
    }

    private fun showHumanSortDialog() {
        val bottomSheetDialogFilter = BottomSheetDialog(requireContext())
        bottomSheetDialogFilter.setContentView(R.layout.dialog_sort)
        bottomSheetDialogFilter.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.onHumanSortDialogOpen()

        var sortHumansAttribute = vm.humanOrder.value!!.first
        when (sortHumansAttribute) {
            HumanOrderAttribute.Date ->
                bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderCreationDateRadioButton)!!.isChecked =
                    true

            HumanOrderAttribute.Sum ->
                bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderSumRadioButton)!!.isChecked =
                    true
        }

        var sortByIncrease = vm.humanOrder.value!!.second
        val sortImageView = bottomSheetDialogFilter.findViewById<ImageView>(R.id.sortImageView)
        if (sortByIncrease)
            sortImageView!!.rotationY = ROTATE_DEGREE_HUMAN_IMAGE_VIEW_BY_INCREASE
        else
            sortImageView!!.rotationY = ROTATE_DEGREE_HUMAN_IMAGE_VIEW_BY_DECREASE

        bottomSheetDialogFilter.findViewById<CardView>(R.id.sortButtonCard)!!.setOnClickListener {
            buttonClickListener!!.onTickVibration()
            if (sortImageView.rotationY == ROTATE_DEGREE_HUMAN_IMAGE_VIEW_BY_INCREASE) {
                sortImageView.rotationY = ROTATE_DEGREE_HUMAN_IMAGE_VIEW_BY_DECREASE
                sortByIncrease = false
            } else {
                sortImageView.rotationY = ROTATE_DEGREE_HUMAN_IMAGE_VIEW_BY_INCREASE
                sortByIncrease = true
            }
        }

        var humansFilter: Filter = vm.humanFilter.value!!

        when (vm.humanFilter.value!!) {
            Filter.All -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showAllRadioButton)!!.isChecked =
                true

            Filter.Negative -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showNegativeRadioButton)!!.isChecked =
                true

            Filter.Positive -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showPositiveRadioButton)!!.isChecked =
                true
        }

        //TODO MIGRATE DB TO MAKE THIS VISIBLE
        bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderDateRadioButton)!!.visibility = View.GONE

        bottomSheetDialogFilter.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            when (bottomSheetDialogFilter.findViewById<RadioGroup>(R.id.filterRadioGroup)!!.checkedRadioButtonId) {
                R.id.showAllRadioButton -> {
                    humansFilter = Filter.All
                }

                R.id.showPositiveRadioButton -> {
                    humansFilter = Filter.Positive
                }

                R.id.showNegativeRadioButton -> {
                    humansFilter = Filter.Negative
                }
            }

            when (bottomSheetDialogFilter.findViewById<RadioGroup>(R.id.orderRadioGroup)!!.checkedRadioButtonId) {
                R.id.orderCreationDateRadioButton -> {
                    sortHumansAttribute = HumanOrderAttribute.Date
                }

                R.id.orderSumRadioButton -> {
                    sortHumansAttribute = HumanOrderAttribute.Sum
                }
            }
            val order = Pair(sortHumansAttribute, sortByIncrease)
            if (vm.humanFilter.value!! != humansFilter) {
                vm.onSetHumanFilter(filter = humansFilter)
                if (vm.humanOrder.value!! == order)
                    vm.sortHumans()
            }

            if (vm.humanOrder.value!! != order)
                vm.onSetHumanOrder(order = order)

            if (bottomSheetDialogFilter.findViewById<CheckBox>(R.id.rememberChoiceCheckBox)!!.isChecked)
                vm.saveHumanOrder(order = order)

            bottomSheetDialogFilter.dismiss()
        }

        bottomSheetDialogFilter.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            bottomSheetDialogFilter.dismiss()
        }
        bottomSheetDialogFilter.setOnDismissListener {
            vm.onHumanSortDialogClose()
        }
        bottomSheetDialogFilter.setOnCancelListener {
            vm.onHumanSortDialogClose()
        }
        bottomSheetDialogFilter.show()
    }

    private fun changeFilterButtonColor(humansFilter: Filter) {
        if (view != null) {
            when (humansFilter) {
                Filter.All -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (resources.configuration.isNightModeActive)
                            filterButton.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.white
                                )
                            )
                        else
                            filterButton.setColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black
                                )
                            )
                    } else {
                        filterButton.setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                    }
                }

                Filter.Negative -> {
                    filterButton.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.red))
                }

                Filter.Positive -> {
                    filterButton.setColorFilter(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.green
                        )
                    )
                }
            }
        }
    }

    private fun openChangeDebtNameDialog(humanDomain: HumanDomain, position: Int) {
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(R.layout.dialog_change_debt_name)
        vm.onChangeDebtNameDialogOpen(humanDomain = humanDomain, position = position)
        val humanNameTextInput: TextInputLayout = dialog.findViewById(R.id.humanNameTextInput)!!
        val humanNameEditText: TextInputEditText = dialog.findViewById(R.id.humanNameEditText)!!
        humanNameEditText.setText(humanDomain.name)
        dialog.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            val changedName = humanNameEditText.text.toString().trim()
            if (changedName.isEmpty())
                humanNameTextInput.error = getString(R.string.youmustentername)
            else {
                if (changedName == humanDomain.name)
                    dialog.dismiss()
                else {
                    humanDomain.name = changedName
                    changeDebtName(humanDomain = humanDomain, position = position)
                    dialog.dismiss()
                }
            }
        }

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.onChangeDebtNameDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onChangeDebtNameDialogClose()
        }

        dialog.show()

    }

    private fun changeDebtName(humanDomain: HumanDomain, position: Int) {
        humanAdapter.updateHuman(humanDomain = humanDomain, position = position)
        vm.updateHuman(human = humanDomain)
    }
}