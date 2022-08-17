package com.android.listapp.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.RoomDatabase
import com.android.listapp.database.ListDatabase
import com.android.listapp.databinding.DialogEditBinding
import com.android.listapp.databinding.ListItemBinding
import com.android.listapp.model.ListInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// ViewHolder는 각 ItemList를 저장하는 객체이다.

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private var lstTodo : ArrayList<ListInfo> = ArrayList()
    private lateinit var roomDatabase: ListDatabase

    fun addListItem(listItem: ListInfo) {
        // 배열은 0,1,2,3..으로 쌓이는데 최신 add 정보가 가장 위에 나오게 해야한.다
        // 최신순으로 보이게 하려면 0번째 인덱스의 위치로 add해주면 된다.
        lstTodo.add(0, listItem)
    }

    // TodoViewHolder는 그저 같은 클래스 내부에 작성된 별도의 클래스이므로 inner을 붙임으로써 lstTodo를 사용할 수 있다.
    inner class TodoViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listItem : ListInfo) {
            // 리스트 뷰 데이터를 UI에 연동
            binding.tvContent.setText(listItem.listContent)
            binding.tvDate.setText(listItem.listDate)

            // 리스트 삭제 버튼 클릭 연동
            binding.btnRemove.setOnClickListener {
                // 쓰레기통 이미지 클릭 시 이벤트 처리

                AlertDialog.Builder(binding.root.context)
                    .setTitle("[경고]")
                    .setMessage("제거하시면 데이터는 복구되지 않습니다.\n정말 제거하시겠습니까?")
                    .setPositiveButton("제거", DialogInterface.OnClickListener { dialog, i ->
                        CoroutineScope(Dispatchers.IO).launch {
                            var innerLst = roomDatabase.listDao().getAllReadData()
                            for (item in innerLst) {
                                if (item.listContent == listItem.listContent && item.listDate == listItem.listDate) {
                                    roomDatabase.listDao().deleteListDate(item)
                                }
                            }

                            // ui remove
                            lstTodo.remove(listItem)
                            (binding.root.context as Activity).runOnUiThread{
                                notifyDataSetChanged()
                                Toast.makeText(binding.root.context, "제거되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, i ->

                    })
                    .show()
            }

            // 리스트 수정
            binding.root.setOnClickListener {
                val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)
                bindingDialog.etMemo.setText(listItem.listContent)

                 androidx.appcompat.app.AlertDialog.Builder(binding.root.context)
                     .setTitle("오늘의 할 일은 ?")
                     .setView(bindingDialog.root)
                     .setPositiveButton("수", DialogInterface.OnClickListener { dialog, i ->
                         CoroutineScope(Dispatchers.IO).launch {
                             val innerLst = roomDatabase.listDao().getAllReadData()
                             for (item in innerLst)
                                 if (item.listContent == listItem.listContent && item.listDate ==listItem.listDate) {
                                     // database modify
                                     listItem.listContent = bindingDialog.etMemo.text.toString()
                                     listItem.listDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                     roomDatabase.listDao().updateListDate(item)
                                 }
                         }
                         // ui modify
                         listItem.listContent = bindingDialog.etMemo.text.toString()
                         listItem.listDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                         // arraylist 수정
                         lstTodo.set(adapterPosition, listItem)
                         (binding.root.context as Activity).runOnUiThread{
                             notifyDataSetChanged()
                         }
                     })
                     .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, i ->
                     // 취소 버튼 이벤트 처리
                     })
                     .show()
            }
        }

    }
    // ViewHoler가 만들어질 때. -> 뷰 홀더가 생성됨( 각 리스트 아이템 1개씩 구성될 때마다 이 오버라이드 메소드가 호출 됨 )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 룸 데이터베이스 초기화
        roomDatabase = ListDatabase.getInstance(binding.root.context)!!

        return TodoViewHolder(binding)
    }

    // ViewHoler가 결합될 때. -> 뷰 홀더가 바인딩( 결합 )이 이루어질 때 해줘야할 처리들을 구현. position은 index 즉 배열과 비슷함.
    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.bind(lstTodo[position])
    }

    // list의 item의 개수를 어댑터에 알려주어야함.
    override fun getItemCount(): Int {
        return lstTodo.size
    }
}