package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class ReplaceAllHumans(private val humanRepository: HumanRepository) {

    fun execute(humanList: List<HumanDomain>) {
        humanRepository.replaceAllHumans(humanList = humanList)
    }
}