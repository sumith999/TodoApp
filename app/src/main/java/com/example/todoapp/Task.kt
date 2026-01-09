package com.example.todoapp

data class Task(
    var title: String,
    var description: String,
    var priority: String = "Medium",
    var dueDate: String = "",
    var isCompleted: Boolean = false
)
