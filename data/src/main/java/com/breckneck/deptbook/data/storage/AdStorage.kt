package com.breckneck.deptbook.data.storage

interface AdStorage {

    fun getClicks(): Int

    fun saveClick(click: Int)
}