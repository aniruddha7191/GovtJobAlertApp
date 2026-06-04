package com.govtjobalert.models

import com.google.firebase.firestore.DocumentId

data class JobModel(
    @DocumentId var id: String = "",
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
    var isPublished: Boolean = false,
    var createdAt: Long = System.currentTimeMillis()
)
