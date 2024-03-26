package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.CategoryColorAdapter
import com.breckneck.debtbook.adapter.CategoryImageAdapter
import com.breckneck.debtbook.presentation.viewmodel.CreateFinanceCategoryFragmentViewModel
import com.breckneck.deptbook.domain.util.categoryColorList
import com.breckneck.deptbook.domain.util.categoryImageList
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateFinanceCategoryFragment: Fragment() {

    private val vm by viewModel<CreateFinanceCategoryFragmentViewModel>()

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

        val backButtonImageView: ImageView = view.findViewById(R.id.backButton)
        backButtonImageView.setOnClickListener {
            onClickListener!!.onBackButtonClick()
        }

        val categoryImageRecyclerView: RecyclerView = view.findViewById(R.id.categoryImageRecyclerView)
        val categoryImageClickListener = object: CategoryImageAdapter.OnCategoryImageClickListener {
            override fun onClick(position: Int, categoryImage: Int) {
                vm.setCheckedImage(image =  categoryImage)
                vm.setCheckedImagePosition(position = position)
            }
        }
        categoryImageRecyclerView.adapter = CategoryImageAdapter(categoryImageList, vm.checkedImagePosition.value, categoryImageClickListener)


        val categoryColorRecyclerView: RecyclerView = view.findViewById(R.id.categoryColorRecyclerView)
        val categoryColorClickListener = object: CategoryColorAdapter.OnCategoryColorClickListener {
            override fun onClick(position: Int, categoryColor: String) {
                vm.setCheckedColor(color = categoryColor)
                vm.setCheckedColorPosition(position = position)
            }
        }
        categoryColorRecyclerView.adapter = CategoryColorAdapter(categoryColorList, vm.checkedColorPosition.value, categoryColorClickListener)


    }


}








