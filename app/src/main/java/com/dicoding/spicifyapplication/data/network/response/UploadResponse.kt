package com.dicoding.spicifyapplication.data.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadResponse(
	val accuracy: String,
	val description: String,
	val label: String
) : Parcelable

