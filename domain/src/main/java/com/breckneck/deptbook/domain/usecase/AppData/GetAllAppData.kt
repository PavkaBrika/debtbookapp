package com.breckneck.deptbook.domain.usecase.AppData

import com.breckneck.deptbook.domain.model.AppDataLists
import com.breckneck.deptbook.domain.repository.AppDataRepository

class GetAllAppData(private val appDataRepository: AppDataRepository) {

    fun execute(): AppDataLists = appDataRepository.getAllAppData()
}
