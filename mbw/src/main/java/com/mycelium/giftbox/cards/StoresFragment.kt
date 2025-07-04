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
package com.mycelium.giftbox.cards

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mycelium.giftbox.cards.adapter.StoresAdapter
import com.mycelium.giftbox.cards.event.RefreshOrdersRequest
import com.mycelium.giftbox.cards.viewmodel.GiftBoxViewModel
import com.mycelium.giftbox.cards.viewmodel.StoresViewModel
import com.mycelium.giftbox.client.GiftboxConstants
import com.mycelium.giftbox.client.GitboxAPI
import com.mycelium.giftbox.common.ListState
import com.mycelium.wallet.BuildConfig
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wallet.activity.news.adapter.PaginationScrollListener
import com.mycelium.wallet.activity.view.DividerItemDecoration
import com.mycelium.wallet.databinding.FragmentGiftboxStoresBinding
import com.mycelium.wallet.databinding.ItemGiftBoxTagBinding
import com.mycelium.wallet.event.NetworkConnectionStateChanged
import com.squareup.otto.Subscribe
import kotlinx.coroutines.Job


class StoresFragment : Fragment() {
    private val adapter = StoresAdapter()
    private val viewModel: StoresViewModel by viewModels()
    private val activityViewModel: GiftBoxViewModel by activityViewModels()
    private var binding: FragmentGiftboxStoresBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            FragmentGiftboxStoresBinding.inflate(inflater).apply {
                binding = this
                this.viewModel = this@StoresFragment.viewModel
                this.activityViewModel = this@StoresFragment.activityViewModel
                this.lifecycleOwner = this@StoresFragment
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCategories(activityViewModel.categories.value ?: listOf())
        viewModel.category?.let {
            getTab(it, binding?.tags!!)?.select()

        }
        binding?.tags?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.category = if (tab.tag == "All") null else tab.tag.toString()
                loadData()
            }
        })
        activityViewModel.categories.observe(viewLifecycleOwner) { categories ->
            updateCategories(categories)
        }
        binding?.list?.adapter = adapter
        binding?.list?.itemAnimator = null
        binding?.list?.addItemDecoration(DividerItemDecoration(resources.getDrawable(R.drawable.divider_bequant), VERTICAL))
        adapter.itemClickListener = {
            findNavController().navigate(GiftBoxFragmentDirections.actionStoreDetails(it))
        }
        adapter.tryAgainListener = {
            loadData(skipCache = true)
        }
        binding?.counties?.setOnClickListener {
            findNavController().navigate(GiftBoxFragmentDirections.actionSelectCountries())
        }
        binding?.list?.addOnScrollListener(object : PaginationScrollListener(binding!!.list.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                loadData(viewModel.products.size.toLong())
            }

            override fun isLastPage() = viewModel.isLoaded

            override fun isLoading() = viewModel.state.value == ListState.LOADING
        })
        binding?.searchInput?.doOnTextChanged { _, _, _, _ ->
            viewModel.search.value = binding?.searchInput?.text?.toString()
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                loadData()
            }
        }
        binding?.searchClose?.setOnClickListener {
            binding?.searchInput?.text = null
            hideKeyboard()
        }
        if (viewModel.products.isEmpty() || activityViewModel.reloadStore) {
            loadData()
        }
        if (BuildConfig.DEBUG) {
            var tapCount = 0
            binding?.giftCardLabel?.setOnClickListener {
                tapCount++
                if (tapCount > 5) {
                    GiftboxConstants.TEST = true
                    Toaster(this).toast("You are in test mode gift cards", false)
                }
            }
        }
        MbwManager.getEventBus().register(this)
    }

    private fun updateCategories(categories: List<String>) {
        binding?.tags?.isVisible = categories.isNotEmpty() == true
        binding?.tags?.let { tags ->
            categories.forEach {
                if (getTab(it, tags) == null) {
                    val tab = tags.newTab().setCustomView(
                            ItemGiftBoxTagBinding.inflate(layoutInflater).apply {
                                this.text.text = it.replace("-", " ").capitalize()
                            }.root)
                    tab.tag = it
                    tags.addTab(tab)
                }
            }
            cleanTabs(categories, tags)
        }
    }

    private fun hideKeyboard() {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(binding?.searchInput?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private var productsJob: Job? = null
    private fun loadData(offset: Long = -1, skipCache: Boolean = false) {
        if (offset == -1L) {
            adapter.submitList(List(8) { StoresAdapter.LOADING_ITEM })
            viewModel.isLoaded = false
            productsJob?.cancel()
        } else if (viewModel.isLoaded) {
            return
        } else {
            if (!adapter.currentList.contains(StoresAdapter.LOADING_ITEM)) {
                adapter.submitList(adapter.currentList + StoresAdapter.LOADING_ITEM)
            }
        }
        activityViewModel.reloadStore = false
        viewModel.state.value = ListState.LOADING
        productsJob = GitboxAPI.mcGiftRepository.getProducts(lifecycleScope,
                search = viewModel.search.value,
                category = viewModel.category,
                country = activityViewModel.selectedCountries.value?.firstOrNull(),
                offset = if (offset == -1L) 0 else offset.toInt(),
                limit = 30,
                skipCache = skipCache,
                success = {
                    viewModel.state.value = ListState.OK
                    if(it?.products?.isEmpty() != false) {
                        viewModel.isLoaded = true
                    }
                    val categories = it?.categories.orEmpty()
                    activityViewModel.categories.value = listOf("All") + categories
                    activityViewModel.countries.value = it?.countries
                    viewModel.setProducts(it?.products.orEmpty(), offset != -1L)
                    adapter.submitList(viewModel.products.toList())
                },
                error = { code, msg ->
                    adapter.submitList(listOf(StoresAdapter.ERROR_ITEM))
                    viewModel.state.value = ListState.ERROR
                    if(code != 400) {
                        Toaster(this).toast(msg, true)
                    }
                    Log.e("StoresFragment", "loadData error: $msg")
                })
    }

    override fun onDestroyView() {
        MbwManager.getEventBus().unregister(this)
        binding?.list?.clearOnScrollListeners()
        binding = null
        super.onDestroyView()
    }

    private fun getTab(category: String, tabLayout: TabLayout): TabLayout.Tab? {
        for (i in 0 until tabLayout.tabCount) {
            if (tabLayout.getTabAt(i)?.tag == category) {
                return tabLayout.getTabAt(i)
            }
        }
        return null
    }

    private fun cleanTabs(list: List<String>, tabLayout: TabLayout) {
        for (i in tabLayout.tabCount - 1 downTo 0) {
            if (!list.contains(tabLayout.getTabAt(i)?.tag)) {
                tabLayout.removeTab(tabLayout.getTabAt(i)!!)
            }
        }
    }

    @Subscribe
    internal fun updateOrder(request: RefreshOrdersRequest) {
//        loadData(skipCache = true) TODO
    }

    @Subscribe
    fun networkConnectionChanged(event: NetworkConnectionStateChanged){
        if(event.connected) {
            loadData()
        }
    }

}