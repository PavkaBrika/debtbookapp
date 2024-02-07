package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository

class GetClicksUseCase(val adRepository: AdRepository) {

    fun execute(): Int {
        return adRepository.getClicks()
    }
}