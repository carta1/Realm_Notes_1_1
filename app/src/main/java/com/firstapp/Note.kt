package com.firstapp.realm_notes_1_1.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.text.FieldPosition
import kotlin.properties.Delegates

@RealmClass
open class Note: RealmObject(){
    @PrimaryKey
    open lateinit var primId: String
    open lateinit var noteTitle: String
    open lateinit var noteContent: String
    open lateinit var noteDate: String
    open var notePosition = 1


}