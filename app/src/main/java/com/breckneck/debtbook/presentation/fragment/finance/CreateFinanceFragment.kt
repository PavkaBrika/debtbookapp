package com.breckneck.debtbook.presentation.fragment.finance

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.FinanceCategoryAdapter
import com.breckneck.debtbook.presentation.customview.CustomSwitchView
import com.breckneck.debtbook.presentation.viewmodel.CreateFinanceViewModel
import com.breckneck.deptbook.domain.model.Finance
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class CreateFinanceFragment : Fragment() {

    private val vm by viewModel<CreateFinanceViewModel>()

    interface OnClickListener {
        fun onBackButtonClick()

        fun onAddCategoryButtonClick()
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
        setFragmentResultListener("requestKey2") { requestKey, bundle ->
            if (bundle.getBoolean("isListModified"))
                vm.getAllFinanceCategories()
        }
        return inflater.inflate(R.layout.fragment_create_finance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isRevenue = arguments?.getBoolean("isRevenue")
        val dayInMillis = arguments?.getLong("dayInMillis")
        if (vm.date.value == null) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dayInMillis!!
            vm.setCurrentDate(calendar.time)
        }

        val currencyNames = listOf(getString(R.string.usd), getString(R.string.eur), getString(R.string.rub),
            getString(R.string.byn), getString(R.string.uah), getString(R.string.kzt),
            getString(R.string.jpy), getString(R.string.gpb), getString(R.string.aud),
            getString(R.string.cad), getString(R.string.chf), getString(R.string.cny),
            getString(R.string.sek), getString(R.string.mxn))

        val sdf = SimpleDateFormat("d MMM yyyy")

        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        val financeSumEditText: EditText = view.findViewById(R.id.financeSumEditText)
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
        vm.date.observe(viewLifecycleOwner) { date ->
            financeDateTextView.text = "${sdf.format(date)} ${getString(R.string.year)}"
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
            calendar.timeInMillis = dayInMillis!!
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
                    onClickListener!!.onAddCategoryButtonClick()
                }
            }

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        vm.financeCategoryList.observe(viewLifecycleOwner) { financeCategoryList ->
            categoryRecyclerView.adapter = FinanceCategoryAdapter(
                financeCategoryList = financeCategoryList,
                onFinanceCategoryClickListener = onFinanceCategoryClickListener
            )
        }

        val financeSumTextInput: TextInputLayout = view.findViewById(R.id.financeSumTextInput)
        val financeInfoEditText: EditText = view.findViewById(R.id.financeInfoEditText)
        fun isAllFieldsFilledRight(): Boolean {
            var isFilledRight = true

            if (financeSumEditText.text.toString().trim().isEmpty()) {
                financeSumTextInput.error = getString(R.string.youmustentername)
                isFilledRight = false
            } else {
                financeSumTextInput.error = ""
                try {
                    if (financeSumEditText.text.toString().toDouble() == 0.0) {
                        financeSumTextInput.error = getString(R.string.zerodebt)
                        isFilledRight = false
                    } else
                        financeSumTextInput.error = ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    financeSumTextInput.error = getString(R.string.something_went_wrong)
                }
            }

            if (vm.checkedFinanceCategory.value == null) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.you_must_select_category), Toast.LENGTH_SHORT
                ).show()
                isFilledRight = false
            }

            return isFilledRight
        }

        val customSwitch: CustomSwitchView = view.findViewById(R.id.customSwitch)
        customSwitch.setChecked(isRevenue!!)
        val setFinanceButton: FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setFinanceButton.setOnClickListener {
            if (isAllFieldsFilledRight()) {
                vm.setFinance(
                    Finance(
                        sum = financeSumEditText.text.toString().toDouble(),
                        isRevenue = customSwitch.isChecked(),
                        info = financeInfoEditText.text.toString(),
                        financeCategoryId = vm.checkedFinanceCategory.value!!.id,
                        date = vm.date.value!!
                    )
                )
                setFragmentResult("requestKey", bundleOf("isListModified" to true))
                onClickListener!!.onBackButtonClick()
            }
        }
    }
}












