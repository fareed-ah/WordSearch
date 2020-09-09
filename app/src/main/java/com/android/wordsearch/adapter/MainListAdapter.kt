package com.android.wordsearch.adapter

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class MainListAdapter : ListDelegationAdapter<List<DiffItem>>(
    wordListAdapter(),
    wordSearchGridAdapter()
)
