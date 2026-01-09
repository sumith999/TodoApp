package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDueDate: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var btnAdd: Button
    private lateinit var listView: ListView

    private val taskList = ArrayList<Task>()
    private lateinit var adapter: TaskAdapter
    private var selectedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTitle = findViewById(R.id.etTitle)
        etDescription = findViewById(R.id.etDescription)
        etDueDate = findViewById(R.id.etDueDate)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        btnAdd = findViewById(R.id.btnAdd)
        listView = findViewById(R.id.listView)

        setupDatePicker()
        loadTasks()
        setupAdapter()

        btnAdd.setOnClickListener {
            val title = etTitle.text.toString()
            val desc = etDescription.text.toString()
            val dueDate = etDueDate.text.toString()
            val priority = spinnerPriority.selectedItem.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedIndex == -1) {
                taskList.add(Task(title, desc, priority, dueDate))
            } else {
                val task = taskList[selectedIndex]
                task.title = title
                task.description = desc
                task.priority = priority
                task.dueDate = dueDate
                selectedIndex = -1
                btnAdd.text = "Add Task"
            }

            clearInputs()
            saveTasks()
            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val task = taskList[position]
            etTitle.setText(task.title)
            etDescription.setText(task.description)
            etDueDate.setText(task.dueDate)
            
            val priorityArray = resources.getStringArray(R.array.priority_levels)
            spinnerPriority.setSelection(priorityArray.indexOf(task.priority))
            
            selectedIndex = position
            btnAdd.text = "Update Task"
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            taskList.removeAt(position)
            saveTasks()
            adapter.notifyDataSetChanged()
            true
        }
    }

    private fun setupDatePicker() {
        etDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etDueDate.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupAdapter() {
        adapter = TaskAdapter(this, taskList) {
            saveTasks()
        }
        listView.adapter = adapter
    }

    private fun clearInputs() {
        etTitle.text.clear()
        etDescription.text.clear()
        etDueDate.text.clear()
        spinnerPriority.setSelection(1) // Default to Medium
    }

    private fun saveTasks() {
        val prefs = getSharedPreferences("tasks", MODE_PRIVATE)
        val json = Gson().toJson(taskList)
        prefs.edit().putString("taskList", json).apply()
    }

    private fun loadTasks() {
        val prefs = getSharedPreferences("tasks", MODE_PRIVATE)
        val json = prefs.getString("taskList", null) ?: return
        val type = object : TypeToken<ArrayList<Task>>() {}.type
        val savedTasks: ArrayList<Task> = Gson().fromJson(json, type)
        taskList.clear()
        taskList.addAll(savedTasks)
    }
}
