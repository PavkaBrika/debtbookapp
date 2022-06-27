package com.breckneck.deptbook.domain.usecase

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class GetAllHumansUseCase(private val humanRepository: HumanRepository) {

    fun execute() : List<HumanDomain> {
        return humanRepository.getAllHumans()
    }
}