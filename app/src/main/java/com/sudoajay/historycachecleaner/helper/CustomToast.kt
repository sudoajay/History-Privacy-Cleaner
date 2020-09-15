package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.widget.Toast

object CustomToast {
    fun toastIt(mContext: Context, mes: String) {
        val toast = Toast.makeText(mContext, mes, Toast.LENGTH_LONG)
        toast.show()
    }
}