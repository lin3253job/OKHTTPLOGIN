package com.example.okhttplogin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StockContent(
    val stockcode: String,
    val stockname: String


) : Parcelable