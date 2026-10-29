package com.example.practice.campus.model

data class Project(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val techStack: List<String>,
    val completed: Boolean = false
)
