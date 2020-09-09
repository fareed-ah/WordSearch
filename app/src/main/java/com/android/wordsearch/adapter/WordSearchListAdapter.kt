package com.android.wordsearch.adapter

import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView
import com.android.wordsearch.R
import com.android.wordsearch.model.GridTile
import com.google.android.material.card.MaterialCardView
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate

fun wordSearchGridAdapter() =
    adapterDelegate<GridTile, DiffItem>(
        R.layout.item_grid
    ) {

        // This is the initializer block where you initialize the ViewHolder.
        // Its called one time only in onCreateViewHolder.
        // this is where you can call findViewById() and setup click listeners etc.

        val tileText: TextView = findViewById(R.id.tileText)
        val gridTile: MaterialCardView = findViewById(R.id.gridTile)

        // name.setClickListener { itemClickedListener(item) } // Item is automatically set for you. It's set lazily though (set in onBindViewHolder()). So only use it for deferred calls like clickListeners.

        bind { diffPayloads -> // diffPayloads is a List<Any> containing the Payload from your DiffUtils
            // This is called anytime onBindViewHolder() is called

            tileText.text = item.text
            tileText.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.textSize)

            gridTile.setOnClickListener { item.listener.invoke(item) }
            //if (item.selected) {
                gridTile.setCardBackgroundColor(item.bgColor)
           // } else {
               // gridTile.setCardBackgroundColor(Color.WHITE)
           // }

        }
    }