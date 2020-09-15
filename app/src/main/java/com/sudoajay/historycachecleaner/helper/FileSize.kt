package com.sudoajay.historycachecleaner.helper

import java.text.DecimalFormat

object FileSize {
    @JvmStatic
    fun convertIt(size: Long): String {
        return try {
            when {
                size > 1024 * 1024 * 1024 -> { // GB
                    getDecimal2Round(size.toDouble() / (1024 * 1024 * 1024).toDouble()) + " GB"
                }
                size > 1024 * 1024 -> { // MB
                    getDecimal2Round(size.toDouble() / (1024 * 1024).toDouble()) + " MB"
                }
                else -> { // KB
                    getDecimal2Round(size.toDouble() / 1024.toDouble()) + " KB"
                }
            }
        }catch (ignored :Exception){
            "00 GB "
        }
    }

    private fun getDecimal2Round(time: Double): String {
        val df = DecimalFormat("#.#")
        return java.lang.Double.valueOf(df.format(time)).toString()
    }
}