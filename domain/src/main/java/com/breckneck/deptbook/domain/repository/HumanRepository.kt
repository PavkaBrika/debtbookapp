package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.HumanDomain

interface HumanRepository {

    fun getAllHumans() : List<HumanDomain>

    fun replaceAllHumans(humanList: List<HumanDomain>)

    fun getPositiveHumans(): List<HumanDomain>

    fun getNegativeHumans(): List<HumanDomain>

    fun insertHuman(humanDomain: HumanDomain)

    fun getLastHumanId() : Int

    fun addSum(humanId: Int,sum: Double)

    fun getAllDebtsSum(currency: String): List<Double>

    fun getHumanSumDebt(humanId: Int): Double

    fun deleteHuman(id: Int)

    fun updateHuman(human: HumanDomain)


}