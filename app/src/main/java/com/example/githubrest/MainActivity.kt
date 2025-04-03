package com.example.githubrest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.githubrest.ui.theme.GithubRestTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubRestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

class LoadViewModel : ViewModel() {

    private val _username = MutableStateFlow("")
    private val _mealResponse = MutableStateFlow(MealResponse(emptyList())) // Mutable StateFlow to hold the click count
    private val _errorMessage = MutableStateFlow("")

    val username: StateFlow<String> = _foodName // Expose a read-only StateFlow
    val errorMessage: StateFlow<String> = _errorMessage
//    val mealResponse: StateFlow<MealResponse> = _mealResponse
//
    fun setUsername(username: String) {
        _username.value = username
    }
//
//    fun requestMeal() {
//        _errorMessage.value = "Loading..." // before anything, we are loading
//        fetchMeal(_foodName.value) { response, error -> // Call fetchForecast
//            if (response != null) { _mealResponse.value = response } // Update forecast response state
//            if (error != null) { _errorMessage.value = error } // Update error message state
//        }
//    }
}

@Composable
fun GithubScreen(viewModel: LoadViewModel = viewModel()) {

    val errorMessage by viewModel.errorMessage.collectAsState()
    val username by viewModel.username.collectAsState()

    Column(
        modifier = Modifier.padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { viewModel.setUsername(it) },
            label = { Text("Enter Username", fontSize = 20.sp) }
        )
        Button(onClick = {
//            if (foodName.isNotEmpty()) {
//                viewModel.requestMeal()
//            }
        }) {
            Text("Get Github Repositroy", fontSize = 20.sp)
        }


        Text("$errorMessage") // Display error message if any
        LazyColumn { // Display forecast items in a LazyColumn
            items(mealResponse!!.list) { item ->
                MealItem(item) // Display each forecast item
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GithubRestTheme {
        GithubScreen("")
    }
}