package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class AdUseCasesTest {

    private val adRepository = mock<AdRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(adRepository)
    }

    // --- GetClicksUseCase ---

    @Test
    fun `GetClicksUseCase should return clicks count from repository`() {
        Mockito.`when`(adRepository.getClicks()).thenReturn(42)
        val result = GetClicksUseCase(adRepository).execute()
        Assertions.assertEquals(42, result)
    }

    @Test
    fun `GetClicksUseCase should return zero when no clicks`() {
        Mockito.`when`(adRepository.getClicks()).thenReturn(0)
        val result = GetClicksUseCase(adRepository).execute()
        Assertions.assertEquals(0, result)
    }

    @Test
    fun `GetClicksUseCase should call getClicks exactly once`() {
        Mockito.`when`(adRepository.getClicks()).thenReturn(0)
        GetClicksUseCase(adRepository).execute()
        verify(adRepository, Mockito.times(1)).getClicks()
    }

    // --- SaveClicksUseCase ---

    @Test
    fun `SaveClicksUseCase should call saveClick with correct value`() {
        SaveClicksUseCase(adRepository).execute(click = 5)
        verify(adRepository).saveClick(click = 5)
    }

    @Test
    fun `SaveClicksUseCase should call saveClick exactly once`() {
        SaveClicksUseCase(adRepository).execute(click = 10)
        verify(adRepository, Mockito.times(1)).saveClick(10)
    }
}
