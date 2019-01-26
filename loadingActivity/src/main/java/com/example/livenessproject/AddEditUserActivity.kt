package com.example.livenessproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.livenessproject.util.HttpHelper
import com.example.livenessproject.util.ImageHelper
import com.megvii.livenessproject.R
import kotlinx.android.synthetic.main.activity_add_edit_user.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class AddEditUserActivity : AppCompatActivity() {

    private var CONTENT_REQUEST: Int = 1337
    private var SELECT_FILE: Int = 0
    private var cameraOutput: File? = null
    private var isUpdateNotCreate = false
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_user)

        // Allow Network Connection to be made on main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        id = intent.getStringExtra("id")
        id?.let{
            isUpdateNotCreate = true
            val result = HttpHelper.getUserById(id!!)
            try {
                val jsonObject = JSONObject(result)
                val loginName = jsonObject.getString("loginName")
                val displayName = jsonObject.getString("displayName")
                val email = jsonObject.getString("email")
                val mobile = jsonObject.getString("mobile")
                val photoUriBase64 = jsonObject.getString("photoURI")

                text_login_name.setText(loginName)
                text_display_name.setText(displayName)
                text_email.setText(email)
                text_mobile.setText(mobile)
                image_holder.setImageBitmap(ImageHelper.ExtendedBase64ToBitmap(photoUriBase64))

                label_password.visibility = View.GONE
                label_confirm_password.visibility = View.GONE
                text_password.visibility = View.GONE
                text_confirm_password.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            finish_button.text = "UPDATE"
        }

        choose_image_button.setOnClickListener { selectImage() }
        finish_button.setOnClickListener {sendToServer() }
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

    private fun sendToServer() {
        try {
            text_login_name.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
            text_display_name.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
            text_email.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
            text_email.validate({ s -> s.isValidEmail() }, "Valid email address required")
            text_mobile.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
            text_mobile.validate({ s -> s.toIntOrNull() != null && s.length >= 8 }, "Valid mobile number required")
            if (!isUpdateNotCreate) {
                text_password.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
                text_password.validate({ s -> s.length >= 6 }, "Minimum length = 6")
                text_confirm_password.validate({ s -> s.isNotEmpty() }, "Cannot be empty!")
            }
            text_confirm_password.validate({ s -> s.trim() == text_password.text.toString().trim() }, "Password is inconsistent!")
            image_holder.validateNotEmpty()


            // finally try to send user details to server
            val photoBase64 = ImageHelper.imageViewToBase64(image_holder)
            if (isUpdateNotCreate) {
                val result = HttpHelper.updateUserById(id!!, text_login_name.text.toString().trim(),
                        text_display_name.text.toString().trim(), text_email.text.toString().trim(),
                        text_mobile.text.toString().trim(), photoBase64)
                val jsonObject = JSONObject(result)
                val okMessage = jsonObject.optString("ok")
                if (okMessage != "") {
                    toast(okMessage)
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                } else {
                    toast("User not updated")
                }
            } else {
                val result = HttpHelper.createNewUser(text_login_name.text.toString().trim(),
                        text_password.text.toString().trim(), text_display_name.text.toString().trim(),
                        text_email.text.toString().trim(), text_mobile.text.toString().trim(), photoBase64)
                val jsonObject = JSONObject(result)
                val message = jsonObject.optString("message")
                if (message != "") {
                    toast(message.toString())
                } else {
                    toast("new user created")
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }
            }
        } catch (e: ValidationException) {
            Log.d("Validation", e.message)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONTENT_REQUEST) {
                image_holder.setImageURI(Uri.fromFile(cameraOutput))
            } else if (requestCode == SELECT_FILE) {
                super.onActivityResult(requestCode, resultCode, data)
                image_holder.setImageURI(data!!.data)
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, Intent())
        finish()
    }

    // Extension function to set validation to EditText easily, return true if validation fails or false otherwise
    @Throws(Exception::class)
    private fun EditText.validate(validator: (String) -> Boolean, message: String) {
        this.afterTextChanged {
            this.error = if (validator(it)) null else message
        }
        this.error = if (validator(this.text.toString())) null else message
        if(!validator(this.text.toString())) throw ValidationException("Validation failed when creating user at tool id: " + this.id)
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { afterTextChanged.invoke(s.toString())}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
    }

    private fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    @Throws(Exception::class)
    private fun ImageView.validateNotEmpty() {
        if(this.drawable == null) {
            toast("Image required for future face login.")
            throw ValidationException("Validation failed when creating user at tool id: " + this.id)
        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}

class ValidationException(message:String): Exception(message)
