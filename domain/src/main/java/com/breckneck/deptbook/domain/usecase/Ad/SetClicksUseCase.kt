package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository

class SetClicksUseCase(val adRepository: AdRepository) {

    fun execute() {
        adRepository.setClicks()
    }
}