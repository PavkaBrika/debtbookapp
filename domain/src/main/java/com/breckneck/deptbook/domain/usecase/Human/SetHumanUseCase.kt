package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class SetHumanUseCase(private val humanRepository: HumanRepository) {

    fun execute(name: String, sumDebt: Double) {
        humanRepository.insertHuman(HumanDomain(name = name, sumDebt = sumDebt, id = 0))
    }
}