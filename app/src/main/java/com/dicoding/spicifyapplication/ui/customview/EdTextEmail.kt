package com.dicoding.spicifyapplication.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.dicoding.spicifyapplication.R
import com.google.android.material.textfield.TextInputEditText

class EdTextEmail : TextInputEditText {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init () {

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                val isValidEmail =
                    email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

                error = if (isValidEmail) {
                    null
                } else {
                    context.getString(R.string.email_must_be_a_valid_email)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}