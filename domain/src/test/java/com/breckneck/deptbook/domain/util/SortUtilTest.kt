package com.breckneck.deptbook.domain.util

import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.model.HumanDomain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Locale

class SortUtilTest {

    private lateinit var savedLocale: Locale

    @BeforeEach
    fun setUp() {
        savedLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
    }

    @AfterEach
    fun tearDown() {
        Locale.setDefault(savedLocale)
    }

    // ===================== sortHumans =====================

    private val humans = listOf(
        HumanDomain(id = 3, name = "Charlie", sumDebt = 300.0, currency = "USD"),
        HumanDomain(id = 1, name = "Alice", sumDebt = -100.0, currency = "USD"),
        HumanDomain(id = 2, name = "Bob", sumDebt = 150.0, currency = "EUR")
    )

    @Test
    fun `sortHumans by Date ascending should sort by id ascending`() {
        val result = sortHumans(humans, Pair(HumanOrderAttribute.Date, false))
        Assertions.assertEquals(listOf(1, 2, 3), result.map { it.id })
    }

    @Test
    fun `sortHumans by Date descending should sort by id descending`() {
        val result = sortHumans(humans, Pair(HumanOrderAttribute.Date, true))
        Assertions.assertEquals(listOf(3, 2, 1), result.map { it.id })
    }

    @Test
    fun `sortHumans by Sum ascending should sort by sumDebt ascending`() {
        val result = sortHumans(humans, Pair(HumanOrderAttribute.Sum, true))
        Assertions.assertEquals(listOf(-100.0, 150.0, 300.0), result.map { it.sumDebt })
    }

    @Test
    fun `sortHumans by Sum descending should sort by sumDebt descending`() {
        val result = sortHumans(humans, Pair(HumanOrderAttribute.Sum, false))
        Assertions.assertEquals(listOf(300.0, 150.0, -100.0), result.map { it.sumDebt })
    }

    @Test
    fun `sortHumans should return empty list for empty input`() {
        val result = sortHumans(emptyList(), Pair(HumanOrderAttribute.Date, false))
        Assertions.assertTrue(result.isEmpty())
    }

    @Test
    fun `sortHumans should handle single element`() {
        val single = listOf(HumanDomain(id = 1, name = "Alice", sumDebt = 100.0, currency = "USD"))
        val result = sortHumans(single, Pair(HumanOrderAttribute.Sum, true))
        Assertions.assertEquals(single, result)
    }

    // ===================== sortDebts =====================

    private val debts = listOf(
        DebtDomain(id = 3, sum = 300.0, idHuman = 1, info = null, date = "15 Mar 2024"),
        DebtDomain(id = 1, sum = -100.0, idHuman = 1, info = null, date = "1 Jan 2024"),
        DebtDomain(id = 2, sum = 50.0, idHuman = 1, info = null, date = "10 Feb 2024")
    )

    @Test
    fun `sortDebts by CreationDate ascending should sort by id ascending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.CreationDate, false))
        Assertions.assertEquals(listOf(1, 2, 3), result.map { it.id })
    }

    @Test
    fun `sortDebts by CreationDate descending should sort by id descending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.CreationDate, true))
        Assertions.assertEquals(listOf(3, 2, 1), result.map { it.id })
    }

    @Test
    fun `sortDebts by Sum ascending should sort by sum ascending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.Sum, true))
        Assertions.assertEquals(listOf(-100.0, 50.0, 300.0), result.map { it.sum })
    }

    @Test
    fun `sortDebts by Sum descending should sort by sum descending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.Sum, false))
        Assertions.assertEquals(listOf(300.0, 50.0, -100.0), result.map { it.sum })
    }

    @Test
    fun `sortDebts by Date ascending should sort by date ascending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.Date, false))
        Assertions.assertEquals(listOf("1 Jan 2024", "10 Feb 2024", "15 Mar 2024"), result.map { it.date })
    }

    @Test
    fun `sortDebts by Date descending should sort by date descending`() {
        val result = sortDebts(debts, Pair(DebtOrderAttribute.Date, true))
        Assertions.assertEquals(listOf("15 Mar 2024", "10 Feb 2024", "1 Jan 2024"), result.map { it.date })
    }

    @Test
    fun `sortDebts should return empty list for empty input`() {
        val result = sortDebts(emptyList(), Pair(DebtOrderAttribute.Sum, false))
        Assertions.assertTrue(result.isEmpty())
    }
}
