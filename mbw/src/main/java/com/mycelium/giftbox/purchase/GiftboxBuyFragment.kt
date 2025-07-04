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
package com.mycelium.giftbox.purchase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mycelium.bequant.common.loader
import com.mycelium.bequant.kyc.inputPhone.coutrySelector.CountriesSource
import com.mycelium.giftbox.ErrorHandler
import com.mycelium.giftbox.client.GitboxAPI
import com.mycelium.giftbox.client.model.MCProductInfo
import com.mycelium.giftbox.client.model.getCardValue
import com.mycelium.giftbox.loadImage
import com.mycelium.giftbox.purchase.adapter.CustomSimpleAdapter
import com.mycelium.giftbox.purchase.viewmodel.GiftboxBuyViewModel
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.WalletApplication
import com.mycelium.wallet.activity.modern.ModernMain
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wallet.activity.modern.helper.MainActions
import com.mycelium.wallet.databinding.FragmentGiftboxBuyBinding
import com.mycelium.wallet.external.changelly2.viewmodel.ExchangeViewModel
import com.mycelium.wapi.wallet.BroadcastResult
import com.mycelium.wapi.wallet.BroadcastResultType
import com.mycelium.wapi.wallet.Transaction
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.erc20.ERC20Account


class GiftboxBuyFragment : Fragment() {
    private var binding: FragmentGiftboxBuyBinding? = null
    private val args by navArgs<GiftboxBuyFragmentArgs>()

    val viewModel: GiftboxBuyViewModel by viewModels { ViewModelFactory(args.mcproduct) }
    val errorHandler = ErrorHandler().apply {
        cancelListener = {
            findNavController().navigate(GiftboxBuyFragmentDirections.actionGiftBox())
        }
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.getSerializableExtra(AmountInputFragment.AMOUNT_KEY)?.let {
                viewModel.totalAmountFiatSingle.value = it as Value
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.accountId.value = args.accountId
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, IntentFilter(AmountInputFragment.ACTION_AMOUNT_SELECTED))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentGiftboxBuyBinding.inflate(inflater).apply {
            binding = this
            vm = viewModel
            lifecycleOwner = this@GiftboxBuyFragment
        }.root

    val preselectedClickListener: (View) -> Unit = {
        showChoicePreselectedValuesDialog()
    }

    val defaultClickListener: (View) -> Unit = {
        findNavController().navigate(
            GiftboxBuyFragmentDirections.enterAmount(
                args.mcproduct,
                viewModel.totalAmountFiatSingle.value,
                viewModel.quantityInt.value!!,
                args.accountId
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btSend?.isEnabled = viewModel.totalAmountFiatSingle.value != null
        viewModel.totalAmountFiatSingle.value = viewModel.totalAmountFiatSingle.value

        if (args.mcproduct.denominations?.isNotEmpty() == true) {
            binding?.btEnterAmount?.isVisible = false
            binding?.btEnterAmountPreselected?.isVisible = true
            binding?.btEnterAmountPreselected?.background = null
            val isNotSetYet =
                viewModel.totalAmountFiatSingle.value == null || viewModel.totalAmountFiatSingle.value?.isZero() ?: true
            if (isNotSetYet && viewModel.getPreselectedValues().isNotEmpty()) {
                viewModel.totalAmountFiatSingle.value = viewModel.getPreselectedValues()[0]
            }
            binding?.btEnterAmountPreselected?.setOnClickListener(preselectedClickListener)
        } else {
            binding?.btEnterAmountPreselected?.isVisible = false
        }

        viewModel.warningQuantityMessage.observe(viewLifecycleOwner) {
            binding?.tlQuanity?.error = Html.fromHtml(it)
            val isError = !it.isNullOrEmpty()
            binding?.tvQuanity?.setTextColor(
                    ContextCompat.getColor(
                            requireContext(), if (isError) R.color.red_error else R.color.white
                    )
            )
        }
        binding?.tlQuanity?.setOnClickListener {
            val account = viewModel.account
            if (account is ERC20Account && viewModel.warningQuantityMessage.value?.contains(ExchangeViewModel.TAG_ETH_TOP_UP) == true) {
                MbwManager.getInstance(WalletApplication.getInstance()).setSelectedAccount(account.ethAcc.id)
                requireActivity().finishAffinity()
                startActivity(Intent(requireContext(), ModernMain::class.java)
                        .apply { action = MainActions.ACTION_BALANCE })
            }
        }

//        loader(true)
//        GitboxAPI.giftRepository.getProduct(viewModel.viewModelScope,
//                productId = args.product.code!!, success = { productResponse ->
//            val product = productResponse?.product
        val product = args.mcproduct
            binding?.detailsHeader?.ivImage?.loadImage(product?.cardImageUrl,
                    RequestOptions().transforms(CenterCrop(),
                            RoundedCorners(resources.getDimensionPixelSize(R.dimen.giftbox_small_corner))))
            binding?.detailsHeader?.tvName?.text = product?.name
            binding?.detailsHeader?.tvQuantityLabel?.isVisible = false
            binding?.detailsHeader?.tvQuantity?.isVisible = false
            binding?.detailsHeader?.tvCardValueHeader?.text = product?.getCardValue()
//            binding?.detailsHeader?.tvExpire?.text =
//                    if (product?.expiryInMonths != null) "${product.expiryDatePolicy} (${product.expiryInMonths} months)" else "Does not expire"

            binding?.detailsHeader?.tvCountry?.text = product?.countries?.mapNotNull {
                CountriesSource.countryModels.find { model -> model.acronym.equals(it, true) }
            }?.joinToString { it.name }

            binding?.btMinusQuantity?.setOnClickListener {
                if (viewModel.isGrantedMinus.value!!) {
                    viewModel.quantityString.value =
                            ((viewModel.quantityInt.value ?: 0) - 1).toString()
                }
            }
            binding?.btPlusQuantity?.setOnClickListener {
                if (viewModel.isGrantedPlus.value!!) {
                    viewModel.quantityString.value =
                            ((viewModel.quantityInt.value ?: 0) + 1).toString()
                } else {
                    if (viewModel.quantityInt.value!! >= GiftboxBuyViewModel.MAX_QUANTITY) {
                        viewModel.warningQuantityMessage.value =
                                "Max available cards: ${GiftboxBuyViewModel.MAX_QUANTITY} cards"
                    }
                }
            }
        binding?.amountRoot?.setOnClickListener(
            if (args.mcproduct.denominations?.isNotEmpty() == true) preselectedClickListener else defaultClickListener
        )
//        },
//                error = { _, error ->
//                    ErrorHandler(requireContext()).handle(error)
//                }, finally = {
//            loader(false)
//        })

        binding?.btSend?.setOnClickListener {
            MbwManager.getInstance(WalletApplication.getInstance()).runPinProtectedFunction(activity) {
                loader(true)
                binding?.btSend?.isEnabled = false
                GitboxAPI.mcGiftRepository.createOrder(
                        viewModel.viewModelScope,
                        code = args.mcproduct.id!!,
                        amount = viewModel.totalAmountFiatSingle.value?.valueAsBigDecimal!!,
                        quantity = viewModel.quantityString.value?.toInt()!!,
                        amountCurrency = args.mcproduct.currency!!,
                        cryptoCurrency = "BTC",
                        success = { orderResponse ->
                            viewModel.orderResponse.value = orderResponse
                            viewModel.sendTransactionAction.value = Unit
                        },
                        error = { code, error ->
                            loader(false)
                            binding?.btSend?.isEnabled = true
                            errorHandler.handle(requireContext(), code, error)
                        })

                viewModel.sendTransaction.observe(viewLifecycleOwner) {
                    loader(false)
                    val (transaction, broadcastResult) = it
                    broadcastResult(transaction, broadcastResult)
                }
            }
        }
    }

    private fun showChoicePreselectedValuesDialog() {
        val preselectedList = viewModel.getPreselectedValues()
        val preselectedValue = viewModel.totalAmountFiatSingle.value
        val selectedIndex = if (preselectedValue != null) {
            preselectedList.indexOfFirst { it.equalsTo(preselectedValue) }
        } else -1
        val valueAndEnableMap =
            preselectedList.associateWith { it.lessOrEqualThan(viewModel.maxSpendableAmount.value!!) }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_card_value_dialog)
            .setSingleChoiceItems(
                CustomSimpleAdapter(requireContext(), valueAndEnableMap),
                selectedIndex
            )
            { dialog, which ->
                val candidateToSelectIsOk = valueAndEnableMap[preselectedList[which]]
                if (candidateToSelectIsOk == true) {
                    viewModel.totalAmountFiatSingle.value = preselectedList[which]
                    dialog.dismiss()
                } else {
                    Toaster(requireContext()).toast(R.string.insufficient_funds, true)
                }
            }
            .create().show()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    fun broadcastResult(transaction: Transaction?, broadcastResult: BroadcastResult) {
        if (broadcastResult.resultType == BroadcastResultType.SUCCESS) {
            findNavController().navigate(
                GiftboxBuyFragmentDirections.toResult(
                    args.accountId,
                    transaction!!,
                    viewModel.productInfo,
                    viewModel.totalAmountFiat.value!!,
                    viewModel.totalAmountCrypto.value!!,
                    viewModel.minerFeeFiat.value,
                    viewModel.minerFeeCrypto.value,
                    viewModel.orderResponse.value!!
                )
            )
        } else {
            binding?.btSend?.isEnabled = true
            Toaster(requireActivity())
                .toast(broadcastResult.errorMessage ?: broadcastResult.resultType.toString(), false)
        }
    }
}

class ViewModelFactory(param: MCProductInfo) :
    ViewModelProvider.Factory {
    private val mParam: MCProductInfo = param
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GiftboxBuyViewModel(mParam) as T
}