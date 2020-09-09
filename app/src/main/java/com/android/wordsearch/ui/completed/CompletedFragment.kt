package com.android.wordsearch.ui.completed

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.wordsearch.R
import com.android.wordsearch.ui.MainActivity
import com.android.wordsearch.ui.grid.GridFragment
import com.android.wordsearch.ui.main_screen.HomeFragment
import kotlinx.android.synthetic.main.fragment_completed.*

private const val ARG_SIZE = "grid_size"
private const val ARG_MESSAGE = "message"

class CompletedFragment : Fragment() {

    private var gridSize: Int = 0
    private var message: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gridSize = it.getInt(ARG_SIZE)
            message = it.getString(ARG_MESSAGE).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        completedMessageText.text = message

        playAgainButton.setOnClickListener { (requireActivity() as MainActivity).changeFragment(GridFragment.newInstance(gridSize))}
        homeButton.setOnClickListener { (requireActivity() as MainActivity).changeFragment(HomeFragment())}
    }

    companion object {
        @JvmStatic
        fun newInstance(gridSize: Int, message: String) =
            CompletedFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIZE, gridSize)
                    putString(ARG_MESSAGE, message)
                }
            }
    }
}