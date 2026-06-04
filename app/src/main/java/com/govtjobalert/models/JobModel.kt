package com.govtjobalert.models

data class JobModel(
    val id: String = "",
    val title: String = "",
    val department: String = "",
    val imageUrl: String = "",
    val vacancies: String = "",
    val qualification: String = "",
    val ageLimit: String = "",
    val applicationFee: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val salary: String = "",
    val location: String = "All India",
    val websiteLink: String = "",
    val notificationLink: String = "",
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
