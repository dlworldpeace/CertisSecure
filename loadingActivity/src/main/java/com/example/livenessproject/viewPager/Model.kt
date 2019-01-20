package com.example.livenessproject.viewPager

import com.megvii.livenessproject.R

enum class Model constructor(val titleResId: Int, val layoutResId: Int) {
    OneToOne(R.string.layout_one, R.layout.activity_main_layout_one),
    OneToMany(R.string.layout_two, R.layout.activity_main_layout_two)
}