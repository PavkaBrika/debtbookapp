package com.breckneck.deptbook.data.storage.repository

import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.entity.Human
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class HumanRepositoryImpl(private val humanStorage: HumanStorage) : HumanRepository {

    override fun getAllHumans(): List<HumanDomain> {
        val humanList: List<Human> = humanStorage.getAllHumans()
        val humanDomainList : List<HumanDomain> = humanList.map {
            HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt)
        }
        return humanDomainList
    }

    override fun insertHuman(humanDomain: HumanDomain) {
        humanStorage.insertHuman(Human(id = humanDomain.id, name = humanDomain.name, sumDebt = humanDomain.sumDebt))
    }
}