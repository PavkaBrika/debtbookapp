package repository

import com.breckneck.deptbook.data.storage.HumanStorage
import entity.Human
import com.breckneck.deptbook.domain.model.HumanDomain
import com.breckneck.deptbook.domain.repository.HumanRepository

class HumanRepositoryImpl(private val humanStorage: HumanStorage) : HumanRepository {

    override fun getAllHumans(): List<HumanDomain> {
        val humanList: List<Human> = humanStorage.getAllHumans()
        val humanDomainList: List<HumanDomain> = humanList.map {
            HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
        }
        return humanDomainList
    }

    override fun replaceAllHumans(humanList: List<HumanDomain>) {
        val humanDataList: List<Human> = humanList.map {
            Human(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
        }
        humanStorage.replaceAllHumans(humanList = humanDataList)
    }

    override fun getPositiveHumans(): List<HumanDomain> {
        val humanList: List<Human> = humanStorage.getPositiveHumans()
        val humanDomainList: List<HumanDomain> = humanList.map {
            HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
        }
        return humanDomainList
    }

    override fun getNegativeHumans(): List<HumanDomain> {
        val humanList: List<Human> = humanStorage.getNegativeHumans()
        val humanDomainList: List<HumanDomain> = humanList.map {
            HumanDomain(id = it.id, name = it.name, sumDebt = it.sumDebt, currency = it.currency)
        }
        return humanDomainList
    }

    override fun insertHuman(humanDomain: HumanDomain) {
        humanStorage.insertHuman(
            Human(
                id = humanDomain.id,
                name = humanDomain.name,
                sumDebt = humanDomain.sumDebt,
                currency = humanDomain.currency
            )
        )
    }

    override fun getLastHumanId(): Int {
        return humanStorage.getLastHumanId()
    }

    override fun addSum(humanId: Int, sum: Double) {
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

    override fun updateHuman(human: HumanDomain) {
        humanStorage.updateHuman(
            Human(
                id = human.id,
                name = human.name,
                sumDebt = human.sumDebt,
                currency = human.currency
            )
        )
    }
}