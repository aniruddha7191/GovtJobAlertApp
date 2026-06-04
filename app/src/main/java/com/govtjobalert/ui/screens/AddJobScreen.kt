package com.govtjobalert.ui.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.AdminViewModel
import java.util.Calendar

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
        onDateSelected(formattedDate)
    }, year, month, day).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobScreen(
    navController: NavController,
    jobId: String? = null,
    adminViewModel: AdminViewModel = viewModel()
) {
    val context = LocalContext.current
    val isEditMode = jobId != null
    var job by remember { mutableStateOf(JobModel()) }

    LaunchedEffect(jobId) {
        if (isEditMode) {
            val existingJob = adminViewModel.getJobById(jobId!!)
            if (existingJob != null) {
                job = existingJob
            }
        }
    }

    GovtJobAlertTheme(isAdmin = true) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditMode) "Edit Job" else "Add New Job", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = job.title,
                    onValueChange = { job = job.copy(title = it) },
                    label = { Text("Job Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = job.department,
                    onValueChange = { job = job.copy(department = it) },
                    label = { Text("Department *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = job.vacancies,
                        onValueChange = { job = job.copy(vacancies = it) },
                        label = { Text("Vacancy") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = job.ageLimit,
                        onValueChange = { job = job.copy(ageLimit = it) },
                        label = { Text("Age Limit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = job.qualification,
                    onValueChange = { job = job.copy(qualification = it) },
                    label = { Text("Qualification") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = job.salary,
                    onValueChange = { job = job.copy(salary = it) },
                    label = { Text("Salary") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = job.startDate,
                        onValueChange = { },
                        label = { Text("Start Date *") },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = { 
                            IconButton(onClick = { showDatePicker(context) { date -> job = job.copy(startDate = date) } }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Start Date")
                            }
                        }
                    )
                    OutlinedTextField(
                        value = job.endDate,
                        onValueChange = { },
                        label = { Text("Last Date *") },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = { 
                            IconButton(onClick = { showDatePicker(context) { date -> job = job.copy(endDate = date) } }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick End Date")
                            }
                        }
                    )
                }
                
                OutlinedTextField(
                    value = job.applicationFee,
                    onValueChange = { job = job.copy(applicationFee = it) },
                    label = { Text("Application Fee") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = job.location,
                    onValueChange = { job = job.copy(location = it) },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                OutlinedTextField(
                    value = job.imageUrl,
                    onValueChange = { job = job.copy(imageUrl = it) },
                    label = { Text("Logo/Image URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = job.websiteLink,
                    onValueChange = { job = job.copy(websiteLink = it) },
                    label = { Text("Official Website Link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = job.notificationLink,
                    onValueChange = { job = job.copy(notificationLink = it) },
                    label = { Text("Official Notification Link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                val saveJobFunction = { publish: Boolean ->
                    if (job.title.isBlank() || job.department.isBlank() || job.startDate.isBlank() || job.endDate.isBlank()) {
                        Toast.makeText(context, "Please fill in all required fields (*)", Toast.LENGTH_SHORT).show()
                    } else {
                        val jobToSave = job.copy(isPublished = publish)
                        val onSuccess: () -> Unit = {
                            val msg = if (publish) "Job Published Successfully" else "Job Saved as Draft"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                            Unit
                        }
                        val onError: (String) -> Unit = { err ->
                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                        }
                        
                        if (isEditMode) {
                            adminViewModel.updateJob(jobToSave, onSuccess, onError)
                        } else {
                            adminViewModel.addJob(jobToSave, onSuccess, onError)
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { saveJobFunction(false) },
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Save as Draft")
                    }
                    Button(
                        onClick = { saveJobFunction(true) },
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Publish Job")
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
