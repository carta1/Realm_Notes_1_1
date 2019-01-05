package com.firstapp.realm_notes_1_1

import android.widget.TextView
import com.firstapp.realm_notes_1_1.model.Note
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import java.util.*
import kotlin.collections.ArrayList

class RealmHelper(internal var realm: Realm){

    //Writes to realm database
    fun save(noteitem: Note){
        realm.executeTransaction { realm ->
            val realmResult = realm.createObject(Note::class.java,UUID.randomUUID().toString())
            realmResult.noteTitle = noteitem.noteTitle
            realmResult.noteContent = noteitem.noteContent
            realmResult.noteDate = noteitem.noteDate
            realmResult.notePosition = noteitem.notePosition
        }
    }

    /*finds notes when touch and updates it, updating the content if some, position (to first),
    and time while adjusting the position of the whole realm database*/
    fun findAndUpdate(noteitem: Note, v: String, sFrom: Int){
        realm.executeTransaction { realm ->
            var realmResults = realm.where(Note::class.java).findAll()
            var list: List<Note> = realmResults

            //this loop updates the note positions of all objects inside the database
            for (i in 0..realmResults.size-1){
                var note: Note = list.get(i)
                if (note.noteTitle == v){
                    note.notePosition = realmResults.size-1
                    println("this is the note: $note")
                    realm.copyToRealmOrUpdate(note)
                }
                if (note.noteTitle!= v && note.notePosition > sFrom){
                    note.notePosition-=1
                    println("Decrease note: $note")
                    realm.copyToRealmOrUpdate(note)
                }
            }
            //saves the new data to update the note
            var realmResult: Note = realm.where(Note::class.java).equalTo("noteTitle", v).findFirstAsync()
            realmResult.noteTitle = noteitem.noteTitle
            realmResult.noteContent = noteitem.noteContent
            realmResult.noteDate = noteitem.noteDate
            realm.copyToRealmOrUpdate(realmResult)
        }
    }

    //retrieves the data inside the realm database
    fun retrieve(): ArrayList<Note>{
        val arraylist = ArrayList<Note>(realm.where(Note::class.java).sort("notePosition",Sort.ASCENDING).findAll())
        println("printing $arraylist")
        return arraylist
    }

    //deletes all data from the realm database
    fun deleteAll(){
        realm.executeTransaction { realm ->
            val realmResult = realm.where(Note::class.java).findAll()
            realmResult.deleteAllFromRealm()
        }
    }

    fun deleteCheckBoxes(array: ArrayList<Int>){
        realm.executeTransaction { realm ->
           for (i in array){
               var realmResult = realm.where(Note::class.java).equalTo("notePosition",i).findFirstAsync()
               println("number inside array: $realmResult")
               realmResult.deleteFromRealm()
           }
            var realmResult = realm.where(Note::class.java).findAll().sort("notePosition",Sort.ASCENDING)
            var list: List<Note> = realmResult
            println("the list is: $realmResult")
            for (i in 0 until realmResult.size){
                var note: Note = list.get(i)
               note.notePosition = i
                realm.copyToRealmOrUpdate(note)
            }
        }

    }

    fun notesAmount(view: TextView){
        var realmResult = realm.where(Note::class.java).findAll()
        var size = realmResult.size.toString()
        view.text = size + " Notes"
    }

    fun deleteWhenSwipe(number: Int) {
        realm.executeTransaction { realm ->
            var realmResult = realm.where(Note::class.java).equalTo("notePosition", number).findFirstAsync()
            realmResult.deleteFromRealm()


         var realmResult2 = realm.where(Note::class.java).findAll().sort("notePosition", Sort.ASCENDING)
        var list: List<Note> = realmResult2
        println("the list is: $realmResult")
        for (i in 0 until realmResult2.size) {
            var note: Note = list.get(i)
            note.notePosition = i
            realm.copyToRealmOrUpdate(note)
        }
    }


    }


}