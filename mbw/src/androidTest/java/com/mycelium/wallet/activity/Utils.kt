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
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

inline fun <reified T : Activity> waitForActivity(timeout: Long = 10000L) {
    runBlocking {
        withTimeout(timeout) {
            do {
                delay(300L)
            } while (getCurrentActivity() !is T)
        }
    }
}

inline fun <reified T : Activity> waitWhileAcitvity(): ActivityIdlingResource {
    val activityIdlingResource = ActivityIdlingResource(T::class.java)
    IdlingRegistry.getInstance().register(activityIdlingResource)
    return activityIdlingResource
}

fun awaitCondition(
    timeout: Long = 10000,
    checkInterval: Long = 200,
    condition: () -> Boolean
) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeout) {
        if (condition()) return
        Thread.sleep(checkInterval)
    }
    throw AssertionError("Condition not met within timeout")
}

fun getCurrentActivity(): Activity? {
    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    val arrayOfActivities = arrayOfNulls<Activity>(1)
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
        arrayOfActivities[0] = activities.first()
    }
    return arrayOfActivities[0]
}

fun customKeyboardType(message: String) {
    message.toCharArray().forEach {
        Espresso.onView(ViewMatchers.withText(it.toString())).perform(ViewActions.click())
    }
}