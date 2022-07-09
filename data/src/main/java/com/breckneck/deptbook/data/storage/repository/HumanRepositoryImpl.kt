package com.breckneck.deptbook.data.storage.repository

import com.breckneck.deptbook.data.storage.HumanStorage
import com.breckneck.deptbook.data.storage.entity.Human
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class HumanRepositoryImpl(private val humanStorage: HumanStorage) : HumanRepository {

    override fun getAllHumans(): List<HumanDomain> {
        val humanList: List<Human> = humanStorage.getAllHumans()
        val humanDomainList : List<HumanDomain> = humanList.map {
            HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
        }
        return humanDomainList
    }

    override fun insertHuman(humanDomain: HumanDomain) {
        humanStorage.insertHuman(Human(id = humanDomain.id, name = humanDomain.name, sumDebt = humanDomain.sumDebt, currency = humanDomain.currency))
    }

    override fun getLastHumanId() : Int {
        return humanStorage.getLastHumanId()
    }

    override fun addSum(humanId: Int,sum: Double) {
        humanStorage.addSum(humanId = humanId, sum = sum)
    }

    override fun getAllDebtsSum(currency: String): List<Double> {
        return humanStorage.getAllDebtsSum(currency)
    }

    override fun getHumanSumDebt(humanId: Int): Double {
        return humanStorage.getHumanSumDebtUseCase(humanId = humanId)
    }

    override fun deleteHuman(id: Int) {
        humanStorage.deleteHumanById(id)
    }

}