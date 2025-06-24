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
import androidx.test.espresso.IdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

class ActivityIdlingResource(private val activity: Class<out Activity>) : IdlingResource {
    private val name = "activity " + activity.name + "(@" + System.identityHashCode(activity) + ")"

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    private val activityInstance: Activity?
        get() = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                .firstOrNull()

    override fun getName(): String = name

    override fun isIdleNow(): Boolean {
        if (activityInstance == null || resourceCallback == null){
            return false
        }
        val isIdle = activity != activityInstance!!.javaClass
        if (isIdle && resourceCallback != null) {
            resourceCallback!!.onTransitionToIdle()
        }
        return isIdle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }
}