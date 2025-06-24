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
package com.mycelium.bequant.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mycelium.bequant.common.ErrorHandler
import com.mycelium.bequant.common.loader
import com.mycelium.bequant.receive.viewmodel.ReceiveCommonViewModel
import com.mycelium.bequant.receive.viewmodel.ShowQRViewModel
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.databinding.FragmentBequantReceiveShowQrBinding

class ShowQRFragment : Fragment() {
    val viewModel: ShowQRViewModel by viewModels()
    var parentViewModel: ReceiveCommonViewModel? = null
    var binding: FragmentBequantReceiveShowQrBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            FragmentBequantReceiveShowQrBinding.inflate(inflater, container, false)
                    .apply {
                        binding = this
                        viewModel = this@ShowQRFragment.viewModel
                        parentViewModel = this@ShowQRFragment.parentViewModel
                        lifecycleOwner = this@ShowQRFragment
                    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.copyAddress?.setOnClickListener {
            Utils.setClipboardString(parentViewModel?.address?.value, requireContext())
            Toast.makeText(requireContext(), getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }
        binding?.createNewAddress?.setOnClickListener {
            createDepositAddress(parentViewModel?.currency?.value ?: "BTC")
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun createDepositAddress(currency: String) {
        loader(true)
        viewModel.createDepositAddress(
                currency,
                success = {
                    parentViewModel?.address?.value = it?.address
                    parentViewModel?.tag?.value = it?.paymentId
                },
                error = { _, message ->
                    ErrorHandler(requireContext()).handle(message)
                },
                finally = {
                    loader(false)
                })
    }
}