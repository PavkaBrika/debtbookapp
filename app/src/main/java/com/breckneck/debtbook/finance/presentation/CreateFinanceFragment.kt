package com.breckneck.debtbook.finance.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.finance.adapter.FinanceCategoryAdapter
import com.breckneck.debtbook.core.customview.CustomSwitchView
import com.breckneck.debtbook.finance.viewmodel.CreateFinanceViewModel
import com.breckneck.debtbook.finance.util.GetFinanceCategoryNameInLocalLanguage
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.CreateFragmentState
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.SWITCH_STATE_INCOMES
import com.breckneck.deptbook.domain.util.SWITCH_STATE_REVENUES
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DecimalFormat
import java.util.Calendar

class CreateFinanceFragment : Fragment() {

    private val vm by viewModel<CreateFinanceViewModel>()

    interface OnClickListener {
        fun onBackButtonClick()

        fun onAddCategoryButtonClick(financeCategoryState: FinanceCategoryState)

        fun onTickVibration()
    }

    private var onClickListener: OnClickListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        onClickListener = context as OnClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setFragmentResultListener("createFinanceFragmentKey") { requestKey, bundle ->
            when (bundle.getString("categoryState")) {
                FinanceCategoryState.EXPENSE.toString() -> vm.setFinanceCategoryState(
                    financeCategoryState = FinanceCategoryState.EXPENSE
                )

                FinanceCategoryState.INCOME.toString() -> vm.setFinanceCategoryState(
                    financeCategoryState = FinanceCategoryState.INCOME
                )

                else -> {}
            }
            if (bundle.getBoolean("isListModified"))
                vm.getFinanceCategoriesByState()
        }
        return inflater.inflate(R.layout.fragment_create_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (vm.createFragmentState.value == null) {
            when (arguments?.getBoolean("isEditFinance")!!) {
                true -> vm.setCreateFinanceState(createFragmentState = CreateFragmentState.EDIT)
                false -> vm.setCreateFinanceState(createFragmentState = CreateFragmentState.CREATE)
            }
        }

        if (vm.isDeleteCategoryDialogOpened.value == true) {
            showDeleteFinanceDialog(vm.deleteFinanceCategory.value!!)
        }

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val customSwitch: CustomSwitchView = view.findViewById(R.id.customSwitch)
        val financeSumEditText: EditText = view.findViewById(R.id.financeSumEditText)
        val financeSumTextInput: TextInputLayout = view.findViewById(R.id.financeSumTextInput)
        val financeInfoEditText: EditText = view.findViewById(R.id.financeInfoEditText)
        val categoryLinearLayout: LinearLayout = view.findViewById(R.id.categoryLinearLayout)
        vm.createFragmentState.observe(viewLifecycleOwner) { state ->
            when (state) {
                CreateFragmentState.CREATE -> {
                    val calendarCurrentTime = Calendar.getInstance()
                    val calendarFinanceTime = Calendar.getInstance()
                    calendarFinanceTime.timeInMillis = arguments?.getLong("dayInMillis")!!
                    calendarFinanceTime.set(Calendar.HOUR, calendarCurrentTime.get(Calendar.HOUR))
                    calendarFinanceTime.set(
                        Calendar.MINUTE,
                        calendarCurrentTime.get(Calendar.MINUTE)
                    )
                    calendarFinanceTime.set(
                        Calendar.SECOND,
                        calendarCurrentTime.get(Calendar.SECOND)
                    )
                    vm.setDayInMillis(dayInMillis = calendarFinanceTime.timeInMillis)
                    vm.getFinanceCategoriesByState()
                }

                CreateFragmentState.EDIT -> {
                    val decimalFormat = DecimalFormat("#.##")
                    val financeEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        arguments?.getSerializable("financeEdit", Finance::class.java)
                    } else {
                        arguments?.getSerializable("financeEdit") as Finance?
                    }
                    vm.setFinanceEdit(finance = financeEdit!!)
                    vm.setDayInMillis(dayInMillis = financeEdit.date.time)
                    financeSumEditText.setText(decimalFormat.format(financeEdit.sum))
                    financeInfoEditText.setText(financeEdit.info)
                    categoryLinearLayout.visibility = View.GONE
                    customSwitch.visibility = View.GONE
                    collaps.title = getString(R.string.edit)
                }
            }

            vm.setFinanceCategoryState(
                financeCategoryState = when (arguments?.getString("categoryState").toString()) {
                    FinanceCategoryState.EXPENSE.toString() -> FinanceCategoryState.EXPENSE
                    FinanceCategoryState.INCOME.toString() -> FinanceCategoryState.INCOME
                    else -> FinanceCategoryState.EXPENSE
                }
            )

            if (vm.date.value == null) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = vm.dayInMillis.value!!
                vm.setCurrentDate(calendar.time)
            }

            customSwitch.setChecked(
                checked = when (vm.financeCategoryState.value!!) {
                    FinanceCategoryState.EXPENSE -> SWITCH_STATE_REVENUES
                    FinanceCategoryState.INCOME -> SWITCH_STATE_INCOMES
                }
            )
        }

        val currencyNames = resources.getStringArray(R.array.currencies).toList()

        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        financeSumEditText.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(editable: Editable?) {
                    val str = editable.toString()
                    val position = str.indexOf(".")
                    if (position != -1) {
                        val subStr = str.substring(position)
                        val subStrStart = str.substring(0, position)
                        if ((subStr.length > 3) || (subStrStart.length == 0))
                            editable?.delete(editable.length - 1, editable.length)
                    }
                }
            }
        }

        val financeCurrencyTextView: TextView = view.findViewById(R.id.financeCurrencyTextView)
        vm.currency.observe(viewLifecycleOwner) { currency ->
            for (i in currencyNames.indices)
                if (currencyNames[i].contains(currency))
                    financeCurrencyTextView.text = currencyNames[i]
        }

        val financeDateTextView: TextView = view.findViewById(R.id.financeDateTextView)
        vm.dateString.observe(viewLifecycleOwner) { date ->
            financeDateTextView.text = "$date ${getString(R.string.year)}"
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            vm.setCurrentDate(calendar.time)
        }
        financeDateTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = vm.dayInMillis.value!!
            DatePickerDialog(
                view.context, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val onFinanceCategoryClickListener =
            object : FinanceCategoryAdapter.OnFinanceCategoryClickListener {
                override fun onCategoryClick(financeCategory: FinanceCategory) {
                    vm.setCheckedFinanceCategory(financeCategory = financeCategory)
                }

                override fun onAddCategoryClick() {
                    onClickListener!!.onAddCategoryButtonClick(financeCategoryState = vm.financeCategoryState.value!!)
                }

                override fun onCategoryLongClick(financeCategory: FinanceCategory) {
                    showDeleteFinanceDialog(financeCategory = financeCategory)
                }
            }

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        vm.financeCategoryList.observe(viewLifecycleOwner) { financeCategoryList ->
            categoryRecyclerView.adapter = FinanceCategoryAdapter(
                financeCategoryList = financeCategoryList,
                onFinanceCategoryClickListener = onFinanceCategoryClickListener
            )
        }

        vm.financeCategoryState.observe(viewLifecycleOwner) {
            vm.getFinanceCategoriesByState()
        }

        customSwitch.setOnClickListener {
            vm.onChangeFinanceCategoryState()
            onClickListener!!.onTickVibration()
        }

        fun isAllFieldsFilledRight(): Boolean {
            var isFilledRight = true

            if (financeSumEditText.text.toString().trim().isEmpty()) {
                financeSumTextInput.error = getString(R.string.youmustentername)
                isFilledRight = false
            } else {
                financeSumTextInput.error = ""
                try {
                    if (financeSumEditText.text.toString().trim().replace(" ", "").toDouble() == 0.0) {
                        financeSumTextInput.error = getString(R.string.zerodebt)
                        isFilledRight = false
                    } else
                        financeSumTextInput.error = ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    financeSumTextInput.error = getString(R.string.something_went_wrong)
                }
            }

            if (vm.createFragmentState.value == CreateFragmentState.CREATE) {
                if (vm.checkedFinanceCategory.value == null) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.you_must_select_category), Toast.LENGTH_SHORT
                    ).show()
                    isFilledRight = false
                }
            }

            return isFilledRight
        }

        val setFinanceButton: FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setFinanceButton.setOnClickListener {
            if (isAllFieldsFilledRight()) {
                when (vm.createFragmentState.value!!) {
                    CreateFragmentState.CREATE -> {
                        vm.setFinance(
                            Finance(
                                sum = financeSumEditText.text.toString().toDouble(),
                                info = financeInfoEditText.text.toString(),
                                financeCategoryId = vm.checkedFinanceCategory.value!!.id,
                                date = vm.date.value!!
                            )
                        )
                    }

                    CreateFragmentState.EDIT -> {
                        vm.editFinance(
                            Finance(
                                id = vm.financeEdit.value!!.id,
                                sum = financeSumEditText.text.toString().toDouble(),
                                info = financeInfoEditText.text.toString(),
                                financeCategoryId = vm.financeEdit.value!!.financeCategoryId,
                                date = vm.date.value!!
                            )
                        )
                        setFragmentResult("financeDetailsKey", bundleOf("isListModified" to true))
                    }
                }
                setFragmentResult("financeFragmentKey", bundleOf("isListModified" to true))
                onClickListener!!.onBackButtonClick()
            }
        }
    }

    private fun showDeleteFinanceDialog(financeCategory: FinanceCategory) {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(R.layout.dialog_are_you_sure)
        vm.setDeleteCategoryDialogOpened(isOpened = true)
        vm.setDeleteCategory(financeCategory = financeCategory)

        dialog.findViewById<Button>(R.id.okButton)!!.setOnClickListener {
            vm.deleteFinanceCategory()
            dialog.dismiss()
        }

        val financeName = GetFinanceCategoryNameInLocalLanguage().execute(
            financeName = financeCategory.name,
            state = financeCategory.state,
            context = requireContext()
        )

        dialog.findViewById<TextView>(R.id.dialogTitle)!!.text = "${getString(R.string.delete_category)} $financeName?"

        dialog.findViewById<TextView>(R.id.dialogMessage)!!.text =
            getString(R.string.this_action_will_delete_the_category_and_all_data_associated_with_it)

        dialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            vm.setDeleteCategoryDialogOpened(isOpened = false)
        }
        dialog.setOnCancelListener {
            vm.setDeleteCategoryDialogOpened(isOpened = false)
        }

        dialog.show()
    }
}












