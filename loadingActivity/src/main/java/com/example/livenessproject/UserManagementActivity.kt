package com.example.livenessproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.example.livenessproject.util.HttpHelper
import com.example.livenessproject.util.ImageHelper
import com.megvii.livenessproject.R
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_user_management.*
import kotlinx.android.synthetic.main.activity_user_management_list_item.view.*
import org.json.JSONArray
import org.json.JSONObject

class UserManagementActivity : AppCompatActivity() {

    private val mArrayList = ArrayList<ArrayList<String>>()
    private var mListDataAdapter: ListDataAdapter? = null
    private val PAGE_INTO_CREATE_USER = 100
    private val PAGE_INTO_UPDATE_USER = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        // Allow Network Connection to be made on main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        initActionBar()
        initSwipeListView()
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.user_management)
    }

    private fun initSwipeListView() {

        swipe_list_view.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT)

        val result = HttpHelper.listAllUsers()
        val jsonArray = JSONArray(result)
        for (i in 0..(jsonArray.length() - 1)) {
            val user = jsonArray.getJSONObject(i)
            val delFlag = user.get("delFlag") as Boolean
            val photoURI = user.get("photoURI") as String
            val displayName = user.get("displayName") as String
            val id = user.get("_id") as String

            if(!delFlag) {
                val innerList: ArrayList<String> = arrayListOf(photoURI, displayName, id)
                mArrayList.add(innerList)
            }
        }

        mListDataAdapter = ListDataAdapter()
        swipe_list_view.adapter = mListDataAdapter

        val creator = SwipeMenuCreator { menu ->
            // add "edit" button to swipe menu
            val editItem = SwipeMenuItem(applicationContext)
            editItem.background = ColorDrawable(Color.rgb(0x30, 0xB1,0xF5))
            editItem.width = dpTopx(90)
            editItem.setIcon(R.drawable.ic_writing)
            menu.addMenuItem(editItem)

            // add "delete" button to swipe menu
            val deleteItem = SwipeMenuItem(applicationContext)
            deleteItem.background = ColorDrawable(Color.rgb(0xF9,0x3F, 0x25))
            deleteItem.width = dpTopx(90)
            deleteItem.setIcon(R.drawable.ic_rubbish_bin)
            menu.addMenuItem(deleteItem)
        }

        swipe_list_view.setMenuCreator(creator)

        swipe_list_view.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> {
                    val intent = Intent(this, AddEditUserActivity::class.java)
                    intent.putExtra("id", mArrayList[position][2])
                    startActivityForResult(intent, PAGE_INTO_UPDATE_USER)
                }
                1 -> {
                    val result = HttpHelper.deleteUserById(mArrayList[position][2])
                    val jsonObject = JSONObject(result)
                    val okMessage = jsonObject.optString("ok")
                    if(okMessage != "") {
                        mArrayList.removeAt(position)
                        mListDataAdapter!!.notifyDataSetChanged()
                        toast(okMessage)
                    } else {
                        toast("delete user failed")
                    }
                }
            }
            true
        }

        swipe_list_view.setOnMenuStateChangeListener(object : SwipeMenuListView.OnMenuStateChangeListener {
            override fun onMenuOpen(position: Int) {}
            override fun onMenuClose(position: Int) {}
        })

        swipe_list_view.setOnSwipeListener(object : SwipeMenuListView.OnSwipeListener {
            override fun onSwipeStart(position: Int) {}
            override fun onSwipeEnd(position: Int) {}
        })

        swipe_list_view.setOnItemClickListener { parent, view, position, id ->
            toast(view.user_management_id.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_add) {
            val intent = Intent(this, AddEditUserActivity::class.java)
            startActivityForResult(intent, PAGE_INTO_CREATE_USER)
        }

        return super.onOptionsItemSelected(item)
    }

    internal inner class ListDataAdapter : BaseAdapter() {

        private var holder: ViewHolder? = null

        override fun getCount(): Int {
            return mArrayList.size
        }

        override fun getItem(i: Int): Any? {
            return null
        }

        override fun getItemId(i: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                holder = ViewHolder()
                convertView = layoutInflater.inflate(R.layout.activity_user_management_list_item, null)
                holder!!.mImage = convertView!!.findViewById(R.id.user_management_image) as ImageView
                holder!!.mDisplayName = convertView!!.findViewById(R.id.user_management_diaplay_name) as TextView
                holder!!.mId = convertView!!.findViewById(R.id.user_management_id) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val photoData = ImageHelper.extendedBase64ToBitmap(mArrayList[position][0])
            val circularPhotoData = ImageHelper.getCroppedCircleBitmap(photoData)
            holder!!.mImage!!.setImageBitmap(circularPhotoData)
            holder!!.mDisplayName!!.text = mArrayList[position][1]
            holder!!.mId!!.text = mArrayList[position][2] // hide each user's id here
            return convertView
        }

        internal inner class ViewHolder {
            var mImage: ImageView? = null
            var mDisplayName: TextView? = null
            var mId : TextView? = null
        }
    }

    private fun dpTopx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == PAGE_INTO_CREATE_USER || requestCode == PAGE_INTO_UPDATE_USER) {
                recreate()
            }
        }
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
