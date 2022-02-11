package com.mycelium.giftbox

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.mycelium.wallet.R
import kotlinx.android.synthetic.main.activity_gift_box_root.*

class GiftBoxRootActivity : AppCompatActivity(R.layout.activity_gift_box_root) {

    companion object {
        fun start(context: Context){
            context.startActivity(Intent(context, GiftBoxRootActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navController = (nav_host_fragment as NavHostFragment).navController
        val inflater = (nav_host_fragment as NavHostFragment).navController.navInflater
        val graph = inflater.inflate(R.navigation.giftbox)
        nav_host_fragment.findNavController().setGraph(graph, intent.extras)
        setupActionBarWithNavController(navController,
                AppBarConfiguration
                .Builder(R.id.giftBoxBuyResultFragment)
                .build())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}