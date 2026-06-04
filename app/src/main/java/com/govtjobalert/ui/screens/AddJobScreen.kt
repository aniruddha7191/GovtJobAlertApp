package com.govtjobalert.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.AdminViewModel

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
                    label = { Text("Job Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = job.department,
                    onValueChange = { job = job.copy(department = it) },
                    label = { Text("Department") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = job.vacancies,
                        onValueChange = { job = job.copy(vacancies = it) },
                        label = { Text("Vacancy") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = job.ageLimit,
                        onValueChange = { job = job.copy(ageLimit = it) },
                        label = { Text("Age Limit") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = job.qualification,
                    onValueChange = { job = job.copy(qualification = it) },
                    label = { Text("Qualification") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = job.salary,
                    onValueChange = { job = job.copy(salary = it) },
                    label = { Text("Salary") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = job.startDate,
                        onValueChange = { job = job.copy(startDate = it) },
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = job.endDate,
                        onValueChange = { job = job.copy(endDate = it) },
                        label = { Text("Last Date") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                OutlinedTextField(
                    value = job.applicationFee,
                    onValueChange = { job = job.copy(applicationFee = it) },
                    label = { Text("Application Fee") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = job.location,
                    onValueChange = { job = job.copy(location = it) },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = job.imageUrl,
                    onValueChange = { job = job.copy(imageUrl = it) },
                    label = { Text("Image/Logo URL") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = job.websiteLink,
                    onValueChange = { job = job.copy(websiteLink = it) },
                    label = { Text("Official Website Link") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = job.notificationLink,
                    onValueChange = { job = job.copy(notificationLink = it) },
                    label = { Text("Official Notification Link") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = job.isPublished,
                        onCheckedChange = { job = job.copy(isPublished = it) }
                    )
                    Text("Publish Immediately")
                }

                Button(
                    onClick = {
                        val onSuccess: () -> Unit = {
                            Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                            navController.navigateUp()
                            Unit
                        }
                        val onError: (String) -> Unit = {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
                        
                        if (isEditMode) {
                            adminViewModel.updateJob(job, onSuccess, onError)
                        } else {
                            adminViewModel.addJob(job, onSuccess, onError)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Save Job")
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
