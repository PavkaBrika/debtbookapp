package com.breckneck.debtbook.presentation.viewmodel

import com.breckneck.deptbook.domain.usecase.Human.GetAllDebtsSumUseCase
import com.breckneck.deptbook.domain.usecase.Human.GetAllHumansUseCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito
import org.mockito.kotlin.mock

class MainFragmentViewModelTest {


    val getAllHumansUseCase = mock<GetAllHumansUseCase>()
    val getAllDebtsSumUseCase = mock<GetAllDebtsSumUseCase>()

    @AfterEach
    fun afterEach() {
        Mockito.reset(getAllHumansUseCase)
        Mockito.reset(getAllDebtsSumUseCase)
    }

    @BeforeEach
    fun beforeEach() {

    }


}