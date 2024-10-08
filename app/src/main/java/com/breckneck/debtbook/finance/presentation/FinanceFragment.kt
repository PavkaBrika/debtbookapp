package com.breckneck.debtbook.finance.presentation

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.settings.adapter.SettingsAdapter
import com.breckneck.debtbook.finance.adapter.UsedFinanceCategoryAdapter
import com.breckneck.debtbook.core.customview.CustomSwitchView
import com.breckneck.debtbook.finance.customview.FinanceProgressBar
import com.breckneck.debtbook.finance.viewmodel.FinanceViewModel
import com.breckneck.deptbook.domain.model.FinanceCategoryWithFinances
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.FinanceInterval
import com.breckneck.deptbook.domain.util.ListState
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar

class FinanceFragment : Fragment() {

    private val TAG = "FinanceFragment"

    private val vm by viewModel<FinanceViewModel>()

    private lateinit var usedFinanceCategoryAdapter: UsedFinanceCategoryAdapter

    private lateinit var financeProgressBar: FinanceProgressBar

    interface OnButtonClickListener {
        fun onAddFinanceButtonClick(financeCategoryState: FinanceCategoryState, dayInMillis: Long)

        fun onFinanceCategoryClick(categoryName: String, categoryId: Int, financeCategoryState: FinanceCategoryState, currency: String)
    }

    var buttonClickListener: OnButtonClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = context as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFragmentResultListener("financeFragmentKey") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getAllCategoriesWithFinances()
        }
        vm.setFinanceListState(ListState.LOADING)
        return inflater.inflate(R.layout.fragment_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val decimalFormat = DecimalFormat("###,###,###.##")
        val customSymbol: DecimalFormatSymbols = DecimalFormatSymbols()
        customSymbol.groupingSeparator = ' '
        decimalFormat.decimalFormatSymbols = customSymbol

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val currencyNames = resources.getStringArray(R.array.currencies).toList()

        val financeIntervalNames = listOf(getString(R.string.day),
            getString(R.string.week), getString(R.string.month), getString(R.string.year_full))

        val onCurrencySettingsClickListener = object : SettingsAdapter.OnClickListener {
            override fun onClick(setting: String, position: Int) {
                vm.setCurrency(setting.substring(setting.lastIndexOf(" ") + 1))
            }
        }

        val onIntervalSettingsClickListener = object : SettingsAdapter.OnClickListener {
            override fun onClick(setting: String, position: Int) {
                when (position) {
                    0 -> vm.setInterval(interval = FinanceInterval.DAY)
                    1 -> vm.setInterval(interval = FinanceInterval.WEEK)
                    2 -> vm.setInterval(interval = FinanceInterval.MONTH)
                    3 -> vm.setInterval(interval = FinanceInterval.YEAR)
                }
            }
        }

        if (vm.isCurrencyDialogOpened.value == true) {
            showCurrencyDialog(
                settingsList = currencyNames,
                selectedSetting = vm.selectedCurrencyPosition.value!!,
                onSettingsClickListener = onCurrencySettingsClickListener
            )
        }

        if (vm.isFinanceIntervalDialogOpened.value == true) {
            showFinanceIntervalDialog(
                settingsList = financeIntervalNames,
                selectedSetting = vm.selectedIntervalPosition.value!!,
                onSettingsClickListener = onIntervalSettingsClickListener
            )
        }

        val financeCurrencyTextView: TextView = view.findViewById(R.id.financeCurrencyTextView)
        vm.currency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices) {
                if (currencyNames[i].contains(currency)) {
                    financeCurrencyTextView.text = currencyNames[i]
                    financeCurrencyTextView.setOnClickListener {
                        Log.e(TAG, "Currency clicked")
                        vm.onCurrencyDialogOpen(selectedCurrencyPosition = i)
                        showCurrencyDialog(
                            settingsList = currencyNames,
                            selectedSetting = i,
                            onSettingsClickListener = onCurrencySettingsClickListener
                        )
                    }
                }
            }
        }

        val financeDateIntervalCardView: CardView =
            view.findViewById(R.id.financeDateIntervalCardView)
        val financeDateIntervalTextView: TextView =
            view.findViewById(R.id.financeDateIntervalTextView)
        vm.financeInterval.observe(viewLifecycleOwner) { interval ->
            val i = when (interval) {
                FinanceInterval.DAY -> 0
                FinanceInterval.WEEK -> 1
                FinanceInterval.MONTH -> 2
                FinanceInterval.YEAR -> 3
            }
            financeDateIntervalCardView.setOnClickListener {
                vm.onIntervalDialogOpen(selectedIntervalPosition = i)
                showFinanceIntervalDialog(
                    settingsList = financeIntervalNames,
                    selectedSetting = i,
                    onSettingsClickListener = onIntervalSettingsClickListener
                )
            }
        }
        vm.financeIntervalString.observe(viewLifecycleOwner) { intervalString ->
            financeDateIntervalTextView.text = intervalString
        }

        val financeSwitch: CustomSwitchView = view.findViewById(R.id.financeSwitch)
        financeSwitch.setOnClickListener {
            vm.onChangeFinanceCategoryState()
        }

        val overallSumTextView: TextView = view.findViewById(R.id.overallSumTextView)
        vm.overallSum.observe(viewLifecycleOwner) { sum ->
            overallSumTextView.text = "${decimalFormat.format(sum)} ${vm.currency.value}"
        }

        val noNotesTextView: TextView = view.findViewById(R.id.noNotesTextView)
        vm.financeCategoryState.observe(viewLifecycleOwner) { state ->
            vm.getAllCategoriesWithFinances()
            when (state) {
                FinanceCategoryState.EXPENSE -> {
                    noNotesTextView.text =
                        getString(R.string.there_are_no_expenses_yet_click_the_button_below_to_add)
                    overallSumTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                }

                FinanceCategoryState.INCOME -> {
                    noNotesTextView.text =
                        getString(R.string.there_are_no_revenues_yet_click_the_button_below_to_add)
                    overallSumTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
            }
        }

        val backToCurrentDateImageView: ImageView =
            view.findViewById(R.id.backToCurrentDateImageView)
        vm.financeIntervalUnix.observe(viewLifecycleOwner) { financeIntervalInMillis ->
            val beginOfTheDayInSeconds = financeIntervalInMillis.first / 1000
            when (vm.financeInterval.value) {
                FinanceInterval.DAY -> {
                    if (beginOfTheDayInSeconds == vm.currentDayInSeconds.value)
                        backToCurrentDateImageView.visibility = View.GONE
                    else
                        backToCurrentDateImageView.visibility = View.VISIBLE
                }

                FinanceInterval.WEEK -> {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = vm.currentDayInSeconds.value!! * 1000
                    calendar.set(Calendar.DAY_OF_YEAR, 0)
                    calendar.set(Calendar.DAY_OF_MONTH, 0)
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    if (beginOfTheDayInSeconds == calendar.timeInMillis / 1000)
                        backToCurrentDateImageView.visibility = View.GONE
                    else
                        backToCurrentDateImageView.visibility = View.VISIBLE
                }

                FinanceInterval.MONTH -> {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = vm.currentDayInSeconds.value!! * 1000
                    calendar.set(
                        Calendar.DAY_OF_MONTH,
                        calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                    )
                    if (beginOfTheDayInSeconds == calendar.timeInMillis / 1000)
                        backToCurrentDateImageView.visibility = View.GONE
                    else
                        backToCurrentDateImageView.visibility = View.VISIBLE
                }

                FinanceInterval.YEAR -> {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = vm.currentDayInSeconds.value!! * 1000
                    calendar.set(
                        Calendar.DAY_OF_YEAR,
                        calendar.getActualMinimum(Calendar.DAY_OF_YEAR)
                    )
                    if (beginOfTheDayInSeconds == calendar.timeInMillis / 1000)
                        backToCurrentDateImageView.visibility = View.GONE
                    else
                        backToCurrentDateImageView.visibility = View.VISIBLE
                }

                null -> {}
            }
        }
        backToCurrentDateImageView.setOnClickListener {
            vm.setInterval(interval = vm.financeInterval.value!!)
        }

//        val listSwitch: CustomSwitchView = view.findViewById(R.id.listSwitch)
//        listSwitch.setOnClickListener {
//            vm.onChangeFinanceListStateSwitch()
//        }

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)

        val pastDateImageView: ImageView = view.findViewById(R.id.pastDateImageView)
        pastDateImageView.setOnClickListener {
            vm.getPastFinanceInterval()
        }

        val nextDateImageView: ImageView = view.findViewById(R.id.nextDateImageView)
        nextDateImageView.setOnClickListener {
            vm.getNextFinanceInterval()
        }

//        vm.financeListState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                FinanceListState.CATEGORIES -> {
//                    val onUsedFinanceCategoryClickListener =
//                        object : UsedFinanceCategoryAdapter.OnUsedFinanceCategoryClickListener {
//                            override fun onClick(usedFinance: FinanceCategoryWithFinances) {
//
//                            }
//                        }
//                    usedFinanceCategoryAdapter = UsedFinanceCategoryAdapter(
//                        onUsedFinanceCategoryClickListener = onUsedFinanceCategoryClickListener,
//                        currency = vm.currency.value!!
//                    ).also {
//                        categoryRecyclerView.adapter = it
//                    }
//                }
//
//                FinanceListState.HISTORY -> {
//
//                }
//            }
//        }

        val onUsedFinanceCategoryClickListener =
            object : UsedFinanceCategoryAdapter.OnUsedFinanceCategoryClickListener {
                override fun onClick(usedFinance: FinanceCategoryWithFinances) {
                    buttonClickListener!!.onFinanceCategoryClick(
                        categoryName = usedFinance.financeCategory.name,
                        categoryId = usedFinance.financeCategory.id,
                        financeCategoryState = vm.financeCategoryState.value!!,
                        currency = vm.currency.value!!
                    )
                }
            }
        usedFinanceCategoryAdapter = UsedFinanceCategoryAdapter(
            onUsedFinanceCategoryClickListener = onUsedFinanceCategoryClickListener,
            currency = vm.currency.value!!
        ).also {
            categoryRecyclerView.adapter = it
        }

        val categoryLayout: LinearLayout = view.findViewById(R.id.categoryLayout)
        val emptyNotesLayout: ConstraintLayout = view.findViewById(R.id.emptyNotesLayout)
        val loadingNotesLayout: ConstraintLayout = view.findViewById(R.id.loadingFinanceCategoriesLayout)
        val shimmerLayout: ShimmerFrameLayout = view.findViewById(R.id.shimmerLayout)
        financeProgressBar = view.findViewById(R.id.financeProgressBar)

        vm.financeListState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ListState.LOADING -> {
                    categoryLayout.visibility = View.GONE
                    emptyNotesLayout.visibility = View.GONE
                    loadingNotesLayout.visibility = View.VISIBLE
                    shimmerLayout.startShimmerAnimation()
                }
                ListState.RECEIVED -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(categoryLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    categoryLayout.visibility = View.VISIBLE
                    emptyNotesLayout.visibility = View.GONE
                    loadingNotesLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
                ListState.EMPTY -> {
                    val transition = Fade()
                    transition.duration = 200
                    transition.addTarget(emptyNotesLayout)
                    TransitionManager.beginDelayedTransition(view as ViewGroup?, transition)
                    categoryLayout.visibility = View.GONE
                    emptyNotesLayout.visibility = View.VISIBLE
                    loadingNotesLayout.visibility = View.GONE
                    shimmerLayout.stopShimmerAnimation()
                }
            }
        }

        val addFinanceButton: FloatingActionButton = view.findViewById(R.id.addFinanceButton)
        addFinanceButton.setOnClickListener {
            if (vm.financeInterval.value == FinanceInterval.DAY)
                buttonClickListener!!.onAddFinanceButtonClick(
                    financeCategoryState = vm.financeCategoryState.value!!,
                    dayInMillis = vm.financeIntervalUnix.value!!.first
                )
            else
                buttonClickListener!!.onAddFinanceButtonClick(
                    financeCategoryState = vm.financeCategoryState.value!!,
                    dayInMillis = vm.currentDayInSeconds.value!! * 1000
                )
        }
    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({
            vm.categoriesWithFinancesList.observe(this) { categoryList ->
                if (categoryList.isNotEmpty()) {
                    usedFinanceCategoryAdapter.updateUsedFinanceCategoryList(usedFinanceCategoryList = categoryList, currency = vm.currency.value!!)
                    vm.setFinanceListState(ListState.RECEIVED)
                } else {
                    vm.setFinanceListState(ListState.EMPTY)
                }
                financeProgressBar.setCategoryList(categoryList = categoryList)
            }
        }, 400)
    }

    private fun showFinanceIntervalDialog(
        settingsList: List<String>,
        selectedSetting: Int,
        onSettingsClickListener: SettingsAdapter.OnClickListener
    ) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_setting)
        dialog.findViewById<TextView>(R.id.settingTitleTextView)!!.text =
            getString(R.string.select_interval)
        val settingsRecyclerView = dialog.findViewById<RecyclerView>(R.id.settingsRecyclerView)!!
        settingsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        val onSettingsSelectListener = object : SettingsAdapter.OnSelectListener {
            override fun onSelect() {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            vm.onIntervalDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onIntervalDialogClose()
        }

        settingsRecyclerView.adapter = SettingsAdapter(
            settingsList = settingsList,
            selectedSetting = selectedSetting,
            settingsClickListener = onSettingsClickListener,
            settingsSelectListener = onSettingsSelectListener
        )
        dialog.show()
    }

    private fun showCurrencyDialog(
        settingsList: List<String>,
        selectedSetting: Int,
        onSettingsClickListener: SettingsAdapter.OnClickListener
    ) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_setting)
        dialog.findViewById<TextView>(R.id.settingTitleTextView)!!.text =
            getString(R.string.select_currency)
        val settingsRecyclerView = dialog.findViewById<RecyclerView>(R.id.settingsRecyclerView)!!
        settingsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        val onSettingsSelectListener = object : SettingsAdapter.OnSelectListener {
            override fun onSelect() {
                dialog.dismiss()
            }
        }

        dialog.setOnDismissListener {
            vm.onCurrencyDialogClose()
        }
        dialog.setOnCancelListener {
            vm.onCurrencyDialogClose()
        }

        settingsRecyclerView.adapter = SettingsAdapter(
            settingsList = settingsList,
            selectedSetting = selectedSetting,
            settingsClickListener = onSettingsClickListener,
            settingsSelectListener = onSettingsSelectListener
        )
        dialog.show()
    }
}