package com.example.photoeditor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.dashBoard.DashboardScreen
import com.example.photoeditor.fragment.LoginScreen
import com.example.photoeditor.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedpref = getSharedPreferences("shared",MODE_PRIVATE)
            val data = sharedpref.getString("login",null)
            if(data == "success"){
                val intent = Intent(this, DashboardScreen::class.java)
                startActivity(intent)
                finish()
            }else{
                supportFragmentManager.beginTransaction().replace(R.id.farmelayout, LoginScreen()).commit()
            }

        },3000)

    }
}