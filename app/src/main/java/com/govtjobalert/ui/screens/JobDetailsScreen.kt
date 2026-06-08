package com.govtjobalert.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.BackgroundGray
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    navController: NavController,
    jobId: String?,
    jobViewModel: JobViewModel = viewModel()
) {
    val context = LocalContext.current
    val job = jobViewModel.getJobById(jobId ?: "")
    val savedJobsSet by jobViewModel.bookmarkManager.savedJobIds.collectAsState()
    val isSaved = savedJobsSet.contains(jobId)

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Job details not found.", color = Color.Red)
            Button(onClick = { navController.navigateUp() }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Go Back")
            }
        }
        return
    }

    GovtJobAlertTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Job Details", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val saved = jobViewModel.bookmarkManager.toggleBookmark(job.id)
                            android.widget.Toast.makeText(
                                context,
                                if (saved) "Job Saved Successfully" else "Job Removed from Saved Jobs",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save Job",
                                tint = Color.White
                            )
                        }
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Check out this Govt Job: ${job.title} at ${job.department}. Apply now: ${job.websiteLink}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (job.notificationLink.isNotBlank()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(job.notificationLink)))
                                } else {
                                    android.widget.Toast.makeText(context, "Notification link not available", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Notification")
                        }
                        Button(
                            onClick = {
                                if (job.websiteLink.isNotBlank()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(job.websiteLink)))
                                } else {
                                    android.widget.Toast.makeText(context, "Application link not available", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apply Online")
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = job.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = job.department, fontSize = 16.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Section 1: Basic Information
                SectionTitle("Basic Information", Icons.Default.Info)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TableRow("Category", job.category)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        TableRow("Location", job.location)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        TableRow("Vacancies", job.vacancies)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        TableRow("Qualification", job.qualification)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        TableRow("Age Limit", job.ageLimit)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        TableRow("Salary", job.salary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Section 2: Important Dates
                SectionTitle("Important Dates", Icons.Default.Event)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Event", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Start Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            Text(job.startDate, modifier = Modifier.weight(1f))
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = BackgroundGray)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text("Last Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.error)
                            Text(job.endDate, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section 3: Application Details
                SectionTitle("Application Details", Icons.Default.Payment)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Application Fee", fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (job.applicationFee.isBlank()) "Not specified" else job.applicationFee, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun TableRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(0.4f),
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = if (value.isBlank()) "N/A" else value,
            modifier = Modifier.weight(0.6f),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}
