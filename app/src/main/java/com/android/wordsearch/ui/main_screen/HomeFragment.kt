package com.android.wordsearch.ui.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.android.wordsearch.R
import com.android.wordsearch.ui.MainActivity
import com.android.wordsearch.ui.grid.GridFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridSizes =
            arrayOf("Grid 5 X 5", "Grid 10 X 10", "Grid 15 X 15")

        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.item_grid_size,
            gridSizes
        )

        gridSizeSpinner.adapter = adapter

        startGameButton.setOnClickListener {
            var gridSize = 5
            when(gridSizeSpinner.selectedItem){

                // 5x5
                gridSizes[0] ->{
                    gridSize = 5
                }
                // 10x10
                gridSizes[1] ->{
                    gridSize = 10
                }
                // 15x15
                gridSizes[2] ->{
                    gridSize = 15
                }
            }
            (requireActivity() as MainActivity).replaceFragment(GridFragment.newInstance(gridSize))
        }
    }
}