package com.example.livenessproject

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.megvii.livenessproject.R
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_user_management.*
import kotlinx.android.synthetic.main.activity_user_management_list_item.view.*

class UserManagementActivity : AppCompatActivity() {

    private val mArrayList = ArrayList<String>()
    private var mListDataAdapter: ListDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
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
        for (i in 1..5){
            mArrayList.add("List item --> $i")
        }

        mListDataAdapter = ListDataAdapter()
        swipe_list_view.adapter = mListDataAdapter

        val creator = SwipeMenuCreator { menu ->
//            editItem.background = ColorDrawable(Color.rgb(0x30, 0xB1,0xF5))

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
                    mArrayList.removeAt(position)
                    mListDataAdapter!!.notifyDataSetChanged()
                    toast("Item deleted")
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
            toast(view.user_management_text_view.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_add) {
            //TODO add item to list from here
            mArrayList.add("List item --> " + mArrayList.size)
            mListDataAdapter!!.notifyDataSetChanged()
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
                holder!!.mTextview = convertView!!.findViewById(R.id.user_management_text_view) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder!!.mTextview!!.text = mArrayList[position]
            return convertView
        }

        internal inner class ViewHolder {
            var mTextview: TextView? = null
        }
    }

    private fun dpTopx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
