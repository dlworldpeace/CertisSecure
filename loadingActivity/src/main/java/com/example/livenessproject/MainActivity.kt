package com.example.livenessproject

import android.app.Activity
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

    private val PAGE_INTO_VIEW_USER = 102
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        // Configure details of account logged in
        val json:String = intent.getStringExtra("json")
        val user = JSONObject(json)
        val id = user.getString("_id")
        val photoURI = user.getString("photoURI")
        val displayName = user.getString("displayName")
        val photoData = ImageHelper.extendedBase64ToBitmap(photoURI)
        val circularPhotoData = ImageHelper.getCroppedCircleBitmap(photoData)

        drawer_image.setImageBitmap(circularPhotoData)
        drawer_user_name.text = displayName
        drawer_header_profile.setOnClickListener {
            val intent = Intent(this, ViewUserActivity::class.java)
            intent.putExtra("id", id)
            startActivityForResult(intent, PAGE_INTO_VIEW_USER)
        }

        // Configure action bar
        //setSupportActionBar(toolbar)
        //val actionBar = supportActionBar
        //actionBar?.title = getString(R.string.welcome)

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
                R.id.action_logout -> { startActivity(Intent(this, LoginActivity::class.java)) }
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        // Inflate viewPager with pages, OnclickListener and dots indicator below
        view_pager.adapter = CustomPagerAdapter(this)
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == PAGE_INTO_VIEW_USER) {
                recreate()
            }
        }
    }

    // Exit app only on back button presses twice consecutively
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
