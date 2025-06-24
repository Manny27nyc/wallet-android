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
package com.mycelium.bequant.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.mycelium.bequant.BequantConstants
import com.mycelium.wallet.R


class LoaderFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.layout_bequant_loading, container, false)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }
}

fun Fragment.loader(show: Boolean) {
    if (!isAdded) {
        return
    }
    if (show) {
        val loader = LoaderFragment()
        loader.show(childFragmentManager, BequantConstants.LOADER_TAG)
    } else {
        childFragmentManager.executePendingTransactions()
        val findFragmentByTag = childFragmentManager.findFragmentByTag(BequantConstants.LOADER_TAG)
        if (findFragmentByTag is LoaderFragment && findFragmentByTag.isAdded) {
            findFragmentByTag.dismissAllowingStateLoss()
        }
    }
}

fun AppCompatActivity.loader(show: Boolean) {
    if (show) {
        val loader = LoaderFragment()
        loader.show(supportFragmentManager, BequantConstants.LOADER_TAG)
    } else {
        val findFragmentByTag = supportFragmentManager.findFragmentByTag(BequantConstants.LOADER_TAG)
        if (findFragmentByTag is LoaderFragment && findFragmentByTag.isAdded) {
            findFragmentByTag.dismissAllowingStateLoss()
        }
    }
}