package com.zhufucdev.bukkithelper

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zhufucdev.bukkithelper.impl.CommonContext
import com.zhufucdev.bukkithelper.impl.CommonServer
import com.zhufucdev.bukkithelper.impl.link.CommonLink
import com.zhufucdev.bukkithelper.manager.DataRefreshDelay
import com.zhufucdev.bukkithelper.manager.KeyManager
import com.zhufucdev.bukkithelper.manager.PluginManager
import com.zhufucdev.bukkithelper.manager.ServerManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // <editor-fold desc="UI" defaultstate="collapsed">
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // </editor-fold>
        // <editor-fold desc="Data" defaultstate="collapsed">
        KeyManager.init(this)
        ServerManager.init(this)
        DataRefreshDelay.init(this)
        CommonContext.init(applicationContext)
        CommonServer.init()
        CommonLink.init()
        // </editor-fold>
        // <editor-fold desc="Plugin" defaultstate="collapsed">
        ServerManager.addConnectionListener {
            PluginManager.enableAll()
        }
        ServerManager.addDisconnectionListener {
            PluginManager.disableAll()
        }
        PluginManager.BuiltIn.registerAll()
        PluginManager.loadAll()
        // </editor-fold>
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }
}