package com.sudoajay.historycachecleaner.helper

import java.text.DecimalFormat

object FileSize {
    private const val KB_FACTOR: Long = 1000
    private const val KIB_FACTOR: Long = 1024
    private const val MB_FACTOR = 1000 * KB_FACTOR
    private const val MIB_FACTOR = 1024 * KIB_FACTOR
    private const val GB_FACTOR = 1000 * MB_FACTOR
    private const val GIB_FACTOR = 1024 * MIB_FACTOR
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
        }catch (ignored: Exception){
            "00 GB "
        }
    }

    private fun getDecimal2Round(time: Double): String {
        val df = DecimalFormat("#.#")
        return java.lang.Double.valueOf(df.format(time)).toString()
    }



}