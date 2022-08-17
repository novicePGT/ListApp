package com.android.listapp.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.room.RoomDatabase
import com.android.listapp.adapter.TodoAdapter
import com.android.listapp.database.ListDatabase
import com.android.listapp.databinding.ActivityListMainBinding
import com.android.listapp.databinding.DialogEditBinding
import com.android.listapp.model.ListInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ListMainAcitvity : AppCompatActivity() {
    private lateinit var binding: ActivityListMainBinding
    private lateinit var listAdapter: TodoAdapter
    private lateinit var roomDatabase: ListDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 어댑터 인스턴스 생성
        listAdapter = TodoAdapter()

        // 리사이클러뷰에 어댑터 생성
        binding.rvListLife.adapter = listAdapter

        // 룸 데이터베이스 초기화
        roomDatabase = ListDatabase.getInstance(applicationContext)!!

        // 전체 데이터 로드( 비동기 ) -> 비순서적으로 데이터를 가져오는 방법
        CoroutineScope(Dispatchers.IO).launch {
            val lstTodo = roomDatabase.listDao().getAllReadData() as ArrayList<ListInfo>
            for (todoItem in lstTodo) {
                listAdapter.addListItem(todoItem)
            }
            // UI Thread에서 처리
            runOnUiThread{
                listAdapter.notifyDataSetChanged()

            }
        }

        // 작성하기 버튼 클릭
        binding.btnWrite.setOnClickListener {
            val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)

            AlertDialog.Builder(this)
                .setTitle("오늘의 할 일은 ?")
                .setView(bindingDialog.root)
                .setPositiveButton("작성", DialogInterface.OnClickListener { dialog, i ->
                    // 작성 버튼 이벤트 처리
                    val todoItem = ListInfo()
                    todoItem.listContent = bindingDialog.etMemo.text.toString()
                    // pattern은 이러한 형식을 유지하겠다는 구문이고, format(Date()) 메서드가 현재 시간을 가져와준다.
                    todoItem.listDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    listAdapter.addListItem(todoItem) // 어댑터의 전역변수의 arraylist 쪽에 아이템 추가하기위한 메소드 호출
                    CoroutineScope(Dispatchers.IO).launch {
                        roomDatabase.listDao().insertListData(todoItem)
                        runOnUiThread {
                            listAdapter.notifyDataSetChanged() // 리스트 새로고침 -> 어댑터가 한 사이클 돌게 됨. 고로 아이템이 표출됨.
                        }
                    }
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, i ->
                    // 취소 버튼 이벤트 처리
                })
                .show()
        }
    }
}