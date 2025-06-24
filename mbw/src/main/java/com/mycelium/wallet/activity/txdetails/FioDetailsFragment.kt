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
package com.mycelium.wallet.activity.txdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.mycelium.wallet.activity.util.AddressLabel
import com.mycelium.wallet.databinding.TransactionDetailsFioBinding
import com.mycelium.wapi.wallet.TransactionSummary
import com.mycelium.wapi.wallet.fio.FioTransactionSummary

class FioDetailsFragment : DetailsFragment() {

    var binding: TransactionDetailsFioBinding? = null

    private val tx: FioTransactionSummary by lazy {
        requireArguments().getSerializable("tx") as FioTransactionSummary
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            TransactionDetailsFioBinding.inflate(inflater, container, false)
                .apply {
                    binding = this
                }
                .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUi()
    }

    private fun updateUi() {
        binding?.run {
            alignTables(specificTable)
            val fromAddress = AddressLabel(requireContext())
            fromAddress.address = tx.sender
            llFrom.addView(fromAddress)

            val toAddress = AddressLabel(requireContext())
            toAddress.address = tx.receiver
            llTo.addView(toAddress)

            llValue.addView(getValue(tx.sum, null))
            llFee.addView(getValue(tx.fee!!, null))
            if (tx.memo?.isNotEmpty() == true) {
                tvMemo?.text = tx.memo
                memoRow?.visibility = VISIBLE
            } else {
                memoRow?.visibility = GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(tx: TransactionSummary): FioDetailsFragment {
            val f = FioDetailsFragment()
            val args = Bundle()

            args.putSerializable("tx", tx)
            f.arguments = args
            return f
        }
    }

}