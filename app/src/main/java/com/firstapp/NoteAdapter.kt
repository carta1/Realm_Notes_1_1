package com.firstapp.realm_notes_1_1.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firstapp.realm_notes_1_1.NoteCreation
import com.firstapp.realm_notes_1_1.R
import com.firstapp.realm_notes_1_1.R.layout.note_rows
import com.firstapp.realm_notes_1_1.model.Note
import kotlinx.android.synthetic.main.note_rows.view.*
import kotlin.math.ln

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"
const val EXTRA_MESSAGE2 = "com.example.myfirstapp.MESSAGE2"
var checkBoxStatus = 0
lateinit var array: ArrayList<Int>


open class NoteAdapter(val context: Context, val stringarray: ArrayList<Note>): RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent!!.context).inflate(note_rows, parent, false)

        array = ArrayList()
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stringarray.size
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        holder!!.bindView(context,stringarray[position])

    }


    inner class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView){
        var title = itemView!!.findViewById<TextView>(R.id.note_title_textview)
        var date = itemView!!.findViewById<TextView>(R.id.note_date_textview)
        var checkBox= itemView!!.findViewById<CheckBox>(R.id.checkBox)




        fun bindView(context: Context,nameView: Note){
            title.text = nameView.noteTitle.trim()
            date.text = nameView.noteDate

            /*checks if condition for checkbox to appear is valid (chekcboxStatus is 0 = appear or
            1 = not appear, if the checkbox are visible a clicklistener activates and gather the
            position*/
            if (checkBoxStatus ==1){
                checkBoxVisibility(checkBox)
                var position = adapterPosition
                checkBoxPos(position)
                getCheckPositions()
            }
            else{
                checkBoxNoVisibility(checkBox)
            }

            /*listens to the touch of a note on the main page and opens it, while sending its
            position, content, and opening the update/edit/addnote activity*/
            itemView.setOnClickListener {
                if (checkBoxStatus ==0) {
                    val message = nameView.noteContent
                    val secondMessage = adapterPosition
                    val extras: Bundle = Bundle()
                    val intent = Intent(itemView.context, NoteCreation::class.java).apply {
                        extras.putString(EXTRA_MESSAGE, message)
                        extras.putString(EXTRA_MESSAGE2, secondMessage.toString())
                    }
                    intent.putExtras(extras)
                    itemView.context.startActivity(intent)
                }
            }

        }

        //makes checkboxes visible
        fun checkBoxVisibility(view: View){
            array = ArrayList()
            var checkBox:CheckBox = view.findViewById(R.id.checkBox)
            checkBox.visibility = View.VISIBLE
            checkBoxStatus = 1

        }
        //makes checkboxes invisible
        fun checkBoxNoVisibility(view: View){
            array = ArrayList()
            var checkBox:CheckBox = view.findViewById(R.id.checkBox)
            checkBox.visibility = View.INVISIBLE
            checkBoxStatus = 0

        }

        fun checkBoxPos(position: Int): ArrayList<Int>{
            checkBox.setOnClickListener {
                if (checkBox.isChecked){
                    array.add(position)

                }
                else{
                    array.remove(position)

                }
            }
            return array
        }

        fun getCheckPositions(): ArrayList<Int>{
            return array
        }

    }

    fun removeAt(position: Int) {
        stringarray.removeAt(position)
        notifyItemRemoved(position)
    }

}




