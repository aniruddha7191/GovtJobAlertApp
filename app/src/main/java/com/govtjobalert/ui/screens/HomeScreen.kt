package com.govtjobalert.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.govtjobalert.models.JobModel
import com.govtjobalert.ui.theme.*
import com.govtjobalert.viewmodels.JobViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    jobViewModel: JobViewModel = viewModel()
) {
    val jobs by jobViewModel.filteredJobs.collectAsState(initial = emptyList())
    val isLoading by jobViewModel.isLoading.collectAsState()
    val error by jobViewModel.error.collectAsState()
    val selectedCategory by jobViewModel.selectedCategory.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val displayJobs = if (searchQuery.isBlank()) jobs else jobViewModel.searchJobs(searchQuery)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("admin_prefs", android.content.Context.MODE_PRIVATE)
    var isAdminLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_logged_in", false)) }

    LaunchedEffect(Unit) {
        isAdminLoggedIn = prefs.getBoolean("is_logged_in", false)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Govt Job Alert",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Divider()
                
                // Categories
                NavigationDrawerItem(
                    label = { Text("All Jobs") },
                    selected = selectedCategory == "All Jobs",
                    onClick = { jobViewModel.setCategory("All Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Work, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Central Govt Jobs") },
                    selected = selectedCategory == "Central Govt Jobs",
                    onClick = { jobViewModel.setCategory("Central Govt Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("State Govt Jobs") },
                    selected = selectedCategory == "State Govt Jobs",
                    onClick = { jobViewModel.setCategory("State Govt Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Bank Jobs") },
                    selected = selectedCategory == "Bank Jobs",
                    onClick = { jobViewModel.setCategory("Bank Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("PSU Jobs") },
                    selected = selectedCategory == "PSU Jobs",
                    onClick = { jobViewModel.setCategory("PSU Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Railway Jobs") },
                    selected = selectedCategory == "Railway Jobs",
                    onClick = { jobViewModel.setCategory("Railway Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.DirectionsRailway, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Other Jobs") },
                    selected = selectedCategory == "Other Jobs",
                    onClick = { jobViewModel.setCategory("Other Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                val savedJobsSet by jobViewModel.bookmarkManager.savedJobIds.collectAsState()
                
                // Other pages
                NavigationDrawerItem(
                    label = { 
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Saved Jobs")
                            if (savedJobsSet.isNotEmpty()) {
                                Badge { Text(savedJobsSet.size.toString()) }
                            }
                        }
                    },
                    selected = false,
                    onClick = { navController.navigate("savedJobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("About App") },
                    selected = false,
                    onClick = { navController.navigate("about"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Privacy Policy") },
                    selected = false,
                    onClick = { navController.navigate("privacy"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Policy, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Contact Us") },
                    selected = false,
                    onClick = { navController.navigate("contact"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Share App") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Download Govt Job Alert for latest updates!")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, null))
                    },
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Rate App") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    label = { Text(if (isAdminLoggedIn) "Admin Dashboard" else "Admin Login", color = MaterialTheme.colorScheme.primary) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        if (isAdminLoggedIn) {
                            navController.navigate("adminDashboard")
                        } else {
                            navController.navigate("adminLogin")
                        }
                    },
                    icon = { Icon(androidx.compose.material.icons.filled.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        GovtJobAlertTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(selectedCategory, color = Color.White) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(BackgroundGray)
                ) {
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        placeholder = { Text("Search jobs by title, department...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    // Latest Jobs Section Title & Counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Latest Jobs",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "${displayJobs.size} Active Jobs Available",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (error != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = error ?: "Unknown error", color = Color.Red, modifier = Modifier.padding(16.dp))
                        }
                    } else if (displayJobs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No jobs found.", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(displayJobs) { job ->
                                JobCardRedesigned(
                                    job = job,
                                    onClick = { navController.navigate("jobDetails/${job.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobCardRedesigned(job: JobModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Category Badge & Publish Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryBadge(category = job.category)
                PublishDatePill(timestamp = job.createdAt)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Job Title
            Text(
                text = job.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Department
            Text(
                text = job.department,
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            
            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(icon = Icons.Default.School, text = job.qualification)
                InfoItem(icon = Icons.Default.LocationOn, text = job.location)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // View Details Button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("View Details", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val backgroundColor = when (category) {
        "Central Govt Jobs" -> ColorCentralGovt
        "State Govt Jobs" -> ColorStateGovt
        "Bank Jobs" -> ColorBank
        "PSU Jobs" -> ColorPSU
        "Railway Jobs" -> ColorRailway
        else -> ColorOther
    }.copy(alpha = 0.15f)
    
    val textColor = when (category) {
        "Central Govt Jobs" -> ColorCentralGovt
        "State Govt Jobs" -> ColorStateGovt
        "Bank Jobs" -> ColorBank
        "PSU Jobs" -> ColorPSU
        "Railway Jobs" -> ColorRailway
        else -> ColorOther
    }

    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = category,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PublishDatePill(timestamp: Long) {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = format.format(Date(timestamp))
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(BackgroundGray, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = dateString,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (text.isBlank()) "N/A" else text,
            fontSize = 13.sp,
            color = Color.DarkGray
        )
    }
}
