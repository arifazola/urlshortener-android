package com.main.urlshort

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.main.urlshort.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var appBarConfig: AppBarConfiguration
//    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        appBarConfig = AppBarConfiguration(setOf(R.id.dashboardFragment, R.id.linksFragment), binding.drawerLayout)
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        drawer = binding.drawerLayout
        val navController = this.findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener(this)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
        NavigationUI.setupWithNavController(binding.navView, navController)


        binding.navView.setNavigationItemSelectedListener(this)

        binding.fabAddLink.setOnClickListener {
            openBottomSheet()
        }

    }

    private fun openBottomSheet(){
        val bottomSheet = BottomSheetExtension(this)
        val bottomSheetView = bottomSheet.showBottomSheetDialog(this, R.layout.add_link, null, null, true, true, layoutInflater)
        val close = bottomSheetView.findViewById<Button>(R.id.btnCloseAdd)

        close.setOnClickListener {
            bottomSheet.dialog.dismiss()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfig)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("Nav Selected", "Selected")
        when(item.itemId){
            R.id.nav_home -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFragment)
                return true
            }
            R.id.nav_link -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.linksFragment)
                return true
            }
            else -> return false
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if(destination.id == R.id.linkDetailFragment){
            supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE)
            supportActionBar?.setDisplayShowCustomEnabled(true)
            supportActionBar?.setCustomView(R.layout.custom_bar)
        } else {
            supportActionBar?.setDisplayShowCustomEnabled(false)
        }
    }
}