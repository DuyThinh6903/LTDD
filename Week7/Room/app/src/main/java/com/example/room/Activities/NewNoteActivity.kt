package com.example.room.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.room.Database.NoteRoomDatabase
import com.example.room.R
import com.example.room.model.note
import kotlinx.android.synthetic.main.activity_new_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewNoteActivity : AppCompatActivity(), CoroutineScope {

    private var noteDB: NoteRoomDatabase?= null

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        mJob = Job()

        noteDB = NoteRoomDatabase.getDatabase(this)

        button_save.setOnClickListener {
            launch {
                val strTitle: String = title_note.text.toString()
                val strContent: String = content_note.text.toString()
                noteDB?.noteDao()?.insert(note(title = strTitle, content = strContent))

                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mJob.cancel()
    }
}
