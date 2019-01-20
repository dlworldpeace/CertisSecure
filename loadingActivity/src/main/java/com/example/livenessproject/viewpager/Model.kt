package com.example.livenessproject.viewpager

import com.megvii.livenessproject.R

enum class Model constructor(val titleResId: Int, val layoutResId: Int) {
    RED(R.string.layout_one, R.layout.activity_main_layout_one),
    BLUE(R.string.layout_two, R.layout.activity_main_layout_two)
}