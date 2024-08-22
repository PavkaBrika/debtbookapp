package com.breckneck.debtbook.core.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.breckneck.debtbook.debt.viewmodel.MainFragmentViewModel
import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetNegativeHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetPositiveHumansUseCase
import com.breckneck.deptbook.domain.usecase.Human.UpdateHuman
import com.breckneck.deptbook.domain.usecase.Settings.GetFirstMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.GetHumanOrder
import com.breckneck.deptbook.domain.usecase.Settings.GetSecondMainCurrency
import com.breckneck.deptbook.domain.usecase.Settings.SetHumanOrder
import com.breckneck.deptbook.domain.util.HumanOrderAttribute
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.mock

class MainFragmentViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    val getAllHumansUseCase = mock<GetAllHumansUseCase>()
    val getAllDebtsSumUseCase = mock<GetAllDebtsSumUseCase>()
    val getPositiveHumansUseCase = mock<GetPositiveHumansUseCase>()
    val getNegativeHumansUseCase = mock<GetNegativeHumansUseCase>()
    val getFirstMainCurrency = mock<GetFirstMainCurrency>()
    val getSecondMainCurrency = mock<GetSecondMainCurrency>()
    val updateHuman = mock<UpdateHuman>()
    val setHumanOrder = mock<SetHumanOrder>()
    val getHumanOrder = mock<GetHumanOrder>()

    lateinit var viewModel: MainFragmentViewModel

    @AfterEach
    fun afterEach() {
        Mockito.reset(setHumanOrder)
        Mockito.reset(getHumanOrder)
    }

    @BeforeEach
    fun beforeEach() {
//        viewModel = MainFragmentViewModel(
//            getAllHumansUseCase,
//            getAllDebtsSumUseCase,
//            getPositiveHumansUseCase,
//            getNegativeHumansUseCase,
//            getFirstMainCurrency,
//            getSecondMainCurrency,
//            getHumanOrder,
//            setHumanOrder,
//            updateHuman
//        )
    }

    @Test
    fun `should set human order`() {
        val viewModel = MainFragmentViewModel(
            getAllHumansUseCase,
            getAllDebtsSumUseCase,
            getPositiveHumansUseCase,
            getNegativeHumansUseCase,
            getFirstMainCurrency,
            getSecondMainCurrency,
            getHumanOrder,
            setHumanOrder,
            updateHuman
        )
        val orderTestData = Pair(HumanOrderAttribute.Sum, true)
        viewModel.onSetHumanOrder(order = orderTestData)

        val expected = orderTestData
        val actual = viewModel.humanOrder.value
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun `should get human order`() {
        val viewModel = MainFragmentViewModel(
            getAllHumansUseCase,
            getAllDebtsSumUseCase,
            getPositiveHumansUseCase,
            getNegativeHumansUseCase,
            getFirstMainCurrency,
            getSecondMainCurrency,
            getHumanOrder,
            setHumanOrder,
            updateHuman
        )
        val orderTestData = Pair(HumanOrderAttribute.Sum, true)
        Mockito.`when`(getHumanOrder.execute()).thenReturn(orderTestData)
        viewModel.getHumanOrder()
        val expected = getHumanOrder.execute()
        val actual = viewModel.humanOrder.value
        Assertions.assertEquals(expected, actual)
    }
}