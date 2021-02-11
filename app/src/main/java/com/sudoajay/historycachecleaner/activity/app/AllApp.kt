package com.sudoajay.historycachecleaner.activity.app

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historycachecleaner.activity.progress.ProgressActivity
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.DarkModeBottomSheet
import com.sudoajay.historycachecleaner.helper.InsetDivider
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidSdCardPermission
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityAllAppBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AllApp : BaseActivity(), FilterAppBottomSheet.IsSelectedBottomSheetFragment {

    lateinit var viewModel: AllAppViewModel
    private lateinit var binding: ActivityAllAppBinding
    private var isDarkTheme: Boolean = false
    private var TAG = "AllActivityTag"
    private var androidExternalStoragePermission: AndroidExternalStoragePermission? = null
    private var sdCardPermission: AndroidSdCardPermission? = null
    private lateinit var selectedList: MutableList<App>




    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode.value?:true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(
                    false
                ) else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_app)

        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(AllAppViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

//        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
//           openSetting()
//        }

//        External and sd-Card permission
//        permissionIssue()
    }


    override fun onResume() {


        binding.deleteFloatingActionButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    selectedList = viewModel.appRepository.getSelectedApp()
                }

                if (selectedList.isEmpty())
                    CustomToast.toastIt(
                        applicationContext,
                        getString(R.string.alert_dialog_no_app_selected_title)
                    )
                else {

                    generateAlertDialog(
                        getString(R.string.alert_dialog_ask_permission_to_remove_apps_title),
                        getString(R.string.alert_dialog_ask_permission_to_remove_apps_message),
                        getString(R.string.no_text),
                        getString(R.string.yes_text)
                    )

                }
            }
        }

        setReference()
        super.onResume()
    }



    private fun setReference() {

        //      Setup Swipe RecyclerView
        binding.swipeRefresh.setColorSchemeResources(
            if (isDarkTheme) R.color.swipeSchemeDarkColor else R.color.swipeSchemeColor
        )
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                applicationContext,
                if (isDarkTheme) R.color.swipeBgDarkColor else R.color.swipeBgColor

            )
        )


//         Setup BottomAppBar Navigation Setup
        binding.bottomAppBar.navigationIcon?.mutate()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setTint(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.navigationIconDarkColor else R.color.navigationIconColor
                    )
                )
            }
            binding.bottomAppBar.navigationIcon = it
        }

        setSupportActionBar(binding.bottomAppBar)


        setRecyclerView()
    }

    private fun setRecyclerView() {

        val recyclerView = binding.recyclerView
        val divider = getInsertDivider()
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pagingAppRecyclerAdapter = PagingAppRecyclerAdapter(applicationContext, this)
        recyclerView.adapter = pagingAppRecyclerAdapter

        lifecycleScope.launch {
            viewModel.appList.collectLatest {
                pagingAppRecyclerAdapter.submitData(it)
            }
        }
//        viewModel.appList.observe(this, {
//            lifecycleScope.launch {
//                if (!viewModel.stopObservingData) {
//                    Log.e(TAG, "HideProgres -- ${viewModel.hideProgress.value.toString()}")
//                    pagingAppRecyclerAdapter.submitData(it)
//
//                    if (binding.swipeRefresh.isRefreshing)
//                        binding.swipeRefresh.isRefreshing = false
//
//                }
//            }
//
//
//        })


        viewModel.filterChanges.observe(this, {
            pagingAppRecyclerAdapter.refresh()
            Log.e(TAG,"Filter Changes - Here $it")
            viewModel.fetchDataFromDataBase()
        })

        binding.swipeRefresh.setOnRefreshListener {
            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false
            pagingAppRecyclerAdapter.refresh()
        }

    }

    private fun getInsertDivider(): RecyclerView.ItemDecoration {
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        val dividerColor = ContextCompat.getColor(
            applicationContext,
            R.color.divider
        )
        val marginLeft = resources.getDimensionPixelSize(R.dimen.divider_inset)
        return InsetDivider.Builder(this)
            .orientation(InsetDivider.VERTICAL_LIST)
            .dividerHeight(dividerHeight)
            .color(dividerColor)
            .insets(marginLeft, 0)
            .build()
    }



    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(MainActivity.allAppId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            darkModeBottomSheet.tag
        )
    }


    private fun showFilterAppBottomSheet() {
        val filterAppBottomSheet = FilterAppBottomSheet()
        filterAppBottomSheet.show(supportFragmentManager, filterAppBottomSheet.tag)
    }

    fun showAppInfoBottomSheet(ID: Long) {
        val appInfoBottomSheet = AppInfoBottomSheet(ID)
        appInfoBottomSheet.show(supportFragmentManager, appInfoBottomSheet.tag)
    }


    private fun openSelectOption() {
        val selectAppBottomSheet = SelectAppBottomSheet()
        selectAppBottomSheet.show(supportFragmentManager, selectAppBottomSheet.tag)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()

            R.id.filterList_optionMenu -> showFilterAppBottomSheet()
            R.id.darkMode_optionMenu -> showDarkMode()
            R.id.selectOption_optionMenu -> openSelectOption()

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bottom_toolbar_menu, menu)
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

                viewModel.filterChanges(query)
                return true
            }
        })
    }




    private fun generateAlertDialog(
        title: String,
        message: String,
        negativeText: String,
        positiveText: String = ""
    ) {
        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(
                    this,
                    if (isDarkTheme) android.R.style.Theme_Material_Light_Dialog_Alert else android.R.style.Theme_Material_Dialog_Alert
                )
            } else {
                AlertDialog.Builder(this)
            }
        builder.setTitle(title)
            .setMessage(message)
            .setNegativeButton(negativeText) { _, _ ->

            }
            .setPositiveButton(positiveText) { _, _ ->

                if (positiveText == getString(R.string.yes_text)) {
                    val intent = Intent(this, ProgressActivity::class.java)
                    intent.action = MainActivity.allAppId
                    startActivity(intent)
                }

            }

            .setCancelable(true)
        val dialog = builder.create()
        dialog.show()
        val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
        textView!!.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.alert_dialog_message_size)
        )
    }
    override fun handleDialogClose() {
        viewModel.filterChanges()
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




}
