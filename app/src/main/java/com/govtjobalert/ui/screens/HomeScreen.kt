package com.govtjobalert.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                // Premium Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Govt Job Alert", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Version 1.0.0", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Categories
                NavigationDrawerItem(
                    label = { Text("All Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "All Jobs",
                    onClick = { jobViewModel.setCategory("All Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Work, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Central Govt Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "Central Govt Jobs",
                    onClick = { jobViewModel.setCategory("Central Govt Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("State Govt Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "State Govt Jobs",
                    onClick = { jobViewModel.setCategory("State Govt Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Bank Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "Bank Jobs",
                    onClick = { jobViewModel.setCategory("Bank Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("PSU Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "PSU Jobs",
                    onClick = { jobViewModel.setCategory("PSU Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Railway Jobs", fontWeight = FontWeight.Medium) },
                    selected = selectedCategory == "Railway Jobs",
                    onClick = { jobViewModel.setCategory("Railway Jobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.DirectionsRailway, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Other Jobs", fontWeight = FontWeight.Medium) },
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
                            Text("Saved Jobs", fontWeight = FontWeight.Medium)
                            if (savedJobsSet.isNotEmpty()) {
                                Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(savedJobsSet.size.toString(), color = Color.White) }
                            }
                        }
                    },
                    selected = false,
                    onClick = { navController.navigate("savedJobs"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("About App", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate("about"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Privacy Policy", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate("privacy"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Policy, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Contact Us", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { navController.navigate("contact"); scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Share App", fontWeight = FontWeight.Medium) },
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
                    label = { Text("Rate App", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                NavigationDrawerItem(
                    label = { Text(if (isAdminLoggedIn) "Admin Dashboard" else "Admin Login", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        if (isAdminLoggedIn) {
                            navController.navigate("adminDashboard")
                        } else {
                            navController.navigate("adminLogin")
                        }
                    },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        GovtJobAlertTheme {
            Scaffold(
                containerColor = BackgroundGray,
                topBar = {
                    TopAppBar(
                        title = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(selectedCategory, color = Color.White, fontWeight = FontWeight.Bold) 
                            }
                        },
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
                ) {
                    // Modern Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        placeholder = { Text("Search jobs by title, post...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    // Latest Jobs Section Title & Counter
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Latest Jobs",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${displayJobs.size} Active Jobs",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
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
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Job Title
            Text(
                text = job.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 24.sp
            )
            
            if (job.postName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = job.postName,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(icon = Icons.Default.School, text = job.qualification)
                InfoItem(icon = Icons.Default.LocationOn, text = job.location)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Premium View Details Button
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("View Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
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
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = category.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
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
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
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
        Box(
            modifier = Modifier.size(28.dp).background(BackgroundGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (text.isBlank()) "N/A" else text,
            fontSize = 14.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
    }
}
