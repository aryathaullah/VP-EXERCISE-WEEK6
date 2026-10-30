package com.example.practice.campus.navigation

import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object CommunitiesRoute

@Serializable
data class ClubProjectsRoute(val clubId: String)

@Serializable
data class ProjectDetailRoute(val projectId: String)
