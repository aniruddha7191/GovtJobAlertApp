package com.govtjobalert.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Bookmark
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
import com.govtjobalert.ui.theme.BackgroundGray
import com.govtjobalert.ui.theme.GovtJobAlertTheme
import com.govtjobalert.viewmodels.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedJobsScreen(
    navController: NavController,
    jobViewModel: JobViewModel = viewModel()
) {
    val savedIds by jobViewModel.bookmarkManager.savedJobIds.collectAsState()
    val savedJobs = jobViewModel.getSavedJobs(savedIds)

    GovtJobAlertTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Saved Jobs", color = Color.White) },
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
            ) {
                if (savedJobs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No Saved Jobs Yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedJobs, key = { it.id }) { job ->
                            val dismissState = rememberDismissState(
                                confirmValueChange = {
                                    if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                                        jobViewModel.bookmarkManager.toggleBookmark(job.id)
                                        true
                                    } else false
                                }
                            )

                            SwipeToDismiss(
                                state = dismissState,
                                background = {
                                    val color = MaterialTheme.colorScheme.error
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                                    }
                                },
                                dismissContent = {
                                    JobCardRedesigned(
                                        job = job,
                                        onClick = { navController.navigate("jobDetails/${job.id}") }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
