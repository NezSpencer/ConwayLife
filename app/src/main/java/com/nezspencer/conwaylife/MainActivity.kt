package com.nezspencer.conwaylife

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.nezspencer.conwaylife.databinding.ActivityMainBinding
import com.nezspencer.conwaylife.databinding.ItemAliveBinding

class MainActivity : AppCompatActivity() {

    private lateinit var boardItems: Array<Array<TextView>>
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainActivityViewModel
    private var rowInput : Int = 0
    private var columnInput : Int = 0
    private var itemPairs =  mutableListOf<Pair<Int,Int>>()
    private var alive = mutableSetOf<Pair<Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this)[MainActivityViewModel::class.java]
        viewModel.status.observe(this, Observer<Status> {stat ->
            stat?.let {
                changeBoardItemState(it)
            }
        })
        initializeBottomSheetViews()

    }

    private fun initializeBottomSheetViews(){
        val sheetBehaviour = BottomSheetBehavior.from(binding.configSheet.sheetItem)
        sheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
        binding.configSheet.btnConfigure.setOnClickListener{

            binding.board.removeAllViews()
            configure(rowInput, columnInput, ArrayList(alive))
            sheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.configSheet.etRow.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    rowInput = if (!TextUtils.isEmpty(it))
                        it.toString().toInt()
                    else
                        0
                }

                if (rowInput > 0 && columnInput > 0)
                    initializeAliveSpinner(rowInput, columnInput)
            }
        })

        binding.configSheet.etColumn.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    columnInput = if (!TextUtils.isEmpty(it))
                        it.toString().toInt()
                    else
                        0
                }

                if (rowInput > 0 && columnInput > 0)
                    initializeAliveSpinner(rowInput, columnInput)

            }
        })
    }

    private fun initializeAliveSpinner(row: Int, column: Int){
        val list = mutableListOf("Select Alive positions")

        for (i in 0 until row)
            for (j in 0 until column) {
                itemPairs.add(Pair(i,j))
                list.add("$i,$j")
            }

        val recyclerAdapter = AlivePreviewAdapter(mutableListOf())
        binding.configSheet.rvAlive.adapter = recyclerAdapter
        val spinnerAdapter = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.configSheet.spinnerAlive.adapter = spinnerAdapter

        binding.configSheet.spinnerAlive.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val pos = list[p2].split(",")
                if (pos.size == 2) {
                    val pair = Pair(pos[0].toInt(), pos[1].toInt())
                    alive.add(pair)
                    recyclerAdapter.refreshList(pair)
                }

            }
        }


    }



    private fun configure(row: Int, column : Int, alive : MutableList<Pair<Int,Int>>){
        val rowParams =
            TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT, 1f)
        val itemParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f)
        itemParams.setMargins(1, 1, 1, 1)

        boardItems = Array(row) { Array(column) { TextView(this) } }
        val boardMirror = Array(row) {Array(column) {0}}
        for (i in 0 until row) {
            val tableRow = TableRow(this)
            tableRow.layoutParams = rowParams
            val views = Array(column) { TextView(this) }
            val rowMirror = Array(column){0}
            for (j in 0 until column) {

                val textView = TextView(this)
                textView.layoutParams = itemParams
                textView.setBackgroundResource(android.R.color.holo_red_dark)
                textView.text = "0"
                rowMirror[j] = 0

                val itemPair = Pair(i, j)
                for (pair in alive) {
                    if (itemPair.first == pair.first && itemPair.second == pair.second) {
                        textView.text = "1"
                        rowMirror[j] = 1
                                textView.setBackgroundResource(android.R.color.white)
                        break
                    }
                }
                views[j] = textView

                tableRow.addView(textView)
            }
            boardItems[i] = views
            boardMirror[i] = rowMirror
            binding.board.addView(tableRow)
        }

        viewModel.startConwayGame(row,column, boardMirror)
    }

    private fun changeBoardItemState(status: Status){
        if (status.isAlive) {
            boardItems[status.position.first][status.position.second].run {
                text = "1"
                setBackgroundResource(android.R.color.white)
            }
        } else {
            boardItems[status.position.first][status.position.second].run {
                text = "0"
                setBackgroundResource(android.R.color.holo_red_dark)
            }
        }
    }

    inner class AlivePreviewAdapter(private val previewList : MutableList<Pair<Int, Int>>) : RecyclerView.Adapter<Holder>(){

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
            val bind = ItemAliveBinding.inflate(LayoutInflater.from(p0.context), p0, false)
            return Holder(bind)
        }

        override fun getItemCount() = previewList.size

        override fun onBindViewHolder(p0: Holder, p1: Int) {
            val pair = previewList[p0.adapterPosition]
            p0.itemBinding.tvAlive.text = "(${pair.first}, ${pair.second})"
        }

        fun refreshList(pair : Pair<Int,Int>){
            val prevSize = previewList.size
            previewList.add(pair)
            notifyItemInserted(prevSize)
        }
    }
    inner class Holder(val itemBinding : ItemAliveBinding) : RecyclerView.ViewHolder(itemBinding.root)
}
