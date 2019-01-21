package com.example.livenessproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.livenessproject.httpUtil.HttpHelper
import com.megvii.livenessproject.R
import java.io.File
import kotlinx.android.synthetic.main.activity_one_to_one.*
import java.io.ByteArrayOutputStream

class OneToOneActivity : AppCompatActivity() {

    private var CONTENT_REQUEST: Int = 1337
    private var SELECT_FILE: Int = 0
    private var cameraOutput: File? = null
    private var currentHolder: Int = 1
    private var imageOneAdded: Boolean = false
    private var imageTwoAdded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_to_one)

        // Allow Network Connection to be made on main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        upload_button1.setOnClickListener {
            selectImage()
            currentHolder = 1
        }
        upload_button2.setOnClickListener {
            selectImage()
            currentHolder = 2
        }
    }

    private fun selectImage() {

        val items = arrayOf<CharSequence>("Take using Camera", "Import from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload Image")

        builder.setItems(items) { dialogInterface, i ->
            when(items[i]) {
                "Take using Camera" -> {
                    // Prevent FileUriExposedException
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    cameraOutput = File(dir, "CameraContent.jpeg")
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraOutput))
                    startActivityForResult(intent, CONTENT_REQUEST)
                }
                "Import from Gallery" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    startActivityForResult(intent, SELECT_FILE)
                }
                "Cancel" -> dialogInterface.dismiss()
            }
        }
        builder.show()

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CONTENT_REQUEST) {

                when(currentHolder) {
                    1 -> {
                        image_holder1.setImageURI(Uri.fromFile(cameraOutput))
                        imageOneAdded = true
                    }
                    2 -> {
                        image_holder2.setImageURI(Uri.fromFile(cameraOutput))
                        imageTwoAdded = true
                    }
                }

            } else if (requestCode == SELECT_FILE) {

                super.onActivityResult(requestCode, resultCode, data)
                when(currentHolder) {
                    1 -> {
                        image_holder1.setImageURI(data!!.data)
                        imageOneAdded = true
                    }
                    2 -> {
                        image_holder2.setImageURI(data!!.data)
                        imageTwoAdded = true
                    }
                }

            }
            if(imageOneAdded && imageTwoAdded) {
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    Log.d("oneToOneComparison", "uploading to compare..........")
                    val img1 = imageViewToBase64(image_holder1)
                    val maxLogSize = 2000
                    for (i in 0..img1.length / maxLogSize) {
                        val start = i * maxLogSize
                        var end = (i + 1) * maxLogSize
                        end = if (end > img1.length) img1.length else end
                        Log.d("oneToOneComparison", img1.substring(start, end))
                    }
                    val img2 = imageViewToBase64(image_holder2)
                    Log.d("Switch", "img2 is:.............." )
                    for (i in 0..img2.length / maxLogSize) {
                        val start = i * maxLogSize
                        var end = (i + 1) * maxLogSize
                        end = if (end > img2.length) img2.length else end
                        Log.d("oneToOneComparison", img2.substring(start, end))
                    }
                    val result = HttpHelper.oneToOneComparison(img1,img2)
                    Log.d("oneToOneComparison", "uploaded finish, waiting for results..........")
                    Log.d("oneToOneComparison", result)
                    Log.d("oneToOneComparison", "done")
                }
            }

        }
    }

    private fun imageViewToBase64(image_holder: ImageView): String {
        val bitmap = (image_holder.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
