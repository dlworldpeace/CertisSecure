package com.example.livenessproject.viewpager

import android.content.Context
import android.content.DialogInterface
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class CustomPagerAdapter(private val mContext: Context) : PagerAdapter() {
    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val modelObject = Model.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup

        // Set onclick event for each page item in ViewPager
        layout.setOnClickListener {
            Toast.makeText(mContext,position.toString(),Toast.LENGTH_SHORT).show()
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