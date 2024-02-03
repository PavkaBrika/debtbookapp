package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.DebtRepository
import com.breckneck.deptbook.domain.repository.HumanRepository

class UpdateHuman(private val humanRepository: HumanRepository) {

    fun execute(humanDomain: HumanDomain) {
        humanRepository.updateHuman(human = humanDomain)
    }
}