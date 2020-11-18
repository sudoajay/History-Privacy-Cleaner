package com.sudoajay.historycachecleaner.activity.scrolling

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.app.PagingAppRecyclerAdapter
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.FileHelper
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityScrollingAppCachePathBinding
import kotlinx.android.synthetic.main.content_scrolling_app_cache_path.*
import kotlinx.android.synthetic.main.layout_app_cache_path_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScrollingAppCachePathActivity : BaseActivity() {
    private lateinit var binding: ActivityScrollingAppCachePathBinding
    lateinit var app: App
    private var isDarkTheme: Boolean = false
    var hideProgress = MutableLiveData<Boolean>()
    private var TAG = "ScrollingAppCachePathActivityTAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isDarkTheme = isDarkMode(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(
                    false
                ) else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scrolling_app_cache_path)
        binding.activity = this
        // Required to update UI with LiveData
        binding.lifecycleOwner = this
        changeStatusBarColor()

        val appDao: AppDao =
            AppRoomDatabase.getDatabase(applicationContext).appDao()
        val appRepository = CacheRepository(applicationContext, appDao)
        if (!intent.action.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                app = appRepository.getAppFromId(intent.action.toString().toInt())

            }
        }


    }

    override fun onResume() {
        super.onResume()
        hideProgress.value = true
        Log.e(TAG, hideProgress.value.toString())
        Log.e(TAG,circular_ProgressBar.visibility.toString() )
        setSupportActionBar(binding.toolbar)
        binding.appImageImageView.setImageDrawable(
            PagingAppRecyclerAdapter.getApplicationsIcon(
                app.icon,
                applicationContext.packageManager
            )
        )
        binding.toolbar.setNavigationIcon(R.drawable.ic_back)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        var list = mutableListOf<String>()


        val viewManager = LinearLayoutManager(this)
        val viewAdapter = MyAdapter(list)

        cachePath_RecyclerView.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }


        CoroutineScope(Dispatchers.IO).launch {
            list = FileHelper(applicationContext, app.packageName).fileList()

            withContext(Dispatchers.Main) {
                if (list.isEmpty())
                    CustomToast.toastIt(applicationContext, "Empty List")
                viewAdapter.updateReceiptsList(list)
            }
            hideProgress.postValue(false)

        }
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

    class MyAdapter(private val cachePath: MutableList<String>) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {
            val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_app_cache_path_item, parent, false)
            return MyViewHolder(layout)
        }

        class MyViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {
            var path: TextView = view.appCachePath_TextView
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.path.text = cachePath[position]
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = cachePath.size

        fun updateReceiptsList(newlist: MutableList<String>) {
            cachePath.clear()
            cachePath.addAll(newlist)
            notifyDataSetChanged()

        }
    }
}