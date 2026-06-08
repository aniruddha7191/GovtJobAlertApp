package com.govtjobalert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.BackgroundGray
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.AdminViewModel
import com.govtjobalert.ui.theme.PrimaryGreen
import com.govtjobalert.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel()
) {
    val jobs by adminViewModel.filteredJobs.collectAsState(initial = emptyList())
    val isLoading by adminViewModel.isLoading.collectAsState()
    val totalJobsCount by adminViewModel.totalJobsCount.collectAsState()
    val publishedJobsCount by adminViewModel.publishedJobsCount.collectAsState()
    
    val categories = listOf("All Jobs", "Central Govt Jobs", "State Govt Jobs", "Bank Jobs", "PSU Jobs", "Railway Jobs", "Other Jobs")
    var selectedCategoryIndex by remember { mutableStateOf(0) }

    var showDeleteConfirmDialog by remember { mutableStateOf<String?>(null) }

    GovtJobAlertTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin Dashboard", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                    actions = {
                        IconButton(onClick = { navController.navigate("splash") { popUpTo(0) } }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("addJob") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Job")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .padding(paddingValues)
            ) {
                // Dashboard Stats
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard("Total Jobs", totalJobsCount.toString(), Modifier.weight(1f))
                    StatCard("Published", publishedJobsCount.toString(), Modifier.weight(1f))
                    StatCard("Drafts", (totalJobsCount - publishedJobsCount).toString(), Modifier.weight(1f))
                }

                // Category Filter Scrollable Row
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedCategoryIndex == index,
                            onClick = {
                                selectedCategoryIndex = index
                                adminViewModel.setCategory(category)
                            },
                            text = { Text(category, fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (jobs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No jobs found in this category.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(jobs, key = { it.id }) { job ->
                            AdminJobCard(
                                job = job,
                                onEdit = { navController.navigate("editJob/${job.id}") },
                                onDeleteClick = { showDeleteConfirmDialog = job.id }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Job") },
            text = { Text("Are you sure you want to permanently delete this job?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        adminViewModel.deleteJob(
                            jobId = showDeleteConfirmDialog!!,
                            onSuccess = { showDeleteConfirmDialog = null },
                            onError = { showDeleteConfirmDialog = null }
                        )
                    }
                ) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AdminJobCard(job: JobModel, onEdit: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = job.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(
                            if (job.isPublished) PrimaryGreen.copy(alpha = 0.1f) else PrimaryRed.copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (job.isPublished) "Published" else "Draft",
                        color = if (job.isPublished) PrimaryGreen else PrimaryRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = job.department, fontSize = 14.sp, color = Color.DarkGray)
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BackgroundGray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }
    }
}
