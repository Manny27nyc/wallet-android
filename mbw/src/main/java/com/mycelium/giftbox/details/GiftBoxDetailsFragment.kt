package com.mycelium.giftbox.details

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mycelium.giftbox.*
import com.mycelium.giftbox.client.GitboxAPI
import com.mycelium.giftbox.details.viewmodel.GiftBoxDetailsViewModel
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wallet.databinding.FragmentGiftboxDetailsBinding

class GiftBoxDetailsFragment : Fragment() {
    private var binding: FragmentGiftboxDetailsBinding? = null
    private val args by navArgs<GiftBoxDetailsFragmentArgs>()
    private val viewModel: GiftBoxDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentGiftboxDetailsBinding.inflate(inflater).apply {
                binding = this
                this.viewModel = this@GiftBoxDetailsFragment.viewModel
                this.lifecycleOwner = this@GiftBoxDetailsFragment
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivImage?.loadImage(args.card.productImg)
        (activity as AppCompatActivity).supportActionBar?.title = args.card.productName
        val descriptionClick: (View) -> Unit = {
            viewModel.more.value = !(viewModel.more.value ?: false)
            binding?.layoutDescription?.tvDescription?.setupDescription(
                    viewModel.description.value ?: "",
                    viewModel.more.value ?: false) {
                viewModel.moreVisible.value = it
            }
        }
        binding?.layoutDescription?.more?.setOnClickListener(descriptionClick)
        binding?.layoutDescription?.redeem?.setOnClickListener {
            findNavController().navigate(GiftBoxDetailsFragmentDirections.actionRedeem(viewModel.productInfo!!))
        }
        binding?.layoutDescription?.terms?.setOnClickListener {
            Utils.openWebsite(requireContext(), viewModel.productInfo?.termsAndConditionsPdfUrl)
        }
        binding?.share?.setOnClickListener {
            shareGiftcard(viewModel.orderResponse!!)
        }
        binding?.layoutCode?.redeemCode?.setOnClickListener {
            Utils.setClipboardString(viewModel.redeemCode.value, it.context)
            Toaster(this).toast(R.string.copied_to_clipboard, true)
        }
        binding?.layoutCode?.pinCode?.setOnClickListener {
            Utils.setClipboardString(viewModel.pinCode.value, it.context)
            Toaster(this).toast(R.string.copied_to_clipboard, true)
        }
        viewModel.description.observe(viewLifecycleOwner) { desc ->
            binding?.layoutDescription?.tvDescription?.setupDescription(desc,
                    viewModel.more.value ?: false) {
                viewModel.moreVisible.value = it
            }
        }
        viewModel.setCard(args.card)
        loadProduct()
    }

    private fun loadProduct() {
        GitboxAPI.giftRepository.getProduct(lifecycleScope, args.card.productCode!!, {
            viewModel.setProduct(it!!)
        }, { _, msg ->
            Toaster(this).toast(msg, true)
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.giftbox_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.share -> {
                    shareGiftcard(viewModel.orderResponse!!)
                    true
                }
                R.id.delete -> {
                    AlertDialog.Builder(requireContext(), R.style.MyceliumModern_Dialog)
                            .setTitle(getString(R.string.delete_gift_card))
                            .setMessage(getString(R.string.delete_gift_card_msg))
                            .setNegativeButton(R.string.button_cancel) { _, _ -> }
                            .setPositiveButton(R.string.delete) { _, _ ->
                                GitboxAPI.giftRepository.remove(args.card, lifecycleScope) {
                                    findNavController().popBackStack()
                                }
                            }
                            .create().show()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}