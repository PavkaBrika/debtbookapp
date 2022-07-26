package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock

class GetHumanSumDebtUseCaseTest {

    val humanRepository = mock<HumanRepository>()

    @Test
    fun `should return the same data as in repository`() {

        Mockito.`when`(humanRepository.getHumanSumDebt(1)).thenReturn(2.0)

        val useCase = GetHumanSumDebtUseCase(humanRepository = humanRepository)
        val actual = useCase.execute(1)
        val expected = 2.0

        Assertions.assertEquals(expected, actual)
    }
}