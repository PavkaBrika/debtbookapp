package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class GetNegativeHumansUseCase(private val humanRepository: HumanRepository) {

    fun execute(): List<HumanDomain> {
        return humanRepository.getNegativeHumans()
    }
}