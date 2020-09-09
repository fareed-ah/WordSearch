package com.android.wordsearch.model

import android.graphics.Color
import com.android.wordsearch.adapter.DiffItem

class GridTile(
    var text: String = "",
    var x: Int,
    var y: Int,
    var selected: Boolean,
    var bgColor: Int = Color.WHITE,
    var prevBgColor: Int = Color.WHITE,
    var textSize: Float,
    var listener: (item: GridTile) -> Unit
) : DiffItem {

    fun getIndex(gridSize: Int): Int {
        return (y * gridSize + x)
    }

    fun setBackgroundColor(color: Int){
        prevBgColor = bgColor
        bgColor = color
    }

    fun unselect(){
        bgColor = prevBgColor
        prevBgColor = Color.WHITE
    }
}