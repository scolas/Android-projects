package com.scottcolas.scott.todo

import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.util.Log
import java.sql.Date
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val ADD_TASK_REQUEST = 1
    private val taskList: MutableList<String> = mutableListOf()
    private val adapter by lazy {makeAdapter(taskList)}
    private val tickReceiver by lazy { makeBroadcastReceiver() }
    private val PREFS_TASKS = "prefs_tasks"
    private val KEY_TASKS_LIST = "tasks_list"

    companion object {
        private const val Log_TAG = "MainActivityLog"

        private fun getCurrentTimeStamp(): String{
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val now = Date()
            return simpleDateFormat.format(now)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskListView.adapter = adapter

        taskListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            taskSelected(position)
        }

        val savedList = getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).getString(KEY_TASKS_LIST, null)
        if(savedList != null){
            val items = savedList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            taskList.addAll(items)
        }
    }

    override fun onResume() {
        super.onResume()
        time_text_view.text = getCurrentTimeStamp()
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onPause() {
        super.onPause()
        try {

        }
        catch (e: IllegalArgumentException){
            Log.e(MainActivity.Log_TAG,"Time tick Receiver not registered", e)
        }
    }

    override fun onStop() {
        super.onStop()
        val savedList = StringBuilder()
        for(task in taskList){
            savedList.append(task)
            savedList.append(",")
        }
        getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).edit()
                .putString(KEY_TASKS_LIST, savedList.toString()).apply()
    }





    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    fun addTaskClicked(view: View){
        var intent = Intent(this, TaskDescriptionActivity::class.java)
        startActivityForResult(intent, ADD_TASK_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == ADD_TASK_REQUEST){
            if (resultCode == Activity.RESULT_OK){
                val task = data?.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION)
                task?.let {
                    taskList.add(task)
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun makeAdapter(list: List<String>): ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

    private fun makeBroadcastReceiver(): BroadcastReceiver{
        return object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if(intent?.action == Intent.ACTION_TIME_TICK){
                    time_text_view.text = getCurrentTimeStamp()
                }
            }
        }
    }

    private fun taskSelected (position: Int){
        AlertDialog.Builder(this)
                .setTitle(R.string.alert_title)
                .setMessage(taskList[position])
                .setPositiveButton(R.string.delete, { _, _ ->
                    taskList.removeAt(position)
                    adapter.notifyDataSetChanged()
                })
                .setNegativeButton(R.string.cancel, {
                    dialog, _ -> dialog.cancel()
                })
                // 4
                .create()
                // 5
                .show()
    }
}
