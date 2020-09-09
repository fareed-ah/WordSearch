package com.android.wordsearch.ui.utils

import com.android.wordsearch.model.GridTile

interface GridItemListener {
    fun onGridItemSelected(gridTile: GridTile)
}
