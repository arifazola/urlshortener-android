package com.main.urlshort

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.main.urlshort.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        sharedPreferences = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
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
        val shorten = bottomSheetView.findViewById<Button>(R.id.btnShorten)

        close.setOnClickListener {
            bottomSheet.dialog.dismiss()
        }

        shorten.setOnClickListener {
            viewModel.respond.removeObservers(this)
            val orgUrl = bottomSheetView.findViewById<TextInputLayout>(R.id.tilDestinationUrl)
            val inputCustom = bottomSheetView.findViewById<TextInputLayout>(R.id.tilbackhalf)
            val createdBy = sharedPreferences.getString("userid", null)
            viewModel.shortUrl(orgUrl.editText?.text.toString(), inputCustom.editText?.text.toString(), createdBy!!)

            viewModel.respond.observe(this){
                Log.i("Short URL Data", it.toString())
                if(it?.error?.get(0)?.orgUrl != null){
                    orgUrl.isErrorEnabled = true
                    orgUrl.error = it.error?.get(0)?.orgUrl
                } else {
                    orgUrl.isErrorEnabled = false
                }

                if(it?.error?.get(0)?.inputCustom != null){
                    inputCustom.isErrorEnabled = true
                    inputCustom.error = it.error?.get(0)?.inputCustom
                } else {
                    inputCustom.isErrorEnabled = true
                }

                if (it?.data?.get(0)?.msg == "Duplicate"){
                    inputCustom.isErrorEnabled = true
                    inputCustom.error = "Back-half is already taken. Try another one"
                }

                if(it?.data?.get(0)?.msg == true){
                    recreate()
                }

                if(it?.data?.get(0)?.msg == false){
                    Utils.showToast(this, "Server Error. Please try again")
                }
            }
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
            R.id.nav_lib -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.libEditFragment)
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
            binding.fabAddLink.visibility = View.GONE
        } else {
            supportActionBar?.setDisplayShowCustomEnabled(false)
            binding.fabAddLink.visibility = View.VISIBLE
        }
    }
}