package com.example.livenessproject.util

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class ImageHelper {
    companion object {
        fun imageViewToRoundBase64(imageView: ImageView): String {
            val srcBmp = (imageView.drawable as BitmapDrawable).bitmap
            var dstBmp :Bitmap?

            // Crop to square shape
            dstBmp = if (srcBmp.width >= srcBmp.height){
                Bitmap.createBitmap(srcBmp, srcBmp.width/2 - srcBmp.height/2, 0, srcBmp.height, srcBmp.height)
            }else{
                Bitmap.createBitmap(srcBmp, 0, srcBmp.height/2 - srcBmp.width/2, srcBmp.width, srcBmp.width)
            }

            // Downsize to range (650px, 1300px)
            while(dstBmp!!.width > 1300) {
                dstBmp = Bitmap.createScaledBitmap(dstBmp, dstBmp.width / 2, dstBmp.height / 2, false)
            }

            val stream = ByteArrayOutputStream()
            dstBmp.compress(Bitmap.CompressFormat.JPEG, 20, stream)
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
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)

            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = 0xff424242.toInt()
            canvas.drawCircle(bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(),
                    bitmap.width / 2.toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}