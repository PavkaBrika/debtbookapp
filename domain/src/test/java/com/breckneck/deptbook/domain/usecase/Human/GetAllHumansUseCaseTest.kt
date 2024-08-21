package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetAllHumansUseCaseTest {

    val humanRepository = mock<HumanRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(humanRepository)
    }

    @Test
    fun `should return the same data as in repository`() {
        val testGetAllHumansData = listOf(HumanDomain(id = 0, name = "Hello", sumDebt = 100.0, currency = "USD"))
        `when`(humanRepository.getAllHumans()).thenReturn(testGetAllHumansData)

        val useCase = GetAllHumansUseCase(humanRepository = humanRepository)
        val actual = useCase.execute()
        val expected = listOf(HumanDomain(id = 0, name = "Hello", sumDebt = 100.0, currency = "USD"))
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return the same list as in repository`() {
        val testGetAllHumansData = listOf(
            HumanDomain(id = 0, name = "Hello", sumDebt = 100.0, currency = "USD"),
            HumanDomain(id = 1, name = "World", sumDebt = 200.0, currency = "RUB"),
            HumanDomain(id = 2, name = "Pavel", sumDebt = 300.0, currency = "EUR")
            )
        `when`(humanRepository.getAllHumans()).thenReturn(testGetAllHumansData)

        val useCase = GetAllHumansUseCase(humanRepository = humanRepository)
        val actual = useCase.execute()
        val expected = listOf(
            HumanDomain(id = 0, name = "Hello", sumDebt = 100.0, currency = "USD"),
            HumanDomain(id = 1, name = "World", sumDebt = 200.0, currency = "RUB"),
            HumanDomain(id = 2, name = "Pavel", sumDebt = 300.0, currency = "EUR")
        )
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should return the different list as in repository`() {
        val testGetAllHumansData = listOf(
            HumanDomain(id = 1, name = "Hello", sumDebt = 100.0, currency = "USD"),
            HumanDomain(id = 1, name = "World", sumDebt = 200.0, currency = "RUB"),
            HumanDomain(id = 1, name = "Pavel", sumDebt = 300.0, currency = "EUR")
        )
        `when`(humanRepository.getAllHumans()).thenReturn(testGetAllHumansData)

        val useCase = GetAllHumansUseCase(humanRepository = humanRepository)
        val actual = useCase.execute()
        val expected = listOf(
            HumanDomain(id = 0, name = "Hello", sumDebt = 100.0, currency = "USD"),
            HumanDomain(id = 1, name = "World", sumDebt = 200.0, currency = "RUB"),
            HumanDomain(id = 2, name = "Pavel", sumDebt = 300.0, currency = "EUR")
        )
        Assertions.assertNotEquals(expected, actual)
    }
}