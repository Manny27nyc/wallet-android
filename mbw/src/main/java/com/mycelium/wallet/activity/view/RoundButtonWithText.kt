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
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.mycelium.wallet.R
import com.mycelium.wallet.databinding.ButtonBigRoundWithTextBinding


class RoundButtonWithText(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    var binding: ButtonBigRoundWithTextBinding
    init {
        binding = ButtonBigRoundWithTextBinding.inflate(LayoutInflater.from(context), this, true)

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundButtonWithText, 0, 0)
        try {
            val circleColor = a.getColor(R.styleable.RoundButtonWithText_backgroundColor
                    , resources.getColor(R.color.black))
            binding.roundButton.circleColor = circleColor

            val icon = a.getDrawable(R.styleable.RoundButtonWithText_icon)
            binding.roundButton.setImageDrawable(icon)

            val text = a.getString(R.styleable.RoundButtonWithText_text)
            binding.textView.text = text
        } finally {
            a.recycle()
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.roundButton.setOnClickListener(l)
    }
}
