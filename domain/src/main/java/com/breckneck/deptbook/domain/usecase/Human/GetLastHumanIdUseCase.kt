package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.repository.HumanRepository

class GetLastHumanIdUseCase(val humanRepository: HumanRepository) {

    fun exectute() : Int {
        return humanRepository.getLastHumanId()
    }
}