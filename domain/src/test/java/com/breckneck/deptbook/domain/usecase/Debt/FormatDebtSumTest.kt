package com.breckneck.deptbook.domain.usecase.Debt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class FormatDebtSumTest {

    val testDataList = listOf(
        0.0,
        -0.0,
        0.0,
        -0.005,
        -0.05,
        -0.5,
        -0.55,
        -0.5555,
        10000000.0,
        10000000.9999,
    )

    val formattedExpectedList = listOf(
        "0",
        "-0",
        "0",
        "-0,01",
        "-0,05",
        "-0,5",
        "-0,55",
        "-0,56",
        "10 000 000",
        "10 000 001",
    )

    @Test
    fun `should return correct formatted by hand value`() {
        val useCase = FormatDebtSum()
        for (i in testDataList.indices) {
            val actual = useCase.execute(testDataList[i])
            val expected = formattedExpectedList[i]
            Assertions.assertEquals(expected, actual)
        }
    }
}