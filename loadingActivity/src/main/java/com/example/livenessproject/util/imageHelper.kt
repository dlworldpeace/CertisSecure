package com.example.livenessproject.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class imageHelper {
    companion object {
        fun imageViewToBase64(image_holder: ImageView): String {
            val bitmap = (image_holder.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
            val bytes = stream.toByteArray()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }
}