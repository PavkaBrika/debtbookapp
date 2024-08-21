package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.text.DecimalFormat

class GetAllDebtsSumUseCaseTest {
    val decimalFormat = DecimalFormat("###,###,###.##")
    val humanRepository = mock<HumanRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(humanRepository)
    }

    @Test
    fun `FIRST CURRENCY should return the same data as in repository`() {
        val currencyTestData = "RUB"
        val debtsTestData = listOf(100.0, -100.0)
        `when`(humanRepository.getAllDebtsSum(currencyTestData)).thenReturn(debtsTestData)

        val useCase = GetAllDebtsSumUseCase(humanRepository = humanRepository)
        val actual = useCase.execute(currencyTestData, "EUR")
        val expected = Pair(
            "+${decimalFormat.format(debtsTestData[0])} $currencyTestData",
            "${decimalFormat.format(debtsTestData[1])} $currencyTestData"
        )
        Assertions.assertEquals(expected, actual)
        Mockito.verify(humanRepository, Mockito.times(2)).getAllDebtsSum(currency = anyString())
    }

    @Test
    fun `SECOND CURRENCY should return the same data as in repository`() {
        val currencyTestData = "EUR"
        val debtsTestData = listOf(100.0, -100.0)
        `when`(humanRepository.getAllDebtsSum(currencyTestData)).thenReturn(debtsTestData)
        val useCase = GetAllDebtsSumUseCase(humanRepository = humanRepository)
        val actual = useCase.execute("RUB", currencyTestData)
        val expected = Pair(
            "+${decimalFormat.format(debtsTestData[0])} $currencyTestData",
            "${decimalFormat.format(debtsTestData[1])} $currencyTestData"
        )
        Assertions.assertEquals(expected, actual)
        Mockito.verify(humanRepository, Mockito.times(2)).getAllDebtsSum(currency = anyString())
    }

    @Test
    fun `BOTH CURRENCIES should return the same data as in repository`() {
        val firstCurrencyTestData = "EUR"
        val secondCurrencyTestData = "RUB"
        val firstCurrencyDebtsTestData = listOf(100.0, -100.0)
        val secondCurrencyDebtsTestData = listOf(200.0, -200.0)
        `when`(humanRepository.getAllDebtsSum(firstCurrencyTestData)).thenReturn(
            firstCurrencyDebtsTestData
        )
        `when`(humanRepository.getAllDebtsSum(secondCurrencyTestData)).thenReturn(
            secondCurrencyDebtsTestData
        )

        val useCase = GetAllDebtsSumUseCase(humanRepository = humanRepository)
        val actual =
            useCase.execute(firstCurrencyTestData, secondCurrencyTestData)
        val expected = Pair(
            "+${decimalFormat.format(firstCurrencyDebtsTestData[0])} $firstCurrencyTestData\n+${
                decimalFormat.format(
                    secondCurrencyDebtsTestData[0]
                )
            } $secondCurrencyTestData",
            "${decimalFormat.format(firstCurrencyDebtsTestData[1])} $firstCurrencyTestData\n${
                decimalFormat.format(
                    secondCurrencyDebtsTestData[1]
                )
            } $secondCurrencyTestData"
        )
        Assertions.assertEquals(expected, actual)
        Mockito.verify(humanRepository, Mockito.times(2)).getAllDebtsSum(currency = anyString())
    }
}