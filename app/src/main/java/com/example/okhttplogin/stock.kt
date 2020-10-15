package com.example.okhttplogin

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class stock(
    var sysCode:Int,
    var sysMsg:String,
    var data: List<StockContent>
) : Parcelable
