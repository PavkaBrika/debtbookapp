package com.breckneck.deptbook.domain.usecase.Human

import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class GetPositiveHumansUseCase(private val humanRepository: HumanRepository) {

    fun execute(humanList: List<HumanDomain>): List<HumanDomain> {
        val resultHumanList = ArrayList<HumanDomain>()
        for (human in humanList) {
            if (human.sumDebt >= 0)
                resultHumanList.add(human)
        }
        return resultHumanList
    }
}