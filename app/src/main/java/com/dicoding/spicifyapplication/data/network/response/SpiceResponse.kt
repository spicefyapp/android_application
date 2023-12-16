package com.dicoding.spicifyapplication.data.network.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SpiceResponse(

	@field:SerializedName("rempah")
	val rempah: List<RempahItem?>? = null,

	@field:SerializedName("ok")
	val ok: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

@Parcelize
data class RempahItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
):Parcelable
