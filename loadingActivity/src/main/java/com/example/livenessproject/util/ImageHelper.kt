package com.example.livenessproject.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class ImageHelper {
    companion object {
        fun imageViewToBase64(imageView: ImageView): String {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val byteArray = stream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP) //NO_WRAP because the string will be used in json, which does not recognize \n
        }
    }
}