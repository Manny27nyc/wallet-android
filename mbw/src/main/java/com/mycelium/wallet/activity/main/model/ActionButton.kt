package com.mycelium.wallet.activity.main.model

import com.mycelium.wallet.activity.main.BuySellFragment

import java.util.Objects

class ActionButton @JvmOverloads constructor(val id: BuySellFragment.ACTION, val text: String, val icon: Int = 0) {
    var textColor = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ActionButton?
        return id == that!!.id &&
                icon == that.icon &&
                textColor == that.textColor &&
                text == that.text
    }

    override fun hashCode(): Int {
        return Objects.hash(id, icon, text, textColor)
    }
}
