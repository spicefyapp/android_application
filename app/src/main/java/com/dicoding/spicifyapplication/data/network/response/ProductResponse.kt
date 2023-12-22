package com.dicoding.spicifyapplication.data.network.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(

	@field:SerializedName("rempah")
	val rempah: List<SpiceItem?>? = null,

	@field:SerializedName("ok")
	val ok: Int? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SpiceItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("noWA")
	val noWA: String? = null,

	@field:SerializedName("lan")
	val lan: Double? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
