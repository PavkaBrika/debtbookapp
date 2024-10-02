package com.breckneck.debtbook.debt.presentation

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.debt.adapter.HumanAdapter
import com.breckneck.debtbook.core.viewmodel.MainActivityViewModel
import com.breckneck.debtbook.debt.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.util.*
import com.breckneck.deptbook.domain.util.Filter
import com.facebook.shimmer.ShimmerFrameLayout
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
    private lateinit var humanRecyclerView: RecyclerView

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
                vm.setListStateLoading()
            else if (mainActivityVM.isNeedDebtDataUpdate.value == true)
                vm.setListStateLoading()
            else if (mainActivityVM.isNeedUpdateDebtSums.value == true)
                vm.getMainSums()
        }
        Log.e(TAG, "MainFragment create view")
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val emptyHumansLayout: ConstraintLayout = view.findViewById(R.id.emptyHumansLayout)
        val humansLayout: ConstraintLayout = view.findViewById(R.id.humansLayout)
        val loadingDebtsLayout: ConstraintLayout = view.findViewById(R.id.loadingHumansLayout)
        val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)

        vm.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ScreenState.LOADING -> {
                    humansLayout.visibility = View.GONE
                    emptyHumansLayout.visibility = View.GONE
                    loadingDebtsLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmerAnimation()
                }
                ScreenState.SUCCESS -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(humansLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    humansLayout.visibility = View.VISIBLE
                    emptyHumansLayout.visibility = View.GONE
                    loadingDebtsLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
                ScreenState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyHumansLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    emptyHumansLayout.visibility = View.VISIBLE
                    humansLayout.visibility = View.GONE
                    loadingDebtsLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
            }
        }

        humanRecyclerView = view.findViewById(R.id.humanRecyclerView)
        humanRecyclerView.adapter = humanAdapter

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener {
            buttonClickListener?.onAddButtonClick()
        }

        filterButton = view.findViewById(R.id.filterHumanButton)
        vm.isSearching.observe(viewLifecycleOwner) { isSearching ->
            if (isSearching)
                filterButton.visibility = View.GONE
            else
                filterButton.visibility = View.VISIBLE
        }
        filterButton.setOnClickListener {
            showHumanSortDialog()
        }

        vm.humanFilter.observe(viewLifecycleOwner) {
            changeFilterButtonColor(it)
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

        val humanDebtSearchView = view.findViewById<SearchView>(R.id.humanSearchView)
        humanDebtSearchView.setOnSearchClickListener {
            if (vm.screenState.value != ScreenState.EMPTY) {
                vm.onStartSearch()
                vm.setIsSearching(isSearching = true)
            }
        }
        humanDebtSearchView.setOnCloseListener {
            vm.onStopSearch()
            vm.setIsSearching(isSearching = false)
            false
        }
        humanDebtSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vm.setSearchQuery(newText!!)
                return true
            }
        })

        vm.resultedHumanList.observe(viewLifecycleOwner) {
            humanAdapter.updateHumansList(it)
            Log.e(TAG, "data in adapter link success")
        }
    }

    override fun onResume() {
        super.onResume()

        //TODO THIS CODE IS UPDATING RECYCLER VIEW WITHOUT LAGGING AND GHOSTING BUT WITH SHOWING PROGRESS BAR AFTER CHANGING ORIENTATION
//        Handler(Looper.getMainLooper()).postDelayed({
//            vm.resultHumanList.observe(this) {
//                if (it.isNotEmpty()) {
//                    humanAdapter.updateHumansList(it)
//                    Log.e(TAG, "data in adapter link success")
//                    vm.setScreenState(ListState.RECEIVED)
//                } else {
//                    vm.setScreenState(ListState.EMPTY)
//                }
//            }
//        }, 400)

        //TODO THIS CODE IS UPDATING RECYCLER VIEW WITHOUT LAGGING BUT WITH GHOSTING OF PREVIOUS FRAGMENT
//        vm.resultHumanList.observe(viewLifecycleOwner) {
//            if (it.isNotEmpty()) {
//                humanAdapter.updateHumansList(it)
//                Log.e(TAG, "data in adapter link success")
//                vm.onSetListState(ListState.FILLED)
//            }
//        }
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
            Filter.ALL -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showAllRadioButton)!!.isChecked =
                true

            Filter.NEGATIVE -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showNegativeRadioButton)!!.isChecked =
                true

            Filter.POSITIVE -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showPositiveRadioButton)!!.isChecked =
                true
        }

        //TODO MIGRATE DB TO MAKE THIS VISIBLE
        bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderDateRadioButton)!!.visibility = View.GONE

        bottomSheetDialogFilter.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            when (bottomSheetDialogFilter.findViewById<RadioGroup>(R.id.filterRadioGroup)!!.checkedRadioButtonId) {
                R.id.showAllRadioButton -> {
                    humansFilter = Filter.ALL
                }

                R.id.showPositiveRadioButton -> {
                    humansFilter = Filter.POSITIVE
                }

                R.id.showNegativeRadioButton -> {
                    humansFilter = Filter.NEGATIVE
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
            vm.onSetHumanSort(filter = humansFilter, order = order)

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
                Filter.ALL -> {
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

                Filter.NEGATIVE -> {
                    filterButton.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.red))
                }

                Filter.POSITIVE -> {
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