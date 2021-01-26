package com.sudoajay.historycachecleaner.activity.main

import android.app.Activity
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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.main.database.Cache
import com.sudoajay.historycachecleaner.activity.progress.ProgressActivity
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historycachecleaner.activity.settingActivity.SettingsActivity
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.DarkModeBottomSheet
import com.sudoajay.historycachecleaner.helper.InsetDivider
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historycachecleaner.helper.root.RootState
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidExternalStoragePermission
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidSdCardPermission
import com.sudoajay.historycachecleaner.helper.storagePermission.SdCardPath
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

    lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding
    private var isDarkTheme: Boolean = false
    private var doubleBackToExitPressedOnce = false
    private var TAG = "MainActivityTag"
    private lateinit var androidExternalStoragePermission: AndroidExternalStoragePermission
    private lateinit var sdCardPermission: AndroidSdCardPermission
    private lateinit var rootManager: RootManager
    private lateinit var selectedList: MutableList<Cache>
    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        isDarkTheme = isDarkMode.value ?: true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(
                    false
                ) else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        changeStatusBarColor()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        if (!intent.action.isNullOrEmpty() && intent.action.toString() == settingShortcutId) {
            openSetting()
        }


        //     Check if root permission is given or not
        checkRootState()
//        External and sd-Card permission if there is no root permission
       permissionIssue()
    }


    override fun onResume() {



        binding.deleteFloatingActionButton.setOnClickListener {
            if (!permissionIssue()) {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        selectedList = viewModel.cacheRepository.getSelectedApp()
                    }

                    if (selectedList.isEmpty())
                        CustomToast.toastIt(
                            applicationContext,
                            getString(R.string.alert_dialog_no_item_selected_title)
                        )
                    else {

                        generateAlertDialog(
                            getString(R.string.alert_dialog_ask_permission_to_remove_apps_title),
                            getString(R.string.alert_dialog_ask_permission_to_remove_cache_item_message),
                            getString(R.string.no_text),
                            getString(R.string.yes_text)
                        )

                    }
                }
            }

        }

        setReference()
        super.onResume()
    }


    fun permissionIssue(): Boolean {
//        If no root permission given  then we need external and sd card permission for further operation
        return if (rootManager.checkRootPermission() == RootState.HAVE_ROOT) {
            //        Take Permission external permission
            androidExternalStoragePermission =
                AndroidExternalStoragePermission(applicationContext, this)
            sdCardPermission = AndroidSdCardPermission(applicationContext, this)

            if (!androidExternalStoragePermission.isExternalStorageWritable) androidExternalStoragePermission.callPermission()
            else
                return sdCardPermission.isSdCardDetected()

            true
        } else {
            false
        }
    }


    fun setReference() {

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

        val cacheDnsAdapter = CacheDnsAdapter(this)
        recyclerView.adapter = cacheDnsAdapter

        viewModel.cacheList!!.observe(this, {
            cacheDnsAdapter.items = it

            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false

            viewModel.hideProgress!!.value = it.isEmpty()

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

    private fun showNavigationDrawer(){
        val navigationDrawerBottomSheet = NavigationDrawerBottomSheet()
        navigationDrawerBottomSheet.show(supportFragmentManager, navigationDrawerBottomSheet.tag)
    }



    private fun openSetting() {
        val intent = Intent(applicationContext, SettingsActivity::class.java)
        startActivity(intent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> showNavigationDrawer()
            R.id.darkMode_optionMenu -> showDarkMode()
            R.id.moreSetting_optionMenu -> openSetting()

            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_bottom_toolbar_menu, menu)
        menu.findItem(R.id.search_optionMenu)

        return super.onCreateOptionsMenu(menu)
    }


    private fun checkRootState(): RootState? {
        rootManager = RootManager(applicationContext)
        val rootState = rootManager.checkRootPermission()
        when (rootState) {
            RootState.NO_ROOT -> {
                setRootAccessAlreadyObtained(false)
                generateAlertDialog(
                    resources.getString(R.string.alert_dialog_title_no_root_permission),
                    resources.getString(R.string.alert_dialog_message_no_root_permission),
                    getString(R.string.ok_text)
                )
            }
            RootState.BE_ROOT -> {
                setRootAccessAlreadyObtained(false)
                generateAlertDialog(
                    resources.getString(R.string.alert_dialog_title_be_root),
                    resources.getString(R.string.alert_dialog_message_be_root),
                    getString(R.string.ok_text)
                )
            }
            RootState.HAVE_ROOT -> {
                if (isRootPermission.value == true) return null
                setRootAccessAlreadyObtained(true)
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
                    if (isDarkTheme) android.R.style.Theme_Material_Dialog_Alert else android.R.style.Theme_Material_Light_Dialog_Alert
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
                    intent.action = homeShortcutId
                    startActivity(intent)
                }

            }

            .setCancelable(true)
        dialog = builder.create()
        dialog!!.show()
        val textView = dialog!!.findViewById<View>(android.R.id.message) as TextView?
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
                CoroutineScope(Dispatchers.IO).launch {
                    ProtoManager(applicationContext).setExternalPath(
                        AndroidExternalStoragePermission.getExternalPathFromCacheDir(
                            applicationContext
                        )
                    )
                }

                sdCardPermission.isSdCardDetected()

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
                this@MainActivity.packageName,
                sdCardURL,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                this@MainActivity.contentResolver.takePersistableUriPermission(
                    sdCardURL!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }

            sdCardPathURL = SdCardPath.getFullPathFromTreeUri(sdCardURL, this@MainActivity)
            stringURI = sdCardURL.toString()

            Log.e(TAG , "SdCardPathUrl - $sdCardPathURL  String uri - $stringURI requestCode - $requestCode" )
            if (requestCode == 2) {
                spiltPart = "%3A"
                lifecycleScope.launch {

                    ProtoManager(applicationContext).setSdCardPath(
                        spiltThePath(
                            stringURI,
                            sdCardPathURL.toString()
                        )
                    )
                    ProtoManager(applicationContext).setSdCardUri(
                        spiltUri(stringURI, spiltPart)
                    )
                }
                sdCardPath.value =  spiltThePath(
                    stringURI,
                    sdCardPathURL.toString()
                )
                sdCardUri.value =  spiltUri(stringURI, spiltPart)
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
                    externalPath.value.toString()
                if (realExternalPath in "$sdCardPathURL/") {
                    spiltPart = "primary%3A"
                    CoroutineScope(Dispatchers.IO).launch {
                        ProtoManager(applicationContext).setExternalPath(
                            realExternalPath
                        )
                        externalPath.value = realExternalPath
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        ProtoManager(applicationContext).setExternalUri(
                            spiltUri(stringURI, spiltPart)
                        )
                    }

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

    private fun setRootAccessAlreadyObtained(status: Boolean) {
        lifecycleScope.launch {
            ProtoManager(applicationContext).setIsRootPermission(status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
            dialog?.dismiss()
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
        const val allAppId = "allApp"

    }


}
