package com.example.todoapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class TaskAdapter(
    private val context: Context,
    private val taskList: ArrayList<Task>,
    private val onTaskStatusChanged: () -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = taskList.size

    override fun getItem(position: Int): Any = taskList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val task = taskList[position]

        val cbCompleted = view.findViewById<CheckBox>(R.id.cbCompleted)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDetails = view.findViewById<TextView>(R.id.tvDetails)
        val tvPriority = view.findViewById<TextView>(R.id.tvPriority)

        tvTitle.text = task.title
        tvDetails.text = "${task.dueDate} - ${task.description}".trimStart(' ', '-')
        tvPriority.text = task.priority

        // Set priority color
        when (task.priority) {
            "High" -> tvPriority.setBackgroundColor(Color.RED)
            "Medium" -> tvPriority.setBackgroundColor(Color.BLUE)
            else -> tvPriority.setBackgroundColor(Color.GREEN)
        }

        cbCompleted.setOnCheckedChangeListener(null)
        cbCompleted.isChecked = task.isCompleted

        updateTaskAppearance(tvTitle, task.isCompleted)

        cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted = isChecked
            updateTaskAppearance(tvTitle, isChecked)
            onTaskStatusChanged()
        }

        return view
    }

    private fun updateTaskAppearance(textView: TextView, isCompleted: Boolean) {
        if (isCompleted) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(Color.GRAY)
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(Color.BLACK)
        }
    }
}
