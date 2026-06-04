package com.govtjobalert.models

data class JobModel(
    var id: String = "",
    var title: String = "",
    var department: String = "",
    var imageUrl: String = "",
    var vacancies: String = "",
    var qualification: String = "",
    var ageLimit: String = "",
    var applicationFee: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var salary: String = "",
    var location: String = "All India",
    var websiteLink: String = "",
    var notificationLink: String = "",
    @get:com.google.firebase.firestore.PropertyName("isPublished")
    @set:com.google.firebase.firestore.PropertyName("isPublished")
    var isPublished: Boolean = true,
    var createdAt: Long = System.currentTimeMillis()
)
