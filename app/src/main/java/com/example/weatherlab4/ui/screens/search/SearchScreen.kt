package com.example.weatherlab4.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherlab4.R
import com.example.weatherlab4.ui.navigation.Routes
import com.example.weatherlab4.ui.screens.home.HomeViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    nav: NavController,
    vm: SearchViewModel = hiltViewModel(),
    homeVm: HomeViewModel = hiltViewModel(nav.getBackStackEntry(Routes.HOME))
) {
    val st by vm.state.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.search_title)) },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.search_back))
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = st.query,
                onValueChange = { vm.onQuery(it) },
                label = { Text(stringResource(R.string.search_city_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Text(stringResource(R.string.search_suggestions), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            st.suggestions.forEach { s ->
                ElevatedCard(
                    onClick = {
                        vm.commitQuery(st.query)
                        homeVm.setManualLocation(s.lat, s.lon)

                        nav.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(Modifier.padding(14.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(s.title, style = MaterialTheme.typography.titleMedium)
                            Text("${s.lat}, ${s.lon}", style = MaterialTheme.typography.bodyMedium)
                        }
                        TextButton(onClick = { vm.saveToFav(s) }) { Text(stringResource(R.string.search_add_to_favorites)) }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.search_history), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            st.history.forEach { h -> Text("• $h") }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { vm.clearHistory() }) { Text(stringResource(R.string.search_clear_history)) }

            Spacer(Modifier.height(32.dp))
        }
    }
}