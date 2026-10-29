package com.example.practice.campus.data

import com.example.practice.campus.model.Community

object CommunityRepository {
    fun getCommunityList(): List<Community> = listOf(
        Community("gdsc", "GDSC", "Google Developer Student Club",
            "Fokus pada ekosistem Google: Android Compose, Flutter, Firebase, dan Google Cloud Platform.", 142),
        Community("cyber", "CyberSecurity Guild", "Security & Pentesting",
            "Eksplorasi ethical hacking, CTF challenges, reverse engineering, dan pertahanan jaringan kampus.", 88),
        Community("ai", "AI Society", "Machine Learning & LLM",
            "Riset terapan deep learning, computer vision, agen cerdas, dan fine-tuning large language models.", 115),
        Community("mobile", "Mobile Dev Community", "Native Jetpack Compose & iOS",
            "Bangun aplikasi mobile dengan Kotlin, Jetpack Compose, dan pengembangan iOS.", 95),
        Community("web", "Web Dev Community", "Frontend & Backend Development",
            "Belajar membangun website interaktif, REST API, dan aplikasi web bersama komunitas.", 120),
        Community("cloud", "Cloud Computing Club", "Cloud Infrastructure & DevOps",
            "Eksplorasi layanan cloud, deployment aplikasi, container, dan otomasi infrastruktur.", 76)
    )
}
