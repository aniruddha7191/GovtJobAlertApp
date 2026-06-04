package com.govtjobalert.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.govtjobalert.R
import com.govtjobalert.viewmodels.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    navController: NavController,
    jobId: String?,
    jobViewModel: JobViewModel = viewModel()
) {
    val job = jobId?.let { jobViewModel.getJobById(it) }
    val context = LocalContext.current

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Job not found")
        }
        return
    }

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
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, job.title)
                            putExtra(Intent.EXTRA_TEXT, "Check out this govt job: ${job.title} at ${job.department}\nLast Date: ${job.endDate}\nMore details: ${job.websiteLink}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Job via"))
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
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = if (job.imageUrl.isNotEmpty()) job.imageUrl else R.drawable.app_logo,
                    contentDescription = "Job Logo",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = job.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = job.department, color = Color.Gray, fontSize = 14.sp)
                }
            }

            Divider(color = Color.LightGray)

            // Details Table
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("Total Vacancies", job.vacancies)
                DetailRow("Qualification", job.qualification)
                DetailRow("Age Limit", job.ageLimit)
                DetailRow("Application Fee", job.applicationFee)
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Important Dates", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                DetailRow("Start Date", job.startDate)
                DetailRow("Last Date", job.endDate)
                
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("Salary", job.salary)
                DetailRow("Location", job.location)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (job.websiteLink.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.websiteLink))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Visit Official Website")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = {
                        if (job.notificationLink.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.notificationLink))
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Read Full Notification")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f),
            color = Color.DarkGray
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            color = Color.Black
        )
    }
}
