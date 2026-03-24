package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

class HumanUseCasesTest {

    private val humanRepository = mock<HumanRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(humanRepository)
    }

    // --- SetHumanUseCase ---

    @Test
    fun `SetHumanUseCase should call insertHuman with correct HumanDomain`() {
        val useCase = SetHumanUseCase(humanRepository)
        useCase.execute(name = "Alice", sumDebt = 150.0, currency = "USD")

        val captor = argumentCaptor<HumanDomain>()
        verify(humanRepository).insertHuman(captor.capture())
        val inserted = captor.firstValue
        Assertions.assertEquals("Alice", inserted.name)
        Assertions.assertEquals(150.0, inserted.sumDebt)
        Assertions.assertEquals("USD", inserted.currency)
        Assertions.assertEquals(0, inserted.id)
    }

    @Test
    fun `SetHumanUseCase should call insertHuman exactly once`() {
        val useCase = SetHumanUseCase(humanRepository)
        useCase.execute("Bob", 0.0, "EUR")
        verify(humanRepository, Mockito.times(1)).insertHuman(any())
    }

    // --- DeleteHumanUseCase ---

    @Test
    fun `DeleteHumanUseCase should call deleteHuman with correct id`() {
        val useCase = DeleteHumanUseCase(humanRepository)
        useCase.execute(id = 42)
        verify(humanRepository).deleteHuman(42)
    }

    @Test
    fun `DeleteHumanUseCase should call deleteHuman exactly once`() {
        val useCase = DeleteHumanUseCase(humanRepository)
        useCase.execute(id = 1)
        verify(humanRepository, Mockito.times(1)).deleteHuman(1)
    }

    // --- GetHumanSumDebtUseCase ---

    @Test
    fun `GetHumanSumDebtUseCase should return sum from repository`() {
        Mockito.`when`(humanRepository.getHumanSumDebt(7)).thenReturn(500.0)
        val useCase = GetHumanSumDebtUseCase(humanRepository)
        val result = useCase.execute(humanId = 7)
        Assertions.assertEquals(500.0, result)
    }

    @Test
    fun `GetHumanSumDebtUseCase should return negative sum`() {
        Mockito.`when`(humanRepository.getHumanSumDebt(3)).thenReturn(-200.0)
        val useCase = GetHumanSumDebtUseCase(humanRepository)
        val result = useCase.execute(humanId = 3)
        Assertions.assertEquals(-200.0, result)
    }

    @Test
    fun `GetHumanSumDebtUseCase should return zero sum`() {
        Mockito.`when`(humanRepository.getHumanSumDebt(0)).thenReturn(0.0)
        val useCase = GetHumanSumDebtUseCase(humanRepository)
        val result = useCase.execute(humanId = 0)
        Assertions.assertEquals(0.0, result)
    }

    // --- GetLastHumanIdUseCase ---

    @Test
    fun `GetLastHumanIdUseCase should return id from repository`() {
        Mockito.`when`(humanRepository.getLastHumanId()).thenReturn(15)
        val useCase = GetLastHumanIdUseCase(humanRepository)
        val result = useCase.exectute()
        Assertions.assertEquals(15, result)
    }

    @Test
    fun `GetLastHumanIdUseCase should call getLastHumanId once`() {
        Mockito.`when`(humanRepository.getLastHumanId()).thenReturn(0)
        val useCase = GetLastHumanIdUseCase(humanRepository)
        useCase.exectute()
        verify(humanRepository, Mockito.times(1)).getLastHumanId()
    }

    // --- AddSumUseCase ---

    @Test
    fun `AddSumUseCase should call addSum with correct params`() {
        val useCase = AddSumUseCase(humanRepository)
        useCase.execute(humanId = 5, sum = 300.0)
        verify(humanRepository).addSum(humanId = 5, sum = 300.0)
    }

    @Test
    fun `AddSumUseCase should call addSum with negative sum`() {
        val useCase = AddSumUseCase(humanRepository)
        useCase.execute(humanId = 2, sum = -100.0)
        verify(humanRepository).addSum(humanId = 2, sum = -100.0)
    }

    // --- UpdateHuman ---

    @Test
    fun `UpdateHuman should call updateHuman with provided HumanDomain`() {
        val human = HumanDomain(id = 1, name = "Carol", sumDebt = 75.0, currency = "RUB")
        val useCase = UpdateHuman(humanRepository)
        useCase.execute(humanDomain = human)
        verify(humanRepository).updateHuman(human)
    }

    @Test
    fun `UpdateHuman should call updateHuman exactly once`() {
        val human = HumanDomain(id = 2, name = "Dave", sumDebt = 0.0, currency = "USD")
        val useCase = UpdateHuman(humanRepository)
        useCase.execute(humanDomain = human)
        verify(humanRepository, Mockito.times(1)).updateHuman(any())
    }

    // --- ReplaceAllHumans ---

    @Test
    fun `ReplaceAllHumans should call replaceAllHumans with provided list`() {
        val humans = listOf(
            HumanDomain(id = 1, name = "Alice", sumDebt = 100.0, currency = "USD"),
            HumanDomain(id = 2, name = "Bob", sumDebt = -50.0, currency = "EUR")
        )
        val useCase = ReplaceAllHumans(humanRepository)
        useCase.execute(humanList = humans)
        verify(humanRepository).replaceAllHumans(humans)
    }

    @Test
    fun `ReplaceAllHumans should call replaceAllHumans with empty list`() {
        val useCase = ReplaceAllHumans(humanRepository)
        useCase.execute(humanList = emptyList())
        verify(humanRepository).replaceAllHumans(emptyList())
    }
}
