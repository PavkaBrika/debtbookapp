package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository

class AddClickUseCase(val adRepository: AdRepository) {

    fun execute() {
        adRepository.addClick()
    }
}