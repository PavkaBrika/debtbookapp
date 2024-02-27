package com.breckneck.deptbook.domain.usecase.Settings

import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.repository.HumanRepository

class MergeSynchronizedData(private val humanRepository: HumanRepository) {

    fun execute(currentAppData: AppDataLists, synchronizedAppData: AppDataLists) {

    }
}