package com.breckneck.deptbook.data.storage

import com.breckneck.deptbook.data.storage.entity.Human

interface HumanStorage  {

    fun getAllHumans() : List<Human>

    fun insertHuman(human: Human)

    fun getLastHumanId() : Int

    fun addSum(humanId: Int,sum: Double)

    fun getAllDebtsSum(): List<Double>
}