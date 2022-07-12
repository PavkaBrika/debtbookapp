package com.breckneck.deptbook.domain.usecase.Ad

import com.breckneck.deptbook.domain.repository.AdRepository

class GetClicksUseCase(val adRepository: AdRepository) {

    fun execute(): Boolean {
        var result = false
        if (adRepository.getClicks() > 10) {
            result = true
        }
        return result
    }
}