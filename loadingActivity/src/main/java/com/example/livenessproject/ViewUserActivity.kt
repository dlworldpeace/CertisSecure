package com.example.livenessproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import com.example.livenessproject.util.HttpHelper
import com.example.livenessproject.util.ImageHelper
import com.megvii.livenessproject.R
import kotlinx.android.synthetic.main.activity_view_user.*
import org.json.JSONObject

class ViewUserActivity : AppCompatActivity() {

    private val PAGE_INTO_UPDATE_USER = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_view_user)

        // Allow Network Connection to be made on main thread
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        init()
    }

    private fun init() {
        val id = intent.getStringExtra("id")
        val result = HttpHelper.getUserById(id)

        val jsonObject = JSONObject(result)
        val loginName = jsonObject.getString("loginName")
        val displayName = jsonObject.getString("displayName")
        val email = jsonObject.getString("email")
        val mobile = jsonObject.getString("mobile")
        val photoUriBase64 = jsonObject.getString("photoURI")

        text_login_name.text = loginName
        text_display_name.text = displayName
        text_email.text = email
        text_mobile.text = mobile
        image_holder.setImageBitmap(ImageHelper.extendedBase64ToBitmap(photoUriBase64))

        edit_button.setOnClickListener {
            val intent = Intent(this, AddEditUserActivity::class.java)
            intent.putExtra("id", id)
            startActivityForResult(intent, PAGE_INTO_UPDATE_USER)
        }
        delete_button.setOnClickListener { val result = HttpHelper.deleteUserById(id)
            val jsonObject = JSONObject(result)
            val okMessage = jsonObject.optString("ok")
            if(okMessage != "") {
                toast(okMessage)
                setResult(Activity.RESULT_OK, Intent())
                finish()
            } else {
                toast("delete user failed")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == PAGE_INTO_UPDATE_USER) {
                recreate()
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
