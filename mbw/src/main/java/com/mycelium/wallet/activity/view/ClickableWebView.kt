/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wallet.activity.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView

/**
 * class copied from
 * https://github.com/AhmadNemati/ClickableWebView/blob/master/clickablewebview/src/main/java/com/ahmadnemati/clickablewebview/ClickableWebView.java
 */
class ClickableWebView : WebView, View.OnClickListener, View.OnTouchListener {
    var imageClicklistener: ((String?) -> Unit)? = null
    private var startClickTime: Long = 0

    init {
        setOnClickListener(this)
        setOnTouchListener(this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onClick(view: View) {
        val hr = hitTestResult
        try {
            if (hr.type == IMAGE_TYPE) {
                imageClicklistener?.invoke(hr.extra)
            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startClickTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                val clickDuration = System.currentTimeMillis() - startClickTime
                if (clickDuration < MAX_CLICK_DURATION) {
                    performClick()
                }
            }
        }
        return false
    }

    companion object {
        private const val MAX_CLICK_DURATION = 200
        private const val IMAGE_TYPE = 5
    }
}