package com.example.livenessproject.viewPager

import com.megvii.livenessproject.R

enum class Model constructor(val titleResId: Int, val layoutResId: Int) {
    FaceDetection(R.string.face_detection, R.layout.activity_main_layout_one),
    OneOfaKindSolution(R.string.one_of_a_kind_solution, R.layout.activity_main_layout_two),
    FaceBasedIdentification(R.string.face_based_identification, R.layout.activity_main_layout_three),
    LivenessDetectionByMotion(R.string.liveness_detection_by_motion, R.layout.activity_main_layout_four)
}