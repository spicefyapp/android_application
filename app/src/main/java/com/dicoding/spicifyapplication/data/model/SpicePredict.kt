package com.dicoding.spicifyapplication.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SpicePredict (
    val id: Int,
    val confidence: String,
    var image: Bitmap
): Parcelable