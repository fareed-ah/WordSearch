package com.android.wordsearch.model

import android.graphics.Color
import com.android.wordsearch.adapter.DiffItem

class WordTile (var text: String,var textColor: Int = Color.BLACK, var found: Boolean = false) : DiffItem {
}