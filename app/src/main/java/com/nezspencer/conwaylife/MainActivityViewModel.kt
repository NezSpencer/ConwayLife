package com.nezspencer.conwaylife

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel : ViewModel(), CoroutineScope{

    private lateinit var boardMirror : Array<Array<Int>>
    private val job = Job()
    val status = MutableLiveData<Status>()

    fun cancelJob(){
        job.cancel()
    }

    fun startConwayGame(row: Int, column: Int, board : Array<Array<Int>>){
        boardMirror = board
        launch {
            startConway(row, column)
            job.join()
        }

    }
    private suspend fun startConway(row : Int, column : Int) {
        for (i in 0 until row) {
            for (j in 0 until column) {
                //traverse through array
                val makeAlive = isAlive(i, j, row, column)
                if (makeAlive){
                    boardMirror[i][j] = 1
                } else {
                    boardMirror[i][j] = 0
                }
                status.postValue(Status(Pair(i,j), makeAlive))
            }
        }
    }

    private suspend fun isAlive(rowIndex: Int, colIndex: Int, row : Int, column: Int): Boolean {
        delay(200)
        var aliveCount = 0
        if (rowIndex % (row - 1) == 0 && colIndex % (column - 1) == 0) {
            //sharpest edges
            for (i in 0 until row step row - 1) {
                for (j in 0 until column step column - 1) {
                    if (i != rowIndex && j != colIndex && boardMirror[i][j] == 1) {
                        aliveCount++
                    }
                }
            }
        } else if (rowIndex % (row - 1) == 0) {
            //left and right edges
            for (i in rowIndex - 1..rowIndex + 1 step 2) {
                if (i < 0 || i >= row)
                    continue
                for (j in 0 until column step column - 1) {
                    if (i != rowIndex && j != colIndex && boardMirror[i][j] == 1) {
                        aliveCount++
                    }
                }
            }
        } else if (colIndex % (column - 1) == 0) {
            //top and bottom edges
            for (i in 0 until row step row - 1) {
                for (j in colIndex - 1..colIndex + 1 step 2) {
                    if (j < 0 || j >= column)
                        continue
                    if (i != rowIndex && j != colIndex && boardMirror[i][j] == 1) {
                        aliveCount++
                    }
                }
            }
        }

        //for positions within the normal range
        for (i in rowIndex - 1..rowIndex + 1 step 2) {
            if (i < 0 || i >= row)
                continue
            for (j in colIndex - 1..colIndex + 1 step 2) {
                if (j < 0 || j >= column)
                    continue
                if (boardMirror[i][j] == 1)
                    aliveCount++

            }
        }

        return aliveCount == 2
    }

    /*private fun configure(row: Int, column : Int, alive : MutableList<Pair<Int,Int>>){
        val rowParams =
            TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f)
        val itemParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f)
        itemParams.setMargins(1, 1, 1, 1)

        //variables

        boardItems = Array(row + 1) { Array(column) { TextView(this) } }
        for (i in 0..row) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = rowParams
            val views = Array(column + 1) { TextView(this) }
            for (j in 0..column) {

                val textView = TextView(this)
                textView.layoutParams = itemParams
                textView.setBackgroundResource(android.R.color.holo_red_dark)
                textView.text = "0"
                val itemPair = Pair(i, j)
                for (pair in alive) {
                    if (itemPair.first == pair.first && itemPair.second == pair.second) {
                        textView.text = "1"
                        textView.setBackgroundResource(android.R.color.white)
                        break
                    }
                }
                views[j] = textView
                tableRow.addView(textView)
            }
            boardItems[i] = views
            binding.board.addView(tableRow)
        }

        launch {
            startConway(row, column)
            job.join()
        }
    }*/

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        cancelJob()
        super.onCleared()
    }
}