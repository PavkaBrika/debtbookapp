package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository

class SaveClicksUseCase(val adRepository: AdRepository) {

    fun execute(click: Int) {
        adRepository.saveClick(click = click)
    }
}