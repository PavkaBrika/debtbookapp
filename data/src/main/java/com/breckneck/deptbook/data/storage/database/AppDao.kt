package com.breckneck.deptbook.data.storage.database

import androidx.room.*
import com.breckneck.deptbook.data.storage.entity.Human


@Dao
interface AppDao {

    @Query("SELECT * FROM human")
    fun getAllHuman(): List<Human>

    @Insert
    fun insertHuman(human: Human)

    @Delete
    fun deleteHuman(human: Human)

    @Update
    fun updateHuman(human: Human)

}