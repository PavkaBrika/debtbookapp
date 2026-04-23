package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.AppDataLists

interface AppDataRepository {

    fun getAllAppData(): AppDataLists

    fun replaceAllAppData(appDataLists: AppDataLists)
}
