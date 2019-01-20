package com.example.livenessproject

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.megvii.livenessproject.R
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.net.Uri
import android.os.Environment
import kotlinx.android.synthetic.main.activity_one_to_many.*
import java.io.File
import android.os.StrictMode
import android.view.View

class OneToManyActivity : AppCompatActivity() {

    private var CONTENT_REQUEST: Int = 1337
    private var SELECT_FILE:Int = 0
    private var cameraOutput: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_to_many)

        fab.setOnClickListener { selectImage() }
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

                image_holder.setImageURI(Uri.fromFile(cameraOutput))

            } else if (requestCode == SELECT_FILE) {

                super.onActivityResult(requestCode, resultCode, data)
                image_holder.setImageURI(data!!.data)

            }

            button.visibility = View.VISIBLE
//            button.setOnClickListener{
//
//            }

        }
    }
}
