package com.example.photoeditor.saveImage

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.others.Constant
import com.example.photoeditor.R

class ImageSaveActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_save)

        val imageView = findViewById<ImageView>(R.id.imagesaveIV)
        imageView.setImageURI(Constant.imageUri)

    }
}