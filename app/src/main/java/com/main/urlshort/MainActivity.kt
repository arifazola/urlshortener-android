package com.main.urlshort

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.main.urlshort.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var viewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var token = ""
    private var isInLink: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        sharedPreferences = getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        appBarConfig = AppBarConfiguration(
            setOf(R.id.dashboardFragment, R.id.linksFragment, R.id.libListFragment, R.id.performanceFragment),
            binding.drawerLayout
        )
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

    private fun openBottomSheet() {
        val bottomSheet = BottomSheetExtension(this)
        val bottomSheetView = bottomSheet.showBottomSheetDialog(
            this,
            R.layout.add_link,
            null,
            null,
            true,
            true,
            layoutInflater
        )

        val close = bottomSheetView.findViewById<Button>(R.id.btnCloseAdd)
        val shorten = bottomSheetView.findViewById<Button>(R.id.btnShorten)
        val inputCustom = bottomSheetView.findViewById<TextInputLayout>(R.id.tilbackhalf)
        val accountType = sharedPreferences.getString("accountType", null)
        if(accountType == "free"){
            inputCustom.isEnabled = false
            inputCustom.isErrorEnabled = true
            inputCustom.error = "Custom back-half is not available for Free Users. Please Upgrade your account"
        } else {
            inputCustom.isEnabled = true
            inputCustom.isErrorEnabled = false
        }

        close.setOnClickListener {
            bottomSheet.dialog.dismiss()
        }

        shorten.setOnClickListener {
            viewModel.respond.removeObservers(this)
            val orgUrl = bottomSheetView.findViewById<TextInputLayout>(R.id.tilDestinationUrl)
            val inputCustom = bottomSheetView.findViewById<TextInputLayout>(R.id.tilbackhalf)
            val createdBy = sharedPreferences.getString("userid", null)
            token = sharedPreferences.getString("token", null).toString()
            viewModel.shortUrl(
                orgUrl.editText?.text.toString(),
                inputCustom.editText?.text.toString(),
                createdBy!!,
                accountType.toString(),
                token
            )

            viewModel.loading.observe(this){
                if(it == true){
                    shorten.text = "Shorting Link"
                    shorten.isEnabled = false
                } else if(it == false){
                    shorten.text = "Shorten Link"
                    shorten.isEnabled = true
                }
            }

            viewModel.respond.observe(this) {
                Log.i("Short URL Data", it.toString())
                Utils.sharedPreferenceString(sharedPreferences, "token", it?.token.toString())
                token = it?.token.toString()
                if (it?.error?.get(0)?.orgUrl != null) {
                    orgUrl.isErrorEnabled = true
                    orgUrl.error = it.error.get(0).orgUrl!!
                } else {
                    orgUrl.isErrorEnabled = false
                }

                if (it?.error?.get(0)?.inputCustom != null) {
                    inputCustom.isErrorEnabled = true
                    inputCustom.error = it.error?.get(0)?.inputCustom
                } else {
                    inputCustom.isErrorEnabled = true
                }

                if (it?.error?.get(0)?.errorMsg != null) {
                    Toast.makeText(this, it.error?.get(0)?.errorMsg, Toast.LENGTH_LONG).show()
                }

                if (it?.data?.get(0)?.msg == "Duplicate") {
                    inputCustom.isErrorEnabled = true
                    inputCustom.error = "Back-half is already taken. Try another one"
                }

                if (it?.data?.get(0)?.msg == true) {
                    if(isInLink == true){
                        bottomSheet.dialog.dismiss()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.linksFragment)
                    } else {
                        recreate()
                    }
                }

                if (it?.data?.get(0)?.msg == false) {
                    Utils.showToast(this, "Server Error. Please try again")
                }

                if(it?.error?.get(0)?.invalidToken != null){
                    Utils.showToast(this, it.error.get(0).invalidToken.toString())
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
        when (item.itemId) {
            R.id.nav_home -> {
//                drawer.closeDrawers()
                findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFragment)
                Handler().post { drawer.closeDrawers() }
//                drawer.addDrawerListener(object : DrawerListener{
//                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//                    }
//
//                    override fun onDrawerOpened(drawerView: View) {
//
//                    }
//
//                    override fun onDrawerClosed(drawerView: View) {
//                        findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFragment)
//                    }
//
//                    override fun onDrawerStateChanged(newState: Int) {
//
//                    }
//
//                })
                return true

            }
            R.id.nav_link -> {
//                drawer.closeDrawers()
                findNavController(R.id.nav_host_fragment).navigate(R.id.linksFragment)
                Handler().post { drawer.closeDrawers() }
//                drawer.addDrawerListener(object : DrawerListener{
//                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//                    }
//
//                    override fun onDrawerOpened(drawerView: View) {
//
//                    }
//
//                    override fun onDrawerClosed(drawerView: View) {
//                        findNavController(R.id.nav_host_fragment).navigate(R.id.linksFragment)
//                    }
//
//                    override fun onDrawerStateChanged(newState: Int) {
//
//                    }
//
//                })
                return true
            }
            R.id.nav_lib -> {
//                drawer.closeDrawers()
                findNavController(R.id.nav_host_fragment).navigate(R.id.libListFragment)
                Handler().post { drawer.closeDrawers() }
//                drawer.addDrawerListener(object : DrawerListener{
//                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//                    }
//
//                    override fun onDrawerOpened(drawerView: View) {
//
//                    }
//
//                    override fun onDrawerClosed(drawerView: View) {
//                        findNavController(R.id.nav_host_fragment).navigate(R.id.libListFragment)
//                    }
//
//                    override fun onDrawerStateChanged(newState: Int) {
//
//                    }
//
//                })
                return true
            }
            R.id.nav_performance -> {
//                drawer.closeDrawers()
                findNavController(R.id.nav_host_fragment).navigate(R.id.performanceFragment)
                Handler().post { drawer.closeDrawers() }
//                drawer.addDrawerListener(object : DrawerListener{
//                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
//
//                    }
//
//                    override fun onDrawerOpened(drawerView: View) {
//
//                    }
//
//                    override fun onDrawerClosed(drawerView: View) {
//                        findNavController(R.id.nav_host_fragment).navigate(R.id.performanceFragment)
//                    }
//
//                    override fun onDrawerStateChanged(newState: Int) {
//
//                    }
//
//                })
                return true
            }
            R.id.nav_signout -> {
                val googleAuth = GoogleAuth(this)
//                googleAuth.initialize(this)
                googleAuth.googleSignInClient.signOut().addOnCompleteListener {
                    Utils.removeSharedPreferences(sharedPreferences, "userid")
                    Utils.removeSharedPreferences(sharedPreferences, "fullname")
                    Utils.removeSharedPreferences(sharedPreferences, "email")
                    Utils.removeSharedPreferences(sharedPreferences, "accountType")
                    Utils.removeSharedPreferences(sharedPreferences, "token")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
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
        if (destination.id == R.id.linkDetailFragment) {
            supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE)
            supportActionBar?.setDisplayShowCustomEnabled(true)
            supportActionBar?.setCustomView(R.layout.custom_bar)
            binding.fabAddLink.visibility = View.GONE
            binding.fabSeePreview.visibility = View.GONE
            isInLink = false
//            Utils.sharedPreferenceString(sharedPreferences, "from_detail", "1")
        } else if (destination.id == R.id.libEditFragment) {
            supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM or ActionBar.DISPLAY_SHOW_HOME or ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE)
            supportActionBar?.setDisplayShowCustomEnabled(true)
            supportActionBar?.setCustomView(R.layout.custom_bar_lib)
            binding.fabAddLink.visibility = View.GONE
            binding.fabSeePreview.visibility = View.VISIBLE
            isInLink = false
//            Utils.removeSharedPreferences(sharedPreferences, "from_detail")
        }  else if(destination.id == R.id.linksFragment) {
            supportActionBar?.setDisplayShowCustomEnabled(false)
            binding.fabAddLink.visibility = View.VISIBLE
            binding.fabSeePreview.visibility = View.GONE
            isInLink = true
//            Utils.sharedPreferenceString(sharedPreferences, "in_link", "1")
        }else if(destination.id == R.id.QRFragment){
            supportActionBar?.setDisplayShowCustomEnabled(false)
            binding.fabAddLink.visibility = View.GONE
            binding.fabSeePreview.visibility = View.GONE
            isInLink = false
        }else {
            supportActionBar?.setDisplayShowCustomEnabled(false)
            binding.fabAddLink.visibility = View.VISIBLE
            binding.fabSeePreview.visibility = View.GONE
            isInLink = false
//            Utils.removeSharedPreferences(sharedPreferences, "from_detail")
        }
    }
}