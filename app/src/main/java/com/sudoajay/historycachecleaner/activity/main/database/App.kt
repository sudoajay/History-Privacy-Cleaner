package com.sudoajay.historycachecleaner.activity.main.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "AppTable")
class App(
    @PrimaryKey(autoGenerate = true ) var id: Long?,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "Path") val path: String,
    @ColumnInfo(name = "Ext_Cache_Path") val extCachePath: String,
    @ColumnInfo(name = "Int_Cache_Path") val intCachePath: String,
    @ColumnInfo(name = "Package_Name") val packageName: String,
    @ColumnInfo(name = "Icon") val icon: String,
    @ColumnInfo(name = "Date") val date: String,
    @ColumnInfo(name = "Size") val size: Long,
    @ColumnInfo(name = "System_App") val isSystemApp: Boolean,
    @ColumnInfo(name = "User_App") val isUserApp: Boolean,
    @ColumnInfo(name = "Selected") val isSelected: Boolean,
    @ColumnInfo(name = "Installed") val isInstalled: Boolean

)