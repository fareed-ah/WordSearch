package com.android.wordsearch.adapter

import android.graphics.Paint
import android.widget.TextView
import com.android.wordsearch.R
import com.android.wordsearch.model.WordTile
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegate

fun wordListAdapter() =
    adapterDelegate<WordTile, DiffItem>(
        R.layout.item_word
    ) {

        // This is the initializer block where you initialize the ViewHolder.
        // Its called one time only in onCreateViewHolder.
        // this is where you can call findViewById() and setup click listeners etc.

        val wordText: TextView = findViewById(R.id.wordText)

        // name.setClickListener { itemClickedListener(item) } // Item is automatically set for you. It's set lazily though (set in onBindViewHolder()). So only use it for deferred calls like clickListeners.

        bind { diffPayloads -> // diffPayloads is a List<Any> containing the Payload from your DiffUtils
            // This is called anytime onBindViewHolder() is called
            wordText.text = item.text
            wordText.setTextColor(item.textColor)

            if (item.found){
                wordText.paintFlags = wordText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }else{
                wordText.paintFlags = wordText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

        }
    }