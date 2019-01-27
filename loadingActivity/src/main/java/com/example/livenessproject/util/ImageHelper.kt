package com.example.livenessproject.util

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class ImageHelper {
    companion object {
        fun imageViewToBase64(imageView: ImageView): String {
            var bitmap = (imageView.drawable as BitmapDrawable).bitmap
            while(bitmap.width > 1300) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, false)
            }
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream)
            val byteArray = stream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP) //NO_WRAP because the string will be used in json, which does not recognize \n
        }

        fun extendedBase64ToBitmap(extendedBase64: String): Bitmap {
            val photoData = extendedBase64.substring(extendedBase64.indexOf(",") + 1)
            val decodedString = Base64.decode(photoData, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        fun getCroppedCircleBitmap(bitmap: Bitmap): Bitmap {
            val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            val color = 0xff424242
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color.toInt()
            canvas.drawCircle(bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
                    bitmap.width / 2.toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}