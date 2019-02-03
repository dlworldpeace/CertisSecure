package com.example.livenessproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.example.livenessproject.util.HttpHelper
import com.example.livenessproject.util.ImageHelper
import com.megvii.livenessproject.R
import kotlinx.android.synthetic.main.activity_detect_face.*
import org.json.JSONObject
import java.io.File

class DetectFaceActivity : AppCompatActivity() {

    private var CONTENT_REQUEST: Int = 1337
    private var SELECT_FILE:Int = 0
    private var cameraOutput: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_detect_face)

        image_button.setOnClickListener { selectImage() }
        button.setOnClickListener { toast("You need to upload an image first") }
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

                image_button.setImageURI(Uri.fromFile(cameraOutput))

            } else if (requestCode == SELECT_FILE) {

                super.onActivityResult(requestCode, resultCode, data)
                image_button.setImageURI(data!!.data)

            }

            button.setOnClickListener{
                val img = ImageHelper.imageViewToRoundBase64(image_button)
                val result = HttpHelper.faceDetection(img)
                val jsonObject = JSONObject(result)
                val faces = jsonObject.optJSONArray("faces")

                if(faces == null) {
                    toast("no face detected")
                } else {
                    val json = faces.get(0) as JSONObject
                    val confidence = json.getString("Confidence")
                    toast("Face detected with confidence level of $confidence")
                }
            }

        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
