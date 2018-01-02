package com.scottcolas.scott.todo

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_task_description.*

class TaskDescriptionActivity : AppCompatActivity() {
    // 1
    companion object {
        val EXTRA_TASK_DESCRIPTION = "task"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_description)
    }

fun onDoneClicked(view: View) {
    var newTask = descriptionText.text.toString()

    if (!newTask.isEmpty()) {
        var returnIntent = Intent()
        returnIntent.putExtra(EXTRA_TASK_DESCRIPTION, newTask)
        setResult(Activity.RESULT_OK, returnIntent)
    }else{
        setResult(Activity.RESULT_CANCELED)
    }
    finish()
}



}
