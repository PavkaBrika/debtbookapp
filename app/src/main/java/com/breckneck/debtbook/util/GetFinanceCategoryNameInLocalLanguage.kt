package com.breckneck.debtbook.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.breckneck.debtbook.R
import com.breckneck.deptbook.domain.util.FinanceCategoryState
import com.breckneck.deptbook.domain.util.incomesCategoryEnglishNameList
import com.breckneck.deptbook.domain.util.revenuesCategoryEnglishNameList

class GetFinanceCategoryNameInLocalLanguage {

    fun execute(financeName: String, state: FinanceCategoryState, context: Context): String {
        when (state) {
            FinanceCategoryState.EXPENSE -> {
                for (i in revenuesCategoryEnglishNameList.indices) {
                    if (financeName == revenuesCategoryEnglishNameList[i]) {
                        when (i) {
                            0 -> {
                                return ContextCompat.getString(context, R.string.health)
                            }
                            1 -> {
                                return ContextCompat.getString(context, R.string.entertainment)
                            }
                            2 -> {
                                return ContextCompat.getString(context, R.string.home)
                            }
                            3 -> {
                                return ContextCompat.getString(context, R.string.education)
                            }
                            4 -> {
                                return ContextCompat.getString(context, R.string.presents)
                            }
                            5 -> {
                                return ContextCompat.getString(context, R.string.food)
                            }
                            6 -> {
                                return ContextCompat.getString(context, R.string.other)
                            }
                            else -> {
                                return financeName
                            }
                        }
                    }
                }
            }
            FinanceCategoryState.INCOME -> {
                for (i in incomesCategoryEnglishNameList.indices) {
                    if (financeName == incomesCategoryEnglishNameList[i]) {
                        when (i) {
                            0 -> {
                                return ContextCompat.getString(context, R.string.salary)
                            }
                            1 -> {
                                return ContextCompat.getString(context, R.string.presents)
                            }
                            2 -> {
                                return ContextCompat.getString(context, R.string.investments)
                            }
                            3 -> {
                                return ContextCompat.getString(context, R.string.other)
                            }
                            else -> {
                                return financeName
                            }
                        }
                    }
                }
            }
        }
        return financeName
    }
}