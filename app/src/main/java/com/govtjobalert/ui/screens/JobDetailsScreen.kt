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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
                    title = { Text("Job Details", color = Color.White, fontWeight = FontWeight.Bold) },
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
                                val postText = if (job.postName.isNotBlank()) " for ${job.postName}" else ""
                                putText(Intent.EXTRA_TEXT, "Check out this Govt Job: ${job.title}$postText. Apply now: ${job.websiteLink}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
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
                    .padding(16.dp)
            ) {
                // Top Section: Job Title & Post Name Card
                Text(
                    text = job.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp
                )
                
                if (job.postName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("POST NAME", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(job.postName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Basic Information Card
                SectionTitle("Basic Information", Icons.Default.Info)
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        InfoRow(Icons.Default.AccountBox, "Post Name", if (job.postName.isNotBlank()) job.postName else "N/A")
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = BackgroundGray)
                        InfoRow(Icons.Default.School, "Qualification", job.qualification)
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = BackgroundGray)
                        InfoRow(Icons.Default.LocationOn, "Location", job.location)
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = BackgroundGray)
                        InfoRow(Icons.Default.Group, "Vacancies", job.vacancies)
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = BackgroundGray)
                        InfoRow(Icons.Default.AttachMoney, "Salary", job.salary)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Important Dates Card (Table Format)
                SectionTitle("Important Dates", Icons.Default.Event)
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Table Header
                        Row(
                            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)).padding(16.dp)
                        ) {
                            Text("Event", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        // Table Body
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text("Start Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = Color.DarkGray)
                                Text(if (job.startDate.isBlank()) "N/A" else job.startDate, modifier = Modifier.weight(1f), color = Color.Black, fontWeight = FontWeight.Medium)
                            }
                            Divider(color = BackgroundGray)
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text("Last Date", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = Color.DarkGray)
                                Text(if (job.endDate.isBlank()) "N/A" else job.endDate, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Application Fee Card (Table Format)
                SectionTitle("Application Fee", Icons.Default.Payment)
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column {
                        // Table Header
                        Row(
                            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)).padding(16.dp)
                        ) {
                            Text("Category", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Fee", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        // Table Body
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text("General / OBC", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = Color.DarkGray)
                                Text(if (job.generalFee.isBlank() || job.generalFee == "0") "₹0" else "₹${job.generalFee}", modifier = Modifier.weight(1f), color = Color.Black, fontWeight = FontWeight.Medium)
                            }
                            Divider(color = BackgroundGray)
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                                Text("SC / ST / PwD", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, color = Color.DarkGray)
                                Text(if (job.scstFee.isBlank() || job.scstFee == "0") "₹0" else "₹${job.scstFee}", modifier = Modifier.weight(1f), color = Color.Black, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                // Important Links Card
                SectionTitle("Important Links", Icons.Default.Link)
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                if (job.websiteLink.isNotBlank()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(job.websiteLink)))
                                } else {
                                    android.widget.Toast.makeText(context, "Application link not available", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Apply Online", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedButton(
                            onClick = {
                                if (job.notificationLink.isNotBlank()) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(job.notificationLink)))
                                } else {
                                    android.widget.Toast.makeText(context, "Notification link not available", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Notification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)) {
        Box(
            modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(if (value.isBlank()) "N/A" else value, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
