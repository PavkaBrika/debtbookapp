package com.breckneck.deptbook.domain.usecase.Debt

class CheckEditTextIsEmpty {

    fun execute(text: String): Boolean {
        val arr = text.split("")
        var isEmpty = true
        for (arg in arr) {
            if (arg == "")
                isEmpty = true
            if ((arg != " ")&&(arg != "")) {
                isEmpty = false
                break
            }
        }
        if (isEmpty || text == "") {
            isEmpty = true
        }

        return isEmpty
    }
}