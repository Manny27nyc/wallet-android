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
package com.mycelium.wallet.activity

import android.app.Activity
import android.os.Handler
import android.view.View

import androidx.annotation.IdRes
import androidx.test.espresso.IdlingResource

import java.lang.ref.WeakReference

class ViewVisibilityIdlingResource(view: View, private val visibility: Int) : IdlingResource {
    private val viewWeakReference: WeakReference<View> = WeakReference(view)
    private val name: String

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    constructor(activity: Activity, @IdRes viewId: Int, visibility: Int) : this(activity.findViewById<View>(viewId), visibility)

    init {
        name = "View Visibility for view " + view.id + "(@" + System.identityHashCode(viewWeakReference) + ")"
    }

    override fun getName(): String = name

    override fun isIdleNow(): Boolean {
        val view = viewWeakReference.get()
        val isIdle = view == null || view.visibility == visibility
        if (isIdle) {
            if (resourceCallback != null) {
                resourceCallback!!.onTransitionToIdle()
            }
        } else {
            Handler().postDelayed({ isIdleNow }, IDLE_POLL_DELAY_MILLIS)
        }

        return isIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    companion object {
        private const val IDLE_POLL_DELAY_MILLIS = 100L
    }
}