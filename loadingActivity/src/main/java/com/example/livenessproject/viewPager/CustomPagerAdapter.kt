package com.example.livenessproject.viewPager

import android.content.Context
import android.content.Intent
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.livenessproject.OneToOneActivity
import com.example.livenessproject.OneToManyActivity

class CustomPagerAdapter(private val mContext: Context) : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val modelObject = Model.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup

        //val button_try_it = layout.getChildAt(0)
        // Set onclick event for each page item in ViewPager
        layout.setOnClickListener {
            when (position){
                0 -> {
                    val intent = Intent(mContext, OneToOneActivity::class.java)
                    mContext.startActivity(intent)
                }

                1 -> {
                    val intent = Intent(mContext, OneToManyActivity::class.java)
                    mContext.startActivity(intent)
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
        val customPagerEnum = Model.values()[position]
        return mContext.getString(customPagerEnum.titleResId)
    }
}