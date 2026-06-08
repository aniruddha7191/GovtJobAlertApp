package com.govtjobalert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.BackgroundGray
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobScreen(
    navController: NavController,
    jobId: String? = null,
    adminViewModel: AdminViewModel = viewModel()
) {
    val isEditing = jobId != null
    var job by remember { mutableStateOf(JobModel()) }
    
    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Central Govt Jobs", "State Govt Jobs", "Bank Jobs", "PSU Jobs", "Railway Jobs", "Other Jobs")

    LaunchedEffect(jobId) {
        if (isEditing) {
            adminViewModel.getJobById(jobId!!)?.let {
                job = it
            }
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    GovtJobAlertTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isEditing) "Edit Job" else "Add New Job", color = Color.White) },
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
                    .background(BackgroundGray)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = job.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Job Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    job = job.copy(category = category)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Standard Text Fields
                AdminTextField(value = job.title, label = "Job Title") { job = job.copy(title = it) }
                AdminTextField(value = job.department, label = "Department Name") { job = job.copy(department = it) }
                AdminTextField(value = job.vacancies, label = "Vacancies (e.g. 100+)", keyboardType = KeyboardType.Number) { job = job.copy(vacancies = it) }
                AdminTextField(value = job.qualification, label = "Qualification") { job = job.copy(qualification = it) }
                AdminTextField(value = job.ageLimit, label = "Age Limit") { job = job.copy(ageLimit = it) }
                AdminTextField(value = job.salary, label = "Salary") { job = job.copy(salary = it) }
                AdminTextField(value = job.location, label = "Location") { job = job.copy(location = it) }
                AdminTextField(value = job.applicationFee, label = "Application Fee") { job = job.copy(applicationFee = it) }
                AdminTextField(value = job.websiteLink, label = "Apply Online URL") { job = job.copy(websiteLink = it) }
                AdminTextField(value = job.notificationLink, label = "Download Notification URL") { job = job.copy(notificationLink = it) }

                // Date Pickers
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { showStartDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (job.startDate.isBlank()) "Start Date" else job.startDate)
                    }
                    OutlinedButton(onClick = { showEndDatePicker = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (job.endDate.isBlank()) "Last Date" else job.endDate)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            val jobToSave = job.copy(isPublished = false)
                            if (isEditing) {
                                adminViewModel.updateJob(jobToSave, onSuccess = { navController.navigateUp() }, onError = {})
                            } else {
                                adminViewModel.addJob(jobToSave, onSuccess = { navController.navigateUp() }, onError = {})
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Save Draft")
                    }

                    Button(
                        onClick = {
                            val jobToSave = job.copy(isPublished = true)
                            if (isEditing) {
                                adminViewModel.updateJob(jobToSave, onSuccess = { navController.navigateUp() }, onError = {})
                            } else {
                                adminViewModel.addJob(jobToSave, onSuccess = { navController.navigateUp() }, onError = {})
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Publish Job")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        job = job.copy(startDate = formatMillisToDate(it))
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        job = job.copy(endDate = formatMillisToDate(it))
                    }
                    showEndDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTextField(
    value: String,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

fun formatMillisToDate(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}
