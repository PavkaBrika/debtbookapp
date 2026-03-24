package com.breckneck.debtbook.settings.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.breckneck.deptbook.domain.model.User
import com.breckneck.deptbook.domain.usecase.Settings.GetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.GetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.GetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetIsFingerprintAuthEnabled
import com.breckneck.deptbook.domain.usecase.Settings.GetIsAuthorized
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetUserData
import com.breckneck.deptbook.domain.usecase.Settings.SetAddSumInShareText
import com.breckneck.deptbook.domain.usecase.Settings.SetAppTheme
import com.breckneck.deptbook.domain.usecase.Settings.SetDefaultCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetIsFingerprintAuthEnabled
import com.breckneck.deptbook.domain.usecase.Settings.SetSecondMainCurrency
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SettingsViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val setFirstMainCurrency = Mockito.mock(SetFirstMainCurrency::class.java)
    private val getFirstMainCurrency = Mockito.mock(GetFirstMainCurrency::class.java)
    private val setSecondMainCurrency = Mockito.mock(SetSecondMainCurrency::class.java)
    private val getSecondMainCurrency = Mockito.mock(GetSecondMainCurrency::class.java)
    private val setDefaultCurrency = Mockito.mock(SetDefaultCurrency::class.java)
    private val getDefaultCurrency = Mockito.mock(GetDefaultCurrency::class.java)
    private val setAddSumInShareText = Mockito.mock(SetAddSumInShareText::class.java)
    private val getAddSumInShareText = Mockito.mock(GetAddSumInShareText::class.java)
    private val getAppTheme = Mockito.mock(GetAppTheme::class.java)
    private val setAppTheme = Mockito.mock(SetAppTheme::class.java)
    private val getIsAuthorized = Mockito.mock(GetIsAuthorized::class.java)
    private val getUserData = Mockito.mock(GetUserData::class.java)
    private val setIsFingerprintAuthEnabled = Mockito.mock(SetIsFingerprintAuthEnabled::class.java)
    private val getIsFingerprintAuthEnabled = Mockito.mock(GetIsFingerprintAuthEnabled::class.java)

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Mockito.`when`(getFirstMainCurrency.execute()).thenReturn("USD")
        Mockito.`when`(getSecondMainCurrency.execute()).thenReturn("EUR")
        Mockito.`when`(getDefaultCurrency.execute()).thenReturn("USD")
        Mockito.`when`(getAppTheme.execute()).thenReturn("light")
        Mockito.`when`(getAddSumInShareText.execute()).thenReturn(false)
        Mockito.`when`(getIsAuthorized.execute()).thenReturn(false)

        viewModel = SettingsViewModel(
            setFirstMainCurrency = setFirstMainCurrency,
            getFirstMainCurrency = getFirstMainCurrency,
            setSecondMainCurrency = setSecondMainCurrency,
            getSecondMainCurrency = getSecondMainCurrency,
            setDefaultCurrency = setDefaultCurrency,
            getDefaultCurrency = getDefaultCurrency,
            setAddSumInShareText = setAddSumInShareText,
            getAddSumInShareText = getAddSumInShareText,
            getAppTheme = getAppTheme,
            setAppTheme = setAppTheme,
            getIsAuthorized = getIsAuthorized,
            getUserData = getUserData,
            setIsFingerprintAuthEnabled = setIsFingerprintAuthEnabled,
            getIsFingerprintAuthEnabled = getIsFingerprintAuthEnabled
        )
    }

    @After
    fun tearDown() {
        Mockito.reset(
            setFirstMainCurrency, getFirstMainCurrency,
            setSecondMainCurrency, getSecondMainCurrency,
            setDefaultCurrency, getDefaultCurrency,
            setAddSumInShareText, getAddSumInShareText,
            getAppTheme, setAppTheme,
            getIsAuthorized, getUserData,
            setIsFingerprintAuthEnabled, getIsFingerprintAuthEnabled
        )
    }

    @Test
    fun `init should load firstMainCurrency from use case`() {
        assertEquals("USD", viewModel.firstMainCurrency.value)
    }

    @Test
    fun `init should load secondMainCurrency from use case`() {
        assertEquals("EUR", viewModel.secondMainCurrency.value)
    }

    @Test
    fun `init should load defaultCurrency from use case`() {
        assertEquals("USD", viewModel.defaultCurrency.value)
    }

    @Test
    fun `init should load appTheme from use case`() {
        assertEquals("light", viewModel.appTheme.value)
    }

    @Test
    fun `init should load addSumInShareText from use case`() {
        assertFalse(viewModel.addSumInShareText.value!!)
    }

    @Test
    fun `setFirstMainCurrency should update LiveData and call use case`() {
        viewModel.setFirstMainCurrency("RUB")
        assertEquals("RUB", viewModel.firstMainCurrency.value)
        Mockito.verify(setFirstMainCurrency).execute(currency = "RUB")
    }

    @Test
    fun `setSecondMainCurrency should update LiveData and call use case`() {
        viewModel.setSecondMainCurrency("GBP")
        assertEquals("GBP", viewModel.secondMainCurrency.value)
        Mockito.verify(setSecondMainCurrency).execute(currency = "GBP")
    }

    @Test
    fun `setDefaultCurrency should update LiveData and call use case`() {
        viewModel.setDefaultCurrency("JPY")
        assertEquals("JPY", viewModel.defaultCurrency.value)
        Mockito.verify(setDefaultCurrency).execute(currency = "JPY")
    }

    @Test
    fun `setAppTheme should update LiveData and call use case`() {
        viewModel.setAppTheme("dark")
        assertEquals("dark", viewModel.appTheme.value)
        Mockito.verify(setAppTheme).execute(theme = "dark")
    }

    @Test
    fun `setSumInShareText true should update LiveData and call use case`() {
        viewModel.setSumInShareText(true)
        assertTrue(viewModel.addSumInShareText.value!!)
        Mockito.verify(setAddSumInShareText).execute(true)
    }

    @Test
    fun `setSumInShareText false should update LiveData and call use case`() {
        viewModel.setSumInShareText(false)
        assertFalse(viewModel.addSumInShareText.value!!)
        Mockito.verify(setAddSumInShareText).execute(false)
    }

    @Test
    fun `onSettingsDialogOpen should set isSettingsDialogOpened to true`() {
        viewModel.onSettingsDialogOpen(
            settingsTitle = "Currency",
            settingsList = listOf("USD", "EUR"),
            selectedSetting = 0,
            onSettingsClickListener = Mockito.mock()
        )
        assertEquals(true, viewModel.isSettingsDialogOpened.value)
    }

    @Test
    fun `onDialogClose should set isSettingsDialogOpened to false`() {
        viewModel.onSettingsDialogOpen(
            settingsTitle = "Theme",
            settingsList = listOf("light", "dark"),
            selectedSetting = 1,
            onSettingsClickListener = Mockito.mock()
        )
        viewModel.onDialogClose()
        assertEquals(false, viewModel.isSettingsDialogOpened.value)
    }

    @Test
    fun `setIsSynchronizationAvailable should update LiveData`() {
        viewModel.setIsSynchronizationAvailable(true)
        assertEquals(true, viewModel.isSynchronizationAvailable.value)
    }

    @Test
    fun `getUserData should update userName and emailAddress LiveData`() {
        val user = User(name = "Alice", email = "alice@example.com")
        Mockito.`when`(getUserData.execute()).thenReturn(user)
        viewModel.getUserData()
        assertEquals("Alice", viewModel.userName.value)
        assertEquals("alice@example.com", viewModel.emailAddress.value)
    }

    @Test
    fun `getIsFingerprintAuthEnabled should update LiveData`() {
        Mockito.`when`(getIsFingerprintAuthEnabled.execute()).thenReturn(true)
        viewModel.getIsFingerprintAuthEnabled()
        assertEquals(true, viewModel.isFingerprintAuthEnabled.value)
    }

    @Test
    fun `setIsFingerprintAuthEnabled should update LiveData and call use case`() {
        viewModel.setIsFingerprintAuthEnabled(true)
        assertEquals(true, viewModel.isFingerprintAuthEnabled.value)
        Mockito.verify(setIsFingerprintAuthEnabled).execute(true)
    }
}
