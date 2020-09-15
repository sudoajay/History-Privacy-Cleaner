package com.sudoajay.historycachecleaner.activity.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historyprivacycleaner.databinding.ActivityMainBinding
import com.sudoajay.historycachecleaner.firebase.NotificationChannels.notificationOnCreate
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.DarkModeBottomSheet
import com.sudoajay.historycachecleaner.helper.InsetDivider
import kotlinx.coroutines.*
import java.util.*

class MainActivity : BaseActivity()  {

    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var isDarkTheme: Boolean = false
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(
                    false
                ) else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
//            openSetting()
        }


    }


    override fun onResume() {



        super.onResume()
    }


    private fun setReference() {

        //      Setup Swipe RecyclerView



        setSupportActionBar(binding.bottomAppBar)


        setRecyclerView()
    }

    private fun setRecyclerView() {



    }

//    private fun getInsertDivider(): RecyclerView.ItemDecoration {
//        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
//        val dividerColor = ContextCompat.getColor(
//            applicationContext,
//            R.color.divider
//        )
//        val marginLeft = resources.getDimensionPixelSize(R.dimen.divider_inset)
//        return InsetDivider.Builder(this)
//            .orientation(InsetDivider.VERTICAL_LIST)
//            .dividerHeight(dividerHeight)
//            .color(dividerColor)
//            .insets(marginLeft, 0)
//            .build()
//    }



    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(homeShortcutId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            darkModeBottomSheet.tag
        )
    }

//    private fun showNavigationDrawer(){
//        val navigationDrawerBottomSheet = NavigationDrawerBottomSheet()
//        navigationDrawerBottomSheet.show(supportFragmentManager, navigationDrawerBottomSheet.tag)
//    }
//
//    private fun showFilterAppBottomSheet(){
//        val filterAppBottomSheet = FilterAppBottomSheet()
//        filterAppBottomSheet.show(supportFragmentManager, filterAppBottomSheet.tag)
//    }
//
//    private fun openSetting() {
//        val intent = Intent(applicationContext, SettingsActivity::class.java)
//        startActivity(intent)
//    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            android.R.id.home -> showNavigationDrawer()
//            R.id.filterList_optionMenu -> showFilterAppBottomSheet()
//            R.id.darkMode_optionMenu -> showDarkMode()
//            R.id.more_setting_optionMenu -> openSetting()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_toolbar_menu, menu)
        val actionSearch = menu.findItem(R.id.search_optionMenu)
        manageSearch(actionSearch)
        return super.onCreateOptionsMenu(menu)
    }

    private fun manageSearch(searchItem: MenuItem) {
        val searchView =
            searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        manageFabOnSearchItemStatus(searchItem)
        manageInputTextInSearchView(searchView)
    }

    private fun manageFabOnSearchItemStatus(searchItem: MenuItem) {
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                binding.deleteFloatingActionButton.hide()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.deleteFloatingActionButton.show()
                return true
            }
        })
    }

    private fun manageInputTextInSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val query: String = newText.toLowerCase(Locale.ROOT).trim { it <= ' ' }
//                viewModel.filterChanges(query)
                return true
            }
        })
    }




    override fun onBackPressed() {
        onBack()
    }

    private fun onBack() {
        if (doubleBackToExitPressedOnce) {
            closeApp()
            return
        }
        doubleBackToExitPressedOnce = true
        CustomToast.toastIt(applicationContext, "Click Back Again To Exit")
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            doubleBackToExitPressedOnce = false
        }
    }

    private fun closeApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }


    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }

    companion object {
        const val settingShortcutId = "setting"
        const val homeShortcutId = "home"



    }



}
