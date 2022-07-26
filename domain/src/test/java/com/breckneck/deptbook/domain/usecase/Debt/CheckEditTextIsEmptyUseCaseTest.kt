package com.breckneck.deptbook.domain.usecase.Debt

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CheckEditTextIsEmptyUseCaseTest {

    @Test
    fun `should return true because string consist of space`() {

        val useCase = CheckEditTextIsEmptyUseCase()
        val actual = useCase.execute("")
        val expected = true

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return true because string consist of spaces`() {

        val useCase = CheckEditTextIsEmptyUseCase()
        val actual = useCase.execute("  ")
        val expected = true

        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return false because string consist of letters`() {

        val useCase = CheckEditTextIsEmptyUseCase()
        val actual = useCase.execute("qwe")
        val expected = false

        Assertions.assertEquals(expected, actual)
    }
}