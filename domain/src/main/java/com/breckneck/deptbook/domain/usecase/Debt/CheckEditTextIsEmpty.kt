package com.breckneck.deptbook.domain.usecase.Debt

class CheckEditTextIsEmpty {

    fun execute(text: String): Boolean {
        val arr = text.split("")
        var isEmpty = true
        for (arg in arr) {
            if (arg == "")
                isEmpty = true
            if (arg != " ") {
                isEmpty = false
                break
            }
        }
        return isEmpty
    }
}