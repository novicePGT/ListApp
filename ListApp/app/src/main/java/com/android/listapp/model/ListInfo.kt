package com.android.listapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ListInfo {
    var listContent: String = "" // 메모 내용
    var listDate: String = "" // 메모 일자

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 // DB에 정보가 들어올 때마다 0,1,2,3,,, 증가함.
}