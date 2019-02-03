package com.example.livenessproject.viewPager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.livenessproject.DetectFaceActivity
import com.example.livenessproject.OneToOneActivity
import com.example.livenessproject.OneToManyActivity
import com.megvii.livenesslib.LivenessActivity

class CustomPagerAdapter(private val mContext: Context) : PagerAdapter() {

    private val PAGE_INTO_LIVENESS = 100

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val modelObject = Model.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup

        // Set onclick event for each page's ImageButton in ViewPager
        when (position) {
            0 -> {
                val button = layout.getChildAt(0)
                button.setOnClickListener {
                    val intent = Intent(mContext, DetectFaceActivity::class.java)
                    mContext.startActivity(intent)
                }
            }

            1 -> {
                val button = layout.getChildAt(0)
                button.setOnClickListener {
                    val intent = Intent(mContext, OneToOneActivity::class.java)
                    mContext.startActivity(intent)
                }
            }

            2 -> {
                val button = layout.getChildAt(0)
                button.setOnClickListener {
                    val intent = Intent(mContext, OneToManyActivity::class.java)
                    mContext.startActivity(intent)
                }
            }

            3 -> {
                val button = layout.getChildAt(0)
                button.setOnClickListener {
                    val intent = Intent(mContext, LivenessActivity::class.java)
                    (mContext as Activity).startActivityForResult(intent, PAGE_INTO_LIVENESS)
                }
            }
        }

        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return Model.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence {
        //val customPagerEnum = Model.values()[position]
        //return mContext.getString(customPagerEnum.titleResId)

        // hide each viewpager item's title
        return ""
    }
}