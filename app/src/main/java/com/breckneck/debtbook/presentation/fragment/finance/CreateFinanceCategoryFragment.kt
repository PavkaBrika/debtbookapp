package com.breckneck.debtbook.presentation.fragment.finance

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.breckneck.debtbook.R
import com.breckneck.debtbook.adapter.CategoryColorAdapter
import com.breckneck.deptbook.domain.util.categoryColorList

class CreateFinanceCategoryFragment: Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

        val categoryColorRecyclerView: RecyclerView = view.findViewById(R.id.categoryColorRecyclerView)
        val categoryColorClickListener = object: CategoryColorAdapter.OnCategoryColorClickListener {
            override fun onCLick(categoryColor: String) {

            }
        }
        categoryColorRecyclerView.adapter = CategoryColorAdapter(categoryColorList, categoryColorClickListener)
    }


}








