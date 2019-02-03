package com.example.livenessproject.viewPager

import com.megvii.livenessproject.R

enum class Model constructor(val titleResId: Int, val layoutResId: Int) {
    FaceDetection(R.string.face_detection, R.layout.activity_main_fragment_face_detection),
    OneOfaKindSolution(R.string.one_of_a_kind_solution, R.layout.activity_main_fragment_one_of_a_kind_solution),
    FaceBasedIdentification(R.string.face_based_identification, R.layout.activity_main_fragment_face_identification),
    LivenessDetectionByMotion(R.string.liveness_detection_by_motion, R.layout.activity_main_fragment_liveness_detection)
}