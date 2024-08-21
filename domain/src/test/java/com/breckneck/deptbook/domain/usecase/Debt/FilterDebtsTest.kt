package com.breckneck.deptbook.domain.usecase.Debt

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.util.Filter
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FilterDebtsTest {

    val debtList = listOf(
        DebtDomain(id = 0, sum = 100.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = -200.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = 300.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = -400.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = 500.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = -600.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = 0.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = -0.0, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = -0.00000001, idHuman = 0, info = "", date = ""),
        DebtDomain(id = 0, sum = 0.0000000001, idHuman = 0, info = "", date = "")
        )

    @Test
    fun `should return all debts`() {
        val testFilter = Filter.All
        val useCase = FilterDebts()

        val actual = useCase.execute(debtList = debtList, filter = testFilter)
        val expected = debtList
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return negative debts`() {
        val testFilter = Filter.Negative
        val useCase = FilterDebts()

        val actual = useCase.execute(debtList = debtList, filter = testFilter)
        val expected = listOf(
            DebtDomain(id = 0, sum = -200.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = -400.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = -600.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = 0.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = -0.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = -0.00000001, idHuman = 0, info = "", date = "")
        )
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return positive debts`() {
        val testFilter = Filter.Positive
        val useCase = FilterDebts()

        val actual = useCase.execute(debtList = debtList, filter = testFilter)
        val expected = listOf(
            DebtDomain(id = 0, sum = 100.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = 300.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = 500.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = 0.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = -0.0, idHuman = 0, info = "", date = ""),
            DebtDomain(id = 0, sum = 0.0000000001, idHuman = 0, info = "", date = "")
        )
        Assertions.assertEquals(expected, actual)
    }
}