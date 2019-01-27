package com.example.livenessproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.livenessproject.util.ImageHelper
import com.example.livenessproject.viewPager.CustomPagerAdapter
import com.megvii.livenessproject.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        // Configure details of account logged in
        val json:String = intent.getStringExtra("json")
        val user = JSONObject(json).getJSONObject("user")
        val photoURI = user.getString("photoURI")
        val displayName = user.getString("displayName")
        val photoData = ImageHelper.extendedBase64ToBitmap(photoURI)
        val circularPhotoData = ImageHelper.getCroppedCircleBitmap(photoData)

        drawer_image.setImageBitmap(circularPhotoData)
        drawer_user_name.text = displayName

        // Configure action bar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.app_name)

        // Configure the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close){}

        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.setDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set navigation view navigation item selected listener
        navigation_view.setNavigationItemSelectedListener{
            when (it.itemId){
                R.id.action_usermanagement ->{ startActivity(Intent(this, UserManagementActivity::class.java)) }
                R.id.action_logout -> { startActivity(Intent(this, LoadingActivity::class.java)) }
            }

            // Close the drawer
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        // Inflate viewPager with pictures and OnclickListener
        view_pager.adapter = CustomPagerAdapter(this)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true)
        }

        this.doubleBackToExitPressedOnce = true
        toast("Press BACK again to exit")

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }
}
