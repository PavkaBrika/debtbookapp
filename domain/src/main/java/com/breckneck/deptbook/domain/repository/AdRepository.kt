package com.breckneck.deptbook.domain.repository

interface AdRepository {

    fun getClicks(): Int

    fun saveClick(click: Int)
}