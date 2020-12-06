package com.sudoajay.historycachecleaner.activity.main.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "CacheTable")
class Cache(
    @PrimaryKey(autoGenerate = true ) var id: Long?,
    @ColumnInfo(name = "Name") val name: String,
    @ColumnInfo(name = "Icon") val icon: String,
    @ColumnInfo(name = "Selected") val isSelected: Boolean,



)