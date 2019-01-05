package com.firstapp.realm_notes_1_1

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.firstapp.realm_notes_1_1.adapter.NoteAdapter
import com.firstapp.realm_notes_1_1.model.Note
import io.realm.Realm
import io.realm.RealmConfiguration

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.ArrayList



class MainActivity : AppCompatActivity() {


    lateinit var realm: Realm
    lateinit var realmHelper: RealmHelper
    lateinit var stringArray: ArrayList<Note>
    lateinit var mdapter: NoteAdapter
    lateinit var deleteB: MenuItem
    lateinit var toolbar: ActionBar
    var changer = 0
    var rowpos = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(myToolbar)
        this.setTitle(R.string.main_screen)
        Realm.init(this)


        val realmConfig = RealmConfiguration.Builder()
                .name("notes.realm")
                .schemaVersion(2)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
        realm = Realm.getDefaultInstance()
        realmHelper = RealmHelper(realm)

        // Set the layout manager to the recyclerview
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = true
        mLayoutManager.stackFromEnd = true
        recyclerview.layoutManager = mLayoutManager

        //refreshing recycler
        refreshRecycler()

        //retrieve realm
        val helper = RealmHelper(realm)
        stringArray =helper.retrieve()

        //bindata
        mdapter= NoteAdapter(this, stringArray)
        recyclerview.adapter = mdapter

        //number of notes view
        var notesSize:TextView = findViewById(R.id.notesSize)
        numberOfNotes(notesSize)




        //setting up Swiping on rows
        val swipeHelper = object : SwipeToDelete(this){
                override fun onSwiped(ViewHolder: RecyclerView.ViewHolder?,direction: Int){
                    val adapter = recyclerview.adapter as NoteAdapter
                    rowpos = ViewHolder!!.adapterPosition
                    adapter.removeAt(ViewHolder!!.adapterPosition)

                    //deletes the note from the database & updates the notes counter
                    onSwipeDeletion(rowpos)
                    numberOfNotes(notesSize)

                }

        }



        //input back end action to delete note
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerview)

        //test code





    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.menu_main, menu)
        deleteB = menu.findItem(R.id.delete)
        hidingB()



        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Edit -> {
                changer += 1
                var databaseSize = realmHelper.retrieve()
                if (databaseSize.size > 0) {

                    if (changer % 2 != 0) {
                        showCheckBox()
                        refreshRecycler()
                    } else {
                        hideCheckBox()
                        refreshRecycler()
                    }
                } else{
                    showToast("There are no notes to edit")
                    changer = 0
                }

                true
            }
            R.id.delete-> {
                deleteByCheckBox()
                numberOfNotes(notesSize)
                refreshRecycler()
                true
            }
            else ->{
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRecycler()
    }



    override fun onDestroy() {
        super.onDestroy()
        realm.close()               //must close realm when done to prevent memory leakage
    }

    //starts the activity in charge of writing and editing the notes
    fun sendToWriter(view: View){
        if(changer % 2 != 0){
            changer=0
            hideCheckBox()
            refreshRecycler()
        }
        val intent = Intent(this,NoteCreation::class.java)
        startActivity(intent)
    }

    //delete all notes from database and refreshes the recyclerV
    fun deleteAll1(){
        realmHelper.deleteAll()
        refreshRecycler()
    }

    //refreshing recyclerV with new data
    fun refreshRecycler(){
        stringArray = realmHelper.retrieve()
        mdapter = NoteAdapter(this, stringArray)
        recyclerview.adapter = mdapter
        mdapter.notifyDataSetChanged()
    }

    //shows the desired text inside a toast notification
    fun showToast(t: String){
        Toast.makeText(applicationContext,t, Toast.LENGTH_LONG).show()
    }

    //shows/activates check boxes, changes the "edit" text to "cancel"
    fun showCheckBox(){
        deleteB.isVisible = true
        var edit = findViewById<TextView>(R.id.Edit)
        edit.text = "Cancel"
        mdapter = NoteAdapter(this, stringArray)
        var checkBox: CheckBox = findViewById(R.id.checkBox)
        var showingCheckBox = mdapter.ViewHolder(checkBox)
        showingCheckBox.checkBoxVisibility(checkBox)
    }

    //hides checkboxes and turns "cancel" text on button to "edit"
    fun hideCheckBox(){
        deleteB.isVisible = false
        var edit = findViewById<TextView>(R.id.Edit)
        edit.text = "Edit"
        mdapter = NoteAdapter(this, stringArray)
        var checkBox: CheckBox = findViewById(R.id.checkBox)
        var showingCheckBox = mdapter.ViewHolder(checkBox)
        showingCheckBox.checkBoxNoVisibility(checkBox)
    }

    //deletes notes that have the checkbox checked
    fun deleteByCheckBox(){
        mdapter = NoteAdapter(this,stringArray)
        var checkBox: CheckBox = findViewById(R.id.checkBox)
        var deleteCheckBoxes = mdapter.ViewHolder(checkBox)
        var postions = deleteCheckBoxes.getCheckPositions()
        realmHelper.deleteCheckBoxes(postions)
        deleteCheckBoxes.checkBoxNoVisibility(checkBox)
        deleteB.isVisible = false
        changer = 0
    }

    fun numberOfNotes(view:TextView){
        realmHelper.notesAmount(view)
    }

    fun hidingB(){
        if(changer ==0) {
            deleteB.isVisible = false
        }
    }

    //deletes the note from the database
    fun onSwipeDeletion(number: Int){
        realmHelper.deleteWhenSwipe(number)
    }



}


