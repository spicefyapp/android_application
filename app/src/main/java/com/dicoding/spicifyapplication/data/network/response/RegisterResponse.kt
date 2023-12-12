package com.dicoding.spicifyapplication.data.network.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
