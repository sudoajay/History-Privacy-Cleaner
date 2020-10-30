package com.sudoajay.historycachecleaner.activity.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.progress.ProgressActivity
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.DarkModeBottomSheet
import com.sudoajay.historycachecleaner.helper.InsetDivider
import com.sudoajay.historycachecleaner.helper.root.RootState
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidSdCardPermission
import com.sudoajay.historycachecleaner.helper.storagePermission.SdCardPath
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityAllAppBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        isDarkTheme = isDarkMode(applicationContext)
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
        permissionIssue()
    }


    override fun onResume() {

        checkRootState()
        binding.deleteFloatingActionButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    selectedList = viewModel.appRepository.getSelectedApp()
                }
                Log.e(TAG , "selectlist size - " + selectedList.size)
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

    private fun permissionIssue() {
        //        Take Permission external permission
        androidExternalStoragePermission =
            AndroidExternalStoragePermission(applicationContext, this)
        sdCardPermission = AndroidSdCardPermission(applicationContext, this)


        if (!androidExternalStoragePermission?.isExternalStorageWritable!!) androidExternalStoragePermission?.callPermission()
        else sdCardPermission?.checkForSdCardExistAfterPermission()


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

        viewModel.appList!!.observe(this, {
            pagingAppRecyclerAdapter.submitList(it)

            Log.e(TAG, "size - " + it.size)
            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false

            viewModel.hideProgress!!.value = false
            if (it.isEmpty()) CustomToast.toastIt(
                applicationContext,
                getString(R.string.alert_dialog_no_cache_app)
            )


        })


        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
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
        val darkModeBottomSheet = DarkModeBottomSheet(homeShortcutId)
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


    private fun checkRootState(): RootState? {
        val rootState: RootState = viewModel.rootManager.checkRootPermission()!!
        when (rootState) {
            RootState.NO_ROOT -> {
                setRootAccessAlreadyObtained(false, applicationContext)
                generateAlertDialog(
                    resources.getString(R.string.alert_dialog_title_no_root_permission),
                    resources.getString(R.string.alert_dialog_message_no_root_permission),
                    getString(R.string.ok_text)
                )
            }
            RootState.BE_ROOT -> {
                setRootAccessAlreadyObtained(false, applicationContext)
                generateAlertDialog(
                    resources.getString(R.string.alert_dialog_title_be_root),
                    resources.getString(R.string.alert_dialog_message_be_root),
                    getString(R.string.ok_text)
                )
            }
            RootState.HAVE_ROOT -> {

                if (isRootAccessAlreadyObtained(applicationContext)) return null
                setRootAccessAlreadyObtained(true, applicationContext)
                generateAlertDialog(
                    resources.getString(R.string.alert_dialog_title_have_root),
                    resources.getString(R.string.alert_dialog_message_have_root),
                    getString(R.string.ok_text)
                )
            }
        }
        return rootState
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
                    if (!isDarkMode(applicationContext)) android.R.style.Theme_Material_Light_Dialog_Alert else android.R.style.Theme_Material_Dialog_Alert
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
                    val intent = Intent(this,ProgressActivity::class.java)
                    intent.action = appCacheDataId
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == 1) { // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
//                permission Granted :)
                AndroidExternalStoragePermission.setExternalPath(
                    applicationContext,
                    AndroidExternalStoragePermission.getExternalPathFromCacheDir(applicationContext)
                        .toString()
                )
                sdCardPermission?.checkForSdCardExistAfterPermission()

            } else // permission denied, boo! Disable the
            // functionality that depends on this permission.
                CustomToast.toastIt(applicationContext, getString(R.string.giveUsPermission))
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) { // local variable
        super.onActivityResult(requestCode, resultCode, data)
        val sdCardPathURL: String?
        val stringURI: String
        val spiltPart: String?
        if (resultCode != Activity.RESULT_OK) return

        if (requestCode == 1 || requestCode == 2) {
            val sdCardURL: Uri? = data!!.data
            grantUriPermission(
                this@AllApp.packageName,
                sdCardURL,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this@AllApp.contentResolver.takePersistableUriPermission(
                    sdCardURL!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            sdCardPathURL = SdCardPath.getFullPathFromTreeUri(sdCardURL, this@AllApp)
            stringURI = sdCardURL.toString()

            // Its supports till android 9 & api 28
            if (requestCode == 2) {
                spiltPart = "%3A"
                AndroidSdCardPermission.setSdCardPath(
                    applicationContext,
                    spiltThePath(stringURI, sdCardPathURL.toString())
                )
                AndroidSdCardPermission.setSdCardUri(
                    applicationContext,
                    spiltUri(stringURI, spiltPart)
                )
                val androidSdCardPermission = AndroidSdCardPermission(applicationContext, this)
                if (!androidSdCardPermission.isSdStorageWritable) {
                    CustomToast.toastIt(
                        applicationContext,
                        resources.getString(R.string.wrongDirectorySelected)
                    )
                    return
                }

            } else {
                val realExternalPath =
                    AndroidExternalStoragePermission.getExternalPath(applicationContext)
                if (realExternalPath in "$sdCardPathURL/") {
                    spiltPart = "primary%3A"
                    AndroidExternalStoragePermission.setExternalPath(
                        applicationContext,
                        realExternalPath
                    )
                    AndroidExternalStoragePermission.setExternalUri(
                        applicationContext,
                        spiltUri(stringURI, spiltPart)
                    )
                } else {
                    CustomToast.toastIt(
                        applicationContext,
                        getString(R.string.wrongDirectorySelected)
                    )

                    return
                }


            }

        } else {
            CustomToast.toastIt(applicationContext, getString(R.string.reportIt))
        }
    }

    private fun spiltUri(uri: String, spiltPart: String): String {
        return uri.split(spiltPart)[0] + spiltPart

    }

    private fun spiltThePath(url: String, path: String): String {
        val spilt = url.split("%3A").toTypedArray()
        val getPaths = spilt[0].split("/").toTypedArray()
        val paths = path.split(getPaths[getPaths.size - 1]).toTypedArray()
        return paths[0] + getPaths[getPaths.size - 1] + "/"

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

    companion object {
        const val settingShortcutId = "setting"
        const val homeShortcutId = "home"
        const val appCacheDataId = "appCacheData"
        private fun setRootAccessAlreadyObtained(status: Boolean, context: Context) {
            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                .putBoolean(
                    context.getString(R.string.is_root_permission_text), status
                ).apply()
        }

        fun isRootAccessAlreadyObtained(context: Context): Boolean {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getBoolean(
                    context.getString(R.string.is_root_permission_text), false
                )
        }


    }


}