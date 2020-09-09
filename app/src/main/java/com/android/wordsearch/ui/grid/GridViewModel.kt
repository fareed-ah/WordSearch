package com.android.wordsearch.ui.grid

import android.content.Context
import android.graphics.Color
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.android.wordsearch.R
import com.android.wordsearch.model.GridTile
import dagger.hilt.android.qualifiers.ApplicationContext

class GridViewModel
@ViewModelInject
constructor(
    @ApplicationContext private val context: Context,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _gridState: MutableLiveData<List<GridTile>> = MutableLiveData()
    private val _itemUpdated: MutableLiveData<GridTile> = MutableLiveData()
    private val _wordFound: MutableLiveData<String> = MutableLiveData()
    private val _removeWord: MutableLiveData<String> = MutableLiveData()
    private var selectedTiles: List<GridTile> = ArrayList()
    private var wordsList: List<String> = ArrayList()
    private var selectionDirection: Direction = Direction.NONE
    private var colors: IntArray = context.resources.getIntArray(R.array.word_colors)
    var currentColor: Int

    private enum class Direction(var x: Int = 0, var y: Int = 0) {
        NORTH(0, -1), NORTH_EAST(1, -1), EAST(1, 0), SOUTH_EAST(1, 1), SOUTH(0, 1),
        SOUTH_WEST(-1, 1), WEST(-1, 0), NORTH_WEST(-1, -1), INITIAL, NONE
    }

    val gridState: LiveData<List<GridTile>>
        get() = _gridState

    val itemUpdated: LiveData<GridTile>
        get() = _itemUpdated

    val wordFound: LiveData<String>
        get() = _wordFound

    val removeWord: LiveData<String>
        get() = _removeWord

    private var gridTiles: List<GridTile> = ArrayList()

    init {
        currentColor = colors[(colors.indices.random())]
    }

    fun generateGrid(size: Int, words: List<String>) {
        wordsList = wordsList.plus(words)
        val textSize: Float = when (size) {
            5 -> 42f
            10 -> 28f
            15 -> 20f
            else -> 24f
        }

        for (pos in (0 until (size * size))) {
            gridTiles =
                gridTiles + GridTile(
                    x = pos % size,
                    y = pos / size,
                    selected = false,
                    textSize = textSize
                ) { gridTile ->
                    run {
                        handleTileSelected(gridTile)
                    }
                }
        }
        tryPlaceWords(words, size)
        fillEmptyTiles(size)
        _gridState.value = gridTiles
    }

    private fun handleTileSelected(tile: GridTile) {
        // if user selects the previously selected tile then unselect the tile
        if (selectedTiles.isNotEmpty() && selectedTiles.last() == tile) {
            tile.selected = false
            tile.unselect()
            selectedTiles = selectedTiles - tile
            if (selectedTiles.size == 1) {
                selectionDirection = Direction.INITIAL
            } else if (selectedTiles.isEmpty()) {
                selectionDirection = Direction.NONE
            }
            _itemUpdated.value = tile
            return
        }

        when (selectionDirection) {

            Direction.NONE -> { // if there are no currently selected tiles any tile selected is valid
                selectTile(tile, Direction.INITIAL)
            }
            Direction.INITIAL -> { // Check if tile selected is within the boundary of the initial tile
                for (directionOption in Direction.values()) {
                    if (tile.x == selectedTiles.last().x + directionOption.x && tile.y == selectedTiles.last().y + directionOption.y) {
                        selectTile(tile, directionOption)
                        break
                    }
                }
            }
            else -> {
                // if selected tile is within the path of the selected tile direction then it can be selected
                if (tile.x == selectedTiles.last().x + selectionDirection.x && tile.y == selectedTiles.last().y + selectionDirection.y) {
                    selectTile(tile, selectionDirection)
                }
            }
        }
        checkWordFound()
    }

    private fun selectTile(tile: GridTile, dir: Direction) {
        tile.selected = true
        tile.setBackgroundColor(currentColor)
        selectedTiles = selectedTiles + tile
        selectionDirection = dir
        _itemUpdated.value = tile
    }

    private fun checkWordFound() {
        var selectedWord = ""

        for (tile in selectedTiles) {
            selectedWord += tile.text
        }

        for (word in wordsList) {
            if (word.equals(selectedWord, ignoreCase = true)) {
                //words found
                wordsList = wordsList - word
                selectedTiles = selectedTiles.minus(selectedTiles)
                _wordFound.value = selectedWord
                selectionDirection = Direction.NONE
                currentColor = colors[(colors.indexOf(currentColor) + 1) % colors.size]
            }
        }

    }


    private fun randomLetter(): String {
        return (65..90).random().toChar().toString()
    }

    private fun tryPlaceWords(words: List<String>, gridSize: Int) {
        for (word in words) {
            var wordPlaced = false
            var attempts = 0
            // try to place the word 1000 times
            attempt@ while (!wordPlaced && attempts < 100) {

                attempts += 1
                val randPos = (gridTiles.indices).random()
                val dir = Direction.values()[(0..7).random()]

                // Check if the word will fit within the grid based on random placement position and direction
                if (gridTiles[randPos].y + ((word.length - 1) * dir.y) in 0 until gridSize &&
                    gridTiles[randPos].x + ((word.length - 1) * dir.x) in 0 until gridSize
                ) {
                    val yPos = gridTiles[randPos].y
                    val xPos = gridTiles[randPos].x
                    // check if each tile in the path is empty or the correct letter in the word that is being placed
                    // else try again
                    for (i in (word.indices)) {
                        if (gridTiles[(yPos + i * dir.y) * gridSize + xPos + (i * dir.x)].text != word[i].toString()
                            && gridTiles[(yPos + i * dir.y) * gridSize + xPos + (i * dir.x)].text.isNotEmpty()
                        ) {
                            continue@attempt
                        }
                    }
                    // if it can be added start placing each letter in the grid
                    for (i in (word.indices)) {
                        gridTiles[(yPos + i * dir.y) * gridSize + xPos + (i * dir.x)].text =
                            word[i].toString()
                    }
                    wordPlaced = true
                }
            }
            if (!wordPlaced) {
                removeWord(word)
            }
        }
    }

    private fun removeWord(word: String) {
        wordsList = wordsList - word
        _removeWord.value = word
    }

    private fun fillEmptyTiles(size: Int) {
        for (pos in (0 until (size * size))) {
            if (gridTiles[pos].text.isEmpty()) {
                gridTiles[pos].text = randomLetter()
            }
        }
    }
}