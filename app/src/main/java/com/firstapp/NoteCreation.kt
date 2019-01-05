package com.firstapp.realm_notes_1_1

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.firstapp.realm_notes_1_1.adapter.EXTRA_MESSAGE
import com.firstapp.realm_notes_1_1.adapter.NoteAdapter
import com.firstapp.realm_notes_1_1.model.Note
import io.realm.Realm
import io.realm.RealmConfiguration
import java.util.*
import com.firstapp.realm_notes_1_1.adapter.EXTRA_MESSAGE2


const val EXTRA_MESSAGE3 = "com.example.myfirstapp.MESSAGE3"

class NoteCreation : AppCompatActivity() {

    lateinit var realm: Realm
    lateinit var realmHelper: RealmHelper
    lateinit var stringArray: ArrayList<Note>
    lateinit var mdapter: NoteAdapter
    lateinit var mLayoutManager: LinearLayoutManager
    lateinit var insideMessage: String
    lateinit var insideMessage2: String
    var visibilityDetect = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_creation)
        setSupportActionBar(findViewById(R.id.myToolbar2))

        realm = Realm.getDefaultInstance()
        realmHelper = RealmHelper(realm)

        //displaying the row(note) user press and search realm object and update it if needed
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val message2 = intent.getStringExtra(EXTRA_MESSAGE2)

        if (message != null){
            var editText = findViewById<EditText>(R.id.editTextNote)
            editText.setText(message)

            editText.isCursorVisible = false
            insideMessage = message
            insideMessage2 = message2
            visibilityDetect = 1
            editText.setOnClickListener {
               editText.isCursorVisible = true
            }



        }
        else{
            visibilityDetect = 0
        }
    }

    //must close realm when done to prevent memory leakage
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.menu_notecreation, menu)
        var updateMenuItem: MenuItem = menu.findItem(R.id.action_settings)
        var saveMenuItem: MenuItem = menu.findItem(R.id.save_action)

        /*hides update button if user is creating new note and hides(make invisible save button if
         user is updating existing note*/
        if (visibilityDetect == 0) {
            updateMenuItem.isVisible = false
        }else{
            saveMenuItem.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                updatingNote()
                sendHome()

                true
            }
            R.id.back_action-> {
               finish()

                true
            }

            else -> {
                R.id.save_action
                addNoteToRealm()
                sendHome()

                true
            }
        }
    }

    /*else -> {
                R.id.save_action
                addNoteToRealm()
                sendHome()

                true
            }*/



    //Add new object to Realm database
    fun addNoteToRealm(){
       val editTextNote = findViewById<EditText>(R.id.editTextNote)
       val newNote = Note()
       var separate = editTextNote.text.toString()
        //assigns the counter for position
        var hello = realmHelper.retrieve()

        newNote.noteTitle = separate
        newNote.noteContent = separate
        newNote.noteDate = Date().toString()
        newNote.notePosition = hello.size
        realmHelper.save(newNote)
   }

    /*finds notes when touch and updates  its position to first (last) while adjusting the positions
     of the whole realm database (lowering the ones above to give space for the clicked note to move
     to the top of the Rview (last position)*/
    fun updatingNote(){
        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        var separate = editTextNote.text.toString()
        val updateNote = Note()
        updateNote.noteTitle = separate
        updateNote.noteContent = separate
        updateNote.noteDate = Date().toString()
        //showToast("inside message $insideMessage2")
        realmHelper.findAndUpdate(updateNote,insideMessage,insideMessage2.toInt())
    }

    //intent to go back to main class
    fun sendHome(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    fun showToast(t: String){
        Toast.makeText(applicationContext,t,Toast.LENGTH_LONG).show()
    }



}
