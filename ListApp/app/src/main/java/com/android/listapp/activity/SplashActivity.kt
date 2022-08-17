package com.android.listapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.android.listapp.R

// 시작화면은 브랜드를 보여주기 위함과 사용자 입장에서 보이지 않는 것들을 로드하기 위해서 사용된다.
// 하지만, 우리는 복잡하게 백그라운드 네트워크를 돌린다던지의 액션을 하지 않기 때문에 보여주기 식으로 딜레이를 걸 용도로 DelayHandler를 사용한다.
// startActivity는 화면을 시작하다는 의미로 인텐트라는 인자를 넣어서 작동을 시키게 되면 1.5초 뒤에 인텐트를 활용해서 화면을 이동시킬 수 있다.
// Intent( 현재액티비티[ this ], 이동하고 싶은 액티비티 명[ ---::class.java ] )
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            // 이 곳에 내부 로직 코드 작성.
            startActivity(Intent(this, ListMainAcitvity::class.java))
            finish() // 현재 액티비티 종료
        }, 1500)
    }
}