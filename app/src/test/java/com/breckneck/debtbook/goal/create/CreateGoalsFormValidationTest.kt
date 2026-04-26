package com.breckneck.debtbook.goal.create

import com.breckneck.debtbook.goal.create.model.NameError
import com.breckneck.debtbook.goal.create.model.SavedSumError
import com.breckneck.debtbook.goal.create.model.SumError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreateGoalsFormValidationTest {

    @Test
    fun `validateName empty is EMPTY`() {
        assertEquals(NameError.EMPTY, CreateGoalsFormValidation.validateName(""))
    }

    @Test
    fun `validateName non-empty is null`() {
        assertNull(CreateGoalsFormValidation.validateName("Goal"))
    }

    @Test
    fun `validateSum empty is INVALID`() {
        val (value, err) = CreateGoalsFormValidation.validateSum("")
        assertNull(value)
        assertEquals(SumError.INVALID, err)
    }

    @Test
    fun `validateSum non-numeric is INVALID`() {
        val (value, err) = CreateGoalsFormValidation.validateSum("abc")
        assertNull(value)
        assertEquals(SumError.INVALID, err)
    }

    @Test
    fun `validateSum negative is NEGATIVE`() {
        val (value, err) = CreateGoalsFormValidation.validateSum("-0.01")
        assertEquals(-0.01, value!!, 0.0)
        assertEquals(SumError.NEGATIVE, err)
    }

    @Test
    fun `validateSum zero is ZERO`() {
        val (value, err) = CreateGoalsFormValidation.validateSum("0")
        assertEquals(0.0, value!!, 0.0)
        assertEquals(SumError.ZERO, err)
    }

    @Test
    fun `validateSum positive is valid`() {
        val (value, err) = CreateGoalsFormValidation.validateSum("1500.50")
        assertEquals(1500.50, value!!, 0.0)
        assertNull(err)
    }

    @Test
    fun `validateSavedSum empty is zero without error`() {
        val (value, err) = CreateGoalsFormValidation.validateSavedSum("", sumDouble = 100.0)
        assertEquals(0.0, value!!, 0.0)
        assertNull(err)
    }

    @Test
    fun `validateSavedSum invalid text`() {
        val (value, err) = CreateGoalsFormValidation.validateSavedSum("x", sumDouble = 100.0)
        assertEquals(0.0, value!!, 0.0)
        assertEquals(SavedSumError.INVALID, err)
    }

    @Test
    fun `validateSavedSum greater or equal goal sum is error`() {
        val (_, errEq) = CreateGoalsFormValidation.validateSavedSum("100", sumDouble = 100.0)
        assertEquals(SavedSumError.GREATER_THAN_SUM, errEq)

        val (_, errGt) = CreateGoalsFormValidation.validateSavedSum("101", sumDouble = 100.0)
        assertEquals(SavedSumError.GREATER_THAN_SUM, errGt)
    }

    @Test
    fun `validateSavedSum below goal sum is valid`() {
        val (value, err) = CreateGoalsFormValidation.validateSavedSum("50", sumDouble = 100.0)
        assertEquals(50.0, value!!, 0.0)
        assertNull(err)
    }

    @Test
    fun `validateSavedSum when goal sum invalid still parses saved`() {
        val (value, err) = CreateGoalsFormValidation.validateSavedSum("10", sumDouble = null)
        assertEquals(10.0, value!!, 0.0)
        assertNull(err)
    }
}
