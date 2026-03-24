package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.repository.SettingsRepository
import com.breckneck.deptbook.domain.util.DebtOrderAttribute
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor

class SettingsUseCasesTest {

    private val settingsRepository = mock<SettingsRepository>()

    @AfterEach
    fun tearDown() {
        Mockito.reset(settingsRepository)
    }

    // --- GetFirstMainCurrency / SetFirstMainCurrency ---

    @Test
    fun `GetFirstMainCurrency should return currency from repository`() {
        Mockito.`when`(settingsRepository.getFirstMainCurrency()).thenReturn("USD")
        val result = GetFirstMainCurrency(settingsRepository).execute()
        Assertions.assertEquals("USD", result)
    }

    @Test
    fun `SetFirstMainCurrency should call setFirstMainCurrency with correct value`() {
        SetFirstMainCurrency(settingsRepository).execute(currency = "EUR")
        verify(settingsRepository).setFirstMainCurrency("EUR")
    }

    // --- GetSecondMainCurrency / SetSecondMainCurrency ---

    @Test
    fun `GetSecondMainCurrency should return currency from repository`() {
        Mockito.`when`(settingsRepository.getSecondMainCurrency()).thenReturn("RUB")
        val result = GetSecondMainCurrency(settingsRepository).execute()
        Assertions.assertEquals("RUB", result)
    }

    @Test
    fun `SetSecondMainCurrency should call setSecondMainCurrency with correct value`() {
        SetSecondMainCurrency(settingsRepository).execute(currency = "GBP")
        verify(settingsRepository).setSecondMainCurrency("GBP")
    }

    // --- GetDefaultCurrency / SetDefaultCurrency ---

    @Test
    fun `GetDefaultCurrency should return currency from repository`() {
        Mockito.`when`(settingsRepository.getDefaultCurrency()).thenReturn("CNY")
        val result = GetDefaultCurrency(settingsRepository).execute()
        Assertions.assertEquals("CNY", result)
    }

    @Test
    fun `SetDefaultCurrency should call setDefaultCurrency with correct value`() {
        SetDefaultCurrency(settingsRepository).execute(currency = "JPY")
        verify(settingsRepository).setDefaultCurrency("JPY")
    }

    // --- GetFinanceCurrency / SetFinanceCurrency ---

    @Test
    fun `GetFinanceCurrency should return currency from repository`() {
        Mockito.`when`(settingsRepository.getFinanceCurrency()).thenReturn("USD")
        val result = GetFinanceCurrency(settingsRepository).execute()
        Assertions.assertEquals("USD", result)
    }

    @Test
    fun `SetFinanceCurrency should call setFinanceCurrency with correct value`() {
        SetFinanceCurrency(settingsRepository).execute(currency = "EUR")
        verify(settingsRepository).setFinanceCurrency("EUR")
    }

    // --- GetAddSumInShareText / SetAddSumInShareText ---

    @Test
    fun `GetAddSumInShareText should return true from repository`() {
        Mockito.`when`(settingsRepository.getAddSumInShareText()).thenReturn(true)
        val result = GetAddSumInShareText(settingsRepository).execute()
        Assertions.assertTrue(result)
    }

    @Test
    fun `GetAddSumInShareText should return false from repository`() {
        Mockito.`when`(settingsRepository.getAddSumInShareText()).thenReturn(false)
        val result = GetAddSumInShareText(settingsRepository).execute()
        Assertions.assertFalse(result)
    }

    @Test
    fun `SetAddSumInShareText should call setAddSumInShareText with true`() {
        SetAddSumInShareText(settingsRepository).execute(addSumInShareText = true)
        verify(settingsRepository).setAddSumInShareText(true)
    }

    @Test
    fun `SetAddSumInShareText should call setAddSumInShareText with false`() {
        SetAddSumInShareText(settingsRepository).execute(addSumInShareText = false)
        verify(settingsRepository).setAddSumInShareText(false)
    }

    // --- GetAppTheme / SetAppTheme ---

    @Test
    fun `GetAppTheme should return theme from repository`() {
        Mockito.`when`(settingsRepository.getAppTheme()).thenReturn("dark")
        val result = GetAppTheme(settingsRepository).execute()
        Assertions.assertEquals("dark", result)
    }

    @Test
    fun `SetAppTheme should call setAppTheme with correct value`() {
        SetAppTheme(settingsRepository).execute(theme = "light")
        verify(settingsRepository).setAppTheme("light")
    }

    // --- GetHumanOrder / SetHumanOrder ---

    @Test
    fun `GetHumanOrder should return order from repository`() {
        val order = Pair(HumanOrderAttribute.Sum, true)
        Mockito.`when`(settingsRepository.getHumanOrder()).thenReturn(order)
        val result = GetHumanOrder(settingsRepository).execute()
        Assertions.assertEquals(order, result)
    }

    @Test
    fun `SetHumanOrder should call setHumanOrder with correct order`() {
        val order = Pair(HumanOrderAttribute.Date, false)
        SetHumanOrder(settingsRepository).execute(order = order)
        verify(settingsRepository).setHumanOrder(order = order)
    }

    // --- GetDebtOrder / SetDebtOrder ---

    @Test
    fun `GetDebtOrder should return order from repository`() {
        val order = Pair(DebtOrderAttribute.Sum, false)
        Mockito.`when`(settingsRepository.getDebtOrder()).thenReturn(order)
        val result = GetDebtOrder(settingsRepository).execute()
        Assertions.assertEquals(order, result)
    }

    @Test
    fun `SetDebtOrder should call setDebtOrder with correct order`() {
        val order = Pair(DebtOrderAttribute.Date, true)
        SetDebtOrder(settingsRepository).execute(order = order)
        verify(settingsRepository).setDebtOrder(order = order)
    }

    // --- GetIsAuthorized / SetIsAuthorized ---

    @Test
    fun `GetIsAuthorized should return true from repository`() {
        Mockito.`when`(settingsRepository.getIsAuthorized()).thenReturn(true)
        val result = GetIsAuthorized(settingsRepository).execute()
        Assertions.assertTrue(result)
    }

    @Test
    fun `SetIsAuthorized should call setIsAuthorized with correct value`() {
        SetIsAuthorized(settingsRepository).execute(isAuthorized = false)
        verify(settingsRepository).setIsAuthorized(false)
    }

    // --- GetPINCode / SetPINCode ---

    @Test
    fun `GetPINCode should return PIN from repository`() {
        Mockito.`when`(settingsRepository.getPINCode()).thenReturn("1234")
        val result = GetPINCode(settingsRepository).execute()
        Assertions.assertEquals("1234", result)
    }

    @Test
    fun `SetPINCode should call setPINCode with correct value`() {
        SetPINCode(settingsRepository).execute(PINCode = "5678")
        verify(settingsRepository).setPINCode("5678")
    }

    // --- GetPINCodeEnabled / SetPINCodeEnabled ---

    @Test
    fun `GetPINCodeEnabled should return true from repository`() {
        Mockito.`when`(settingsRepository.getPINCodeEnabled()).thenReturn(true)
        val result = GetPINCodeEnabled(settingsRepository).execute()
        Assertions.assertTrue(result)
    }

    @Test
    fun `SetPINCodeEnabled should call setPINCodeEnabled with correct value`() {
        SetPINCodeEnabled(settingsRepository).execute(isEnabled = true)
        verify(settingsRepository).setPINCodeEnabled(true)
    }

    // --- GetIsFingerprintAuthEnabled / SetIsFingerprintAuthEnabled ---

    @Test
    fun `GetIsFingerprintAuthEnabled should return false from repository`() {
        Mockito.`when`(settingsRepository.getIsFingerprintAuthEnabled()).thenReturn(false)
        val result = GetIsFingerprintAuthEnabled(settingsRepository).execute()
        Assertions.assertFalse(result)
    }

    @Test
    fun `SetIsFingerprintAuthEnabled should call setIsFingerprintAuthEnabled with correct value`() {
        SetIsFingerprintAuthEnabled(settingsRepository).execute(isEnabled = true)
        verify(settingsRepository).setIsFingerprintAuthEnabled(true)
    }

    // --- GetLastSyncDate / SetLastSyncDate ---

    @Test
    fun `GetLastSyncDate should return timestamp from repository`() {
        val timestamp = 1711234567890L
        Mockito.`when`(settingsRepository.getLastSyncDate()).thenReturn(timestamp)
        val result = GetLastSyncDate(settingsRepository).execute()
        Assertions.assertEquals(timestamp, result)
    }

    @Test
    fun `SetLastSyncDate should call setLastSyncDate with correct value`() {
        val timestamp = 1711234567890L
        SetLastSyncDate(settingsRepository).execute(lastSyncDateInMillis = timestamp)
        verify(settingsRepository).setLastSyncDate(timestamp)
    }

    // --- GetUserData / SetUserData ---

    @Test
    fun `GetUserData should return User from repository`() {
        val user = User(name = "Alice", email = "alice@example.com")
        Mockito.`when`(settingsRepository.getUserData()).thenReturn(user)
        val result = GetUserData(settingsRepository).execute()
        Assertions.assertEquals(user, result)
    }

    @Test
    fun `SetUserData should call setUserData with correct User`() {
        SetUserData(settingsRepository).execute(name = "Bob", email = "bob@example.com")
        val captor = argumentCaptor<User>()
        verify(settingsRepository).setUserData(captor.capture())
        Assertions.assertEquals("Bob", captor.firstValue.name)
        Assertions.assertEquals("bob@example.com", captor.firstValue.email)
    }

    // --- GetDebtQuantityForAppRateDialogShow / SetDebtQuantityForAppRateDialogShow ---

    @Test
    fun `GetDebtQuantityForAppRateDialogShow should return quantity from repository`() {
        Mockito.`when`(settingsRepository.getDebtQuantityForAppRateDialogShow()).thenReturn(5)
        val result = GetDebtQuantityForAppRateDialogShow(settingsRepository).execute()
        Assertions.assertEquals(5, result)
    }

    @Test
    fun `SetDebtQuantityForAppRateDialogShow should call repository with correct quantity`() {
        SetDebtQuantityForAppRateDialogShow(settingsRepository).execute(quantity = 10)
        verify(settingsRepository).setDebtQuantityForAppRateDialogShow(10)
    }
}
