package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.HumanAdapter
import com.breckneck.debtbook.presentation.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.util.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    private val vm by viewModel<MainFragmentViewModel>()

    lateinit var filterButton: ImageView

    interface OnButtonClickListener {
        fun onHumanClick(idHuman: Int, currency: String, name: String)

        fun onAddButtonClick()

        fun onSettingsButtonClick()

        fun getDebtQuantity()

        fun onChangeOrderButtonClick()
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "Activity created")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        if (vm.isSortDialogOpened.value == true)
            showHumanSortDialog()

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                view.context,
                DividerItemDecoration.VERTICAL
            )
        )

        val noDebtsTextView: TextView = view.findViewById(R.id.noDebtTextView)
        val mainRecyclerViewHintTextView: TextView =
            view.findViewById(R.id.mainRecyclerViewHintTextView)

        val addButton: FloatingActionButton = view.findViewById(R.id.addHumanButton)
        addButton.setOnClickListener {
            buttonClickListener?.onAddButtonClick()
        }

        val humanClickListener = object : HumanAdapter.OnHumanClickListener {
            override fun onHumanClick(humanDomain: HumanDomain, position: Int) {
                buttonClickListener?.onHumanClick(
                    idHuman = humanDomain.id,
                    currency = humanDomain.currency,
                    name = humanDomain.name
                )
                Log.e("TAG", "Click on human with id = ${humanDomain.id}")
            }
        }

        val settingsButton: ImageView = view.findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            buttonClickListener?.onSettingsButtonClick()
        }

        filterButton = view.findViewById(R.id.filterHumanButton)
        filterButton.setOnClickListener {
            showHumanSortDialog()
        }

        vm.humanFilter.observe(viewLifecycleOwner) {
            vm.getHumans()
            changeSortButtonColor(it)
        }

        vm.humanList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                mainRecyclerViewHintTextView.visibility = View.VISIBLE
                noDebtsTextView.visibility = View.INVISIBLE
            } else {
                mainRecyclerViewHintTextView.visibility = View.INVISIBLE
                noDebtsTextView.visibility = View.VISIBLE
            }
            val adapter = HumanAdapter(it, humanClickListener)
            recyclerView.adapter = adapter
            Log.e("TAG", "adapter link success")
        }

        vm.humanOrder.observe(viewLifecycleOwner) {
            vm.sortHumans()
        }

        buttonClickListener?.getDebtQuantity()

        val overallPositiveSumTextView: TextView =
            view.findViewById(R.id.overallPositiveSumTextView)

        vm.positiveSum.observe(viewLifecycleOwner) {
            overallPositiveSumTextView.text = it
            if (it == "") {
                overallPositiveSumTextView.visibility = View.GONE
            } else {
                overallPositiveSumTextView.visibility = View.VISIBLE
                overallPositiveSumTextView.text = it
            }
        }

        val overallNegativeSumTextView: TextView =
            view.findViewById(R.id.overallNegativeSumTextView)
        vm.negativeSum.observe(viewLifecycleOwner) {
            overallNegativeSumTextView.text = it
            if (it == "") {
                overallNegativeSumTextView.textSize = 0F
            } else {
                overallNegativeSumTextView.visibility = View.VISIBLE
                overallNegativeSumTextView.text = it
            }
        }

        return view
    }

    fun showHumanSortDialog() {
        val bottomSheetDialogFilter = BottomSheetDialog(requireContext())
        bottomSheetDialogFilter.setContentView(R.layout.dialog_sort)
        bottomSheetDialogFilter.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        vm.onHumanSortDialogOpen()

        var sortHumansAttribute = vm.humanOrder.value!!.first
        when (sortHumansAttribute) {
            HumanOrderAttribute.Date ->
                bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderDateRadioButton)!!.isChecked =
                    true

            HumanOrderAttribute.Sum ->
                bottomSheetDialogFilter.findViewById<RadioButton>(R.id.orderSumRadioButton)!!.isChecked =
                    true
        }

        var sortByIncrease = vm.humanOrder.value!!.second
        val sortImageView = bottomSheetDialogFilter.findViewById<ImageView>(R.id.sortImageView)
        if (sortByIncrease)
            sortImageView!!.rotationY = ROTATE_HUMAN_IMAGE_VIEW_BY_INCREASE
        else
            sortImageView!!.rotationY = ROTATE_HUMAN_IMAGE_VIEW_BY_DECREASE

        bottomSheetDialogFilter.findViewById<CardView>(R.id.sortButtonCard)!!.setOnClickListener {
            buttonClickListener!!.onChangeOrderButtonClick()
            if (sortImageView.rotationY == ROTATE_HUMAN_IMAGE_VIEW_BY_INCREASE) {
                sortImageView.rotationY = ROTATE_HUMAN_IMAGE_VIEW_BY_DECREASE
                sortByIncrease = false
            } else {
                sortImageView.rotationY = ROTATE_HUMAN_IMAGE_VIEW_BY_INCREASE
                sortByIncrease = true
            }
        }

        var humansFilter: HumanFilter = vm.humanFilter.value!!

        when (vm.humanFilter.value!!) {
            HumanFilter.AllHumans -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showAllHumansRadioButton)!!.isChecked =
                true

            HumanFilter.NegativeHumans -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showNegativeHumansRadioButton)!!.isChecked =
                true

            HumanFilter.PositiveHumans -> bottomSheetDialogFilter.findViewById<RadioButton>(R.id.showPositiveHumansRadioButton)!!.isChecked =
                true
        }

        bottomSheetDialogFilter.findViewById<Button>(R.id.confirmButton)!!.setOnClickListener {
            when (bottomSheetDialogFilter.findViewById<RadioGroup>(R.id.filterRadioGroup)!!.checkedRadioButtonId) {
                R.id.showAllHumansRadioButton -> {
                    humansFilter = HumanFilter.AllHumans
                }

                R.id.showPositiveHumansRadioButton -> {
                    humansFilter = HumanFilter.PositiveHumans
                }

                R.id.showNegativeHumansRadioButton -> {
                    humansFilter = HumanFilter.NegativeHumans
                }
            }

            when (bottomSheetDialogFilter.findViewById<RadioGroup>(R.id.orderRadioGroup)!!.checkedRadioButtonId) {
                R.id.orderDateRadioButton -> {
                    sortHumansAttribute = HumanOrderAttribute.Date
                }

                R.id.orderSumRadioButton -> {
                    sortHumansAttribute = HumanOrderAttribute.Sum
                }
            }

            if (vm.humanOrder.value!! != Pair(sortHumansAttribute, sortByIncrease))
                vm.onSetHumanOrder(Pair(sortHumansAttribute, sortByIncrease))

            if (bottomSheetDialogFilter.findViewById<CheckBox>(R.id.rememberChoiceCheckBox)!!.isChecked)
                vm.setHumanOrder(order = Pair(sortHumansAttribute, sortByIncrease))

            if (vm.humanFilter.value!! != humansFilter)
                vm.onSetHumanFilter(humansFilter)
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

    fun changeSortButtonColor(humansFilter: HumanFilter) {
        if (view != null) {
            when (humansFilter) {
                HumanFilter.AllHumans -> {
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

                HumanFilter.NegativeHumans -> {
                    filterButton.setColorFilter(ContextCompat.getColor(view!!.context, R.color.red))
                }

                HumanFilter.PositiveHumans -> {
                    filterButton.setColorFilter(
                        ContextCompat.getColor(
                            view!!.context,
                            R.color.green
                        )
                    )
                }
            }
        }
    }
}