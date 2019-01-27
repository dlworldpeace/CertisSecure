package com.example.livenessproject

import android.app.Activity
import android.content.Context
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
import android.widget.Toast
import com.example.livenessproject.util.HttpHelper
import com.example.livenessproject.util.ImageHelper
import org.json.JSONObject

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

                image_holder1.setImageURI(Uri.fromFile(cameraOutput))

            } else if (requestCode == SELECT_FILE) {

                super.onActivityResult(requestCode, resultCode, data)
                image_holder1.setImageURI(data!!.data)

            }

            button.visibility = View.VISIBLE
            button.setOnClickListener{
                val img = ImageHelper.imageViewToRoundBase64(image_holder1)
                val result = HttpHelper.oneToManyComparison(img)
                val jsonObject = JSONObject(result)
                val message = jsonObject.optString("message")

                if(message != "") {
                    toast(message)
                } else {
                    val user = jsonObject.getJSONObject("user")
                    val id = user.get("_id")
                    val displayName = user.get("displayName")
                    val mobile = user.get("mobile")

                    toast("Match found.")
                    result_display.setText("id: $id\ndisplayName: $displayName\nmobile: $mobile")
                    result_display.visibility = View.VISIBLE
                }
            }

        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
