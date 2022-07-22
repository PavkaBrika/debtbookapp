package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock


class GetLastHumanIdUseCaseTest {

    val humanRepository = mock<HumanRepository>()

    @Test
    fun `should return the same data as in repository`() {

        Mockito.`when`(humanRepository.getLastHumanId()).thenReturn(2)

        val useCase = GetLastHumanIdUseCase(humanRepository = humanRepository)
        val actual = useCase.exectute()
        val expected = 2

        Assertions.assertEquals(expected, actual)
    }
}