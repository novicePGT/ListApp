package com.android.listapp.database

import androidx.room.*
import com.android.listapp.model.ListInfo

// Dao -> Data Access Object
@Dao
interface ListDao {

    // database table에 삽입( Create )
    @Insert
    fun insertListData(listInfo : ListInfo)

    // database table에 수정( Update )
    @Update
    fun updateListDate(listInfo : ListInfo)

    // database table에 삭제( Delete )
    @Delete
    fun deleteListDate(listInfo : ListInfo)

    // database table의 전체 데이터를 조회( Read )
    @Query("SELECT * FROM ListInfo ORDER BY listDate")
    fun getAllReadData(): List<ListInfo>
}