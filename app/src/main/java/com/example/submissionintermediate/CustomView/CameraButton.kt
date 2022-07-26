package com.example.submissionintermediate.CustomView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.submissionintermediate.R

class CameraButton: AppCompatButton {

    private var cameraButton : Drawable? = null
    private var txtColor : Int = 0

    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) :super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = cameraButton

        setTextColor(txtColor)
        textSize = 16f
        gravity = Gravity.CENTER
        text = "Camera"
    }

    private fun init(){
        cameraButton = ContextCompat.getDrawable(context, R.drawable.new_story_custom_button)
        txtColor = ContextCompat.getColor(context, R.color.ice_white)
    }

}