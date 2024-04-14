package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.CategoryColorAdapter
import com.breckneck.debtbook.adapter.CategoryImageAdapter
import com.breckneck.debtbook.presentation.viewmodel.CreateFinanceCategoryViewModel
import com.breckneck.deptbook.domain.model.FinanceCategory
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.categoryColorList
import com.breckneck.deptbook.domain.util.categoryImageList
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateFinanceCategoryFragment : Fragment() {

    private val vm by viewModel<CreateFinanceCategoryViewModel>()

    interface OnClickListener {
        fun onBackButtonClick()
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
        return inflater.inflate(R.layout.fragment_create_finance_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (vm.financeCategoryState.value == null)
            vm.setFinanceCategoryState(
                financeCategoryState =
                when (arguments?.getString("categoryState").toString()) {
                    FinanceCategoryState.EXPENSE.toString() -> FinanceCategoryState.EXPENSE
                    FinanceCategoryState.INCOME.toString() -> FinanceCategoryState.INCOME
                    else -> FinanceCategoryState.EXPENSE
                }
            )

        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        val collaps: CollapsingToolbarLayout = view.findViewById(R.id.collaps)
        collaps.apply {
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleTypeface(Typeface.DEFAULT_BOLD)
        }

        val categoryImageRecyclerView: RecyclerView =
            view.findViewById(R.id.categoryImageRecyclerView)
        val categoryImageClickListener =
            object : CategoryImageAdapter.OnCategoryImageClickListener {
                override fun onClick(position: Int, categoryImage: Int) {
                    vm.setCheckedImage(image = categoryImage)
                    vm.setCheckedImagePosition(position = position)
                }
            }
        categoryImageRecyclerView.adapter = CategoryImageAdapter(
            categoryImageList,
            vm.checkedImagePosition.value,
            categoryImageClickListener
        )

        val categoryColorRecyclerView: RecyclerView =
            view.findViewById(R.id.categoryColorRecyclerView)
        val categoryColorClickListener =
            object : CategoryColorAdapter.OnCategoryColorClickListener {
                override fun onClick(position: Int, categoryColor: String) {
                    vm.setCheckedColor(color = categoryColor)
                    vm.setCheckedColorPosition(position = position)
                }
            }
        categoryColorRecyclerView.adapter = CategoryColorAdapter(
            categoryColorList,
            vm.checkedColorPosition.value,
            categoryColorClickListener
        )

        val financeNameTextInput: TextInputLayout = view.findViewById(R.id.financeNameTextInput)
        val financeNameEditText: EditText = view.findViewById(R.id.financeNameEditText)
        fun isAllFieldsFilledRight(): Boolean {
            var isFilledRight = true
            if (financeNameEditText.text.toString().trim().isEmpty()) {
                financeNameTextInput.error = getString(R.string.youmustentername)
                isFilledRight = false
            } else
                financeNameTextInput.error = ""

            if (vm.checkedColor.value == null) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.you_must_select_color), Toast.LENGTH_SHORT
                ).show()
                isFilledRight = false
            }

            if (vm.checkedImage.value == null) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.you_must_select_image), Toast.LENGTH_SHORT
                ).show()
                isFilledRight = false
            }

            return isFilledRight
        }

        val setCategoryButton: FloatingActionButton = view.findViewById(R.id.setCategoryButton)
        setCategoryButton.setOnClickListener {
            if (isAllFieldsFilledRight()) {
                vm.setFinanceCategory(
                    FinanceCategory(
                        name = financeNameEditText.text.toString(),
                        color = vm.checkedColor.value!!,
                        state = vm.financeCategoryState.value!!,
                        image = vm.checkedImage.value!!
                    )
                )
                setFragmentResult("createFinanceFragmentKey", bundleOf("isListModified" to true, "categoryState" to vm.financeCategoryState.value!!.toString()))
                onClickListener!!.onBackButtonClick()
            }
        }
    }


}








