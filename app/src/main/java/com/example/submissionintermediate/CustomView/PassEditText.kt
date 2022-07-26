package com.example.submissionintermediate.CustomView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import com.google.android.material.textfield.TextInputEditText


class PassEditText : TextInputEditText {

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init()

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "masukkan password"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init(){

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (c.toString().isEmpty()){
                    setError("password tidak boleh kosong")
                } else{
                    setError(null)
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setError(null)
            }

            override fun afterTextChanged(e: Editable?) {
                if (e.toString().length <= 6){
                    setError("password harus lebih dari 6 karakter")
                } else {
                    setError(null)
                }
            }

        })
    }


}