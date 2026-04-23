package com.breckneck.deptbook.domain.repository

import com.breckneck.deptbook.domain.model.AppDataLists

interface AppDataRepository {

    fun replaceAllAppData(appDataLists: AppDataLists)
}
