package com.breckneck.deptbook.data.storage

import entity.Human

interface HumanStorage  {

    fun getAllHumans() : List<Human>

    fun getPositiveHumans(): List<Human>

    fun getNegativeHumans(): List<Human>

    fun insertHuman(human: Human)

    fun getLastHumanId() : Int

    fun addSum(humanId: Int,sum: Double)

    fun getAllDebtsSum(currency: String): List<Double>

    fun getHumanSumDebtUseCase(humanId: Int): Double

    fun deleteHumanById(id: Int)

    fun updateHuman(human: Human)
}