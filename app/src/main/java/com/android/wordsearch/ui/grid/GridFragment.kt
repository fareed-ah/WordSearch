package com.android.wordsearch.ui.grid

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.android.wordsearch.R
import com.android.wordsearch.adapter.MainListAdapter
import com.android.wordsearch.model.GridTile
import com.android.wordsearch.model.WordTile
import com.android.wordsearch.ui.MainActivity
import com.android.wordsearch.ui.completed.CompletedFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_grid.*


private const val ARG_SIZE = "grid_size"

@AndroidEntryPoint
class GridFragment : Fragment() {

    private var gridSize: Int = 0

    private var gridItems: ArrayList<GridTile> = ArrayList()

    private var wordItems: List<WordTile> = ArrayList()

    private val gridViewModel: GridViewModel by viewModels()

    private val gridAdapter: MainListAdapter = MainListAdapter()

    private val wordListAdapter: MainListAdapter = MainListAdapter()

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gridSize = it.getInt(ARG_SIZE)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(gridSize: Int) =
            GridFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SIZE, gridSize)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timer = object : CountDownTimer(120000, 1000) {
            override fun onFinish() {
                (requireActivity() as MainActivity).changeFragment(
                    CompletedFragment.newInstance(
                        gridSize,
                        "Time is up!"
                    )
                )
            }

            override fun onTick(millisUntilFinished: Long) {
                val minutes: Int = (millisUntilFinished / 1000 / 60).toInt()
                val seconds: Int = ((millisUntilFinished / 1000 % 60).toInt())
                val time = "%02d:%02d".format(minutes, seconds)
                if(minutes == 0 && seconds <=10){
                    timerText.setTextColor(Color.RED)
                }
                timerText.text = time
            }
        }.start()

        gridAdapter.items = gridItems
        wordSearchGridView.layoutManager = GridLayoutManager(requireContext(), gridSize)
        wordSearchGridView.adapter = gridAdapter

        wordListGridView.layoutManager = GridLayoutManager(context, 3)
        wordListAdapter.items = wordItems
        wordListAdapter.notifyDataSetChanged()
        wordListGridView.adapter = wordListAdapter


        val options: List<WordTile> = listOf(
            WordTile("Kotlin".toUpperCase()),
            WordTile("ObjectiveC".toUpperCase()),
            WordTile("React".toUpperCase()),
            WordTile("Ruby".toUpperCase()),
            WordTile("Rails".toUpperCase()),
            WordTile("Python".toUpperCase()),
            WordTile("Android".toUpperCase())
        )

        wordItems = options.filter { it.text.length <= gridSize }

        wordListAdapter.items = wordItems
        wordListAdapter.notifyDataSetChanged()

        gridViewModel.gridState.observe(viewLifecycleOwner, Observer { gridTiles ->
            gridItems.clear()
            gridItems.addAll(gridTiles)
            gridAdapter.notifyDataSetChanged()
        })

        gridViewModel.itemUpdated.observe(viewLifecycleOwner, Observer {
            gridItems[it.getIndex(gridSize)] = it
            gridAdapter.notifyItemChanged(it.getIndex(gridSize))
        })

        gridViewModel.removeWord.observe(viewLifecycleOwner, Observer {removeWord  ->
            val wordTile: WordTile = wordItems.find { it.text.equals(removeWord,ignoreCase = true) }!!
            val index = wordItems.indexOf(wordTile)
            wordItems = wordItems.minus(wordTile)
            wordListAdapter.items = wordItems
            wordListAdapter.notifyDataSetChanged()
        })

        gridViewModel.wordFound.observe(viewLifecycleOwner, Observer { foundWord ->
            val wordTile = wordItems.find { it.text == foundWord }
            val index = wordItems.indexOf(wordTile)
            wordTile?.textColor = gridViewModel.currentColor
            wordTile?.found = true
            wordListAdapter.notifyItemChanged(index)

            if (wordItems.all { it.found }) {
                (requireActivity() as MainActivity).changeFragment(
                    CompletedFragment.newInstance(
                        gridSize,
                        "Level Completed"
                    )
                )
            }
        })
        gridViewModel.generateGrid(gridSize, wordItems.map { it.text })
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }
}