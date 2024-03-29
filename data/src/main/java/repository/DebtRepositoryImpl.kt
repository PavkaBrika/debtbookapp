package repository

import com.breckneck.deptbook.data.storage.DebtStorage
import entity.Debt
import com.breckneck.deptbook.domain.model.DebtDomain
import com.breckneck.deptbook.domain.repository.DebtRepository

class DebtRepositoryImpl(val debtStorage: DebtStorage): DebtRepository {

    override fun getAllDebtsById(id: Int): List<DebtDomain> {
        val debtList = debtStorage.getAllDebtsById(id = id)
        val humanDomainList = debtList.map {
            DebtDomain(id = it.id, sum = it.sum, idHuman = it.idHuman, info = it.info, date = it.date)
        }
        return humanDomainList
    }

    override fun getAllDebts(): List<DebtDomain> {
        val debtList = debtStorage.getAllDebts()
        val humanDomainList = debtList.map {
            DebtDomain(id = it.id, sum = it.sum, idHuman = it.idHuman, info = it.info, date = it.date)
        }
        return humanDomainList
    }

    override fun replaceAllDebts(debtList: List<DebtDomain>) {
        val debtDataList = debtList.map {
            Debt(id = it.id, sum = it.sum, idHuman = it.idHuman, info = it.info, date = it.date)
        }
        debtStorage.replaceAllDebts(debtList = debtDataList)
    }

    override fun setDebt(debtDomain: DebtDomain) {
        val debt = Debt(id = debtDomain.id, sum = debtDomain.sum, idHuman = debtDomain.idHuman, info = debtDomain.info, date = debtDomain.date)
        debtStorage.setDebt(debt)
    }

    override fun deleteDebt(debtDomain: DebtDomain) {
        val debt = Debt(id = debtDomain.id, sum = debtDomain.sum, idHuman = debtDomain.idHuman, info = debtDomain.info, date = debtDomain.date)
        debtStorage.deleteDebt(debt)
    }

    override fun editDebt(debtDomain: DebtDomain) {
        val debt = Debt(id = debtDomain.id, sum = debtDomain.sum, idHuman = debtDomain.idHuman, info = debtDomain.info, date = debtDomain.date)
        debtStorage.editDebt(debt)
    }

    override fun deleteDebtsByHumanId(id: Int) {
        debtStorage.deleteDebtsByHumanId(id = id)
    }

    override fun getDebtQuantity(): Int {
        return debtStorage.getDebtQuantity()
    }
}