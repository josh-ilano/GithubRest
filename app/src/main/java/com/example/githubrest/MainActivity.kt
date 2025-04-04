package com.example.githubrest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.githubrest.ui.theme.GithubRestTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GithubRestTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GithubScreen()
//                }
            }
        }
    }
}

class LoadViewModel : ViewModel() {

    private val _username = MutableStateFlow("")
    private val _gitResponse = MutableStateFlow(listOf(GitResponse("", "", Owner(""))))
    private val _errorMessage = MutableStateFlow("")
    private val _page = MutableStateFlow(1)
    private val _track = MutableStateFlow(true)

    val username: StateFlow<String> = _username // Expose a read-only StateFlow
    val errorMessage: StateFlow<String> = _errorMessage
    val gitResponse: StateFlow<List<GitResponse>> = _gitResponse

    fun setUsername(username: String) {
        if (_username.value != username) {
            _username.value = username
            _track.value = true;
            _page.value = 1;
        }
    }

    fun incrementPage() {
        if(_track.value) {
            _page.value++
            Log.d("LOAD", _page.value.toString())
            requestRepos()
        }
    }

    fun requestRepos() {

        val resetLogin = _username.value != _gitResponse.value[0].owner.login
        val refreshPage = _username.value == _gitResponse.value[0].owner.login &&  _track.value

        if(resetLogin || refreshPage) {
            // the submitted username has to be different from the existing owner
            // OR THE NAME IS THE SAME, AND WE ARE HAVE MORE INFO TO SCROLL DOWN ON

            _errorMessage.value = if (resetLogin) "Loading user info..."  else "Loading more repositories... "
            // can only be one or the other

            fetchRepos(_username.value, _page.value) { response, error -> // Call fetchForecast
                if (response != null) {
                    _track.value = (response.size - _gitResponse.value.size > 0)
                    _gitResponse.value = response; _errorMessage.value = ""
                }
                // Update response state ONLY IF THERE IS SOMETHING TO UPDATE
                if (error != null) { _errorMessage.value = error } // Update error message state
            }
        }


    }
}

@Composable
fun GithubScreen(viewModel: LoadViewModel = viewModel()) {


    val errorMessage by viewModel.errorMessage.collectAsState()
    val username by viewModel.username.collectAsState()
    val gitResponse by viewModel.gitResponse.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

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
            if (username.isNotEmpty()) {
                viewModel.requestRepos()
                coroutineScope.launch { listState.scrollToItem(0) }
            }
        }) {
            Text("Get Github Repository", fontSize = 20.sp)
        }

        Text("$errorMessage") // Display error message if any
        LazyColumn(state = listState) { // Display git repos in a LazyColumn
            items(gitResponse) {
                 item -> GitItem(item) // Display each forecast item
            }
            item { Spacer(Modifier.height(600.dp)) }
        }
    }

    // Observe scroll position
    val firstVisibleItemIndex =  listState.firstVisibleItemIndex

    LaunchedEffect(firstVisibleItemIndex) {
        // You can log or act on scroll position here
        if(firstVisibleItemIndex/gitResponse.size.toDouble() > 0.75) {
            viewModel.incrementPage()
                // we only add another page if we either have one element or are
                // between bounds
        }
    }
}

@Composable
fun GitItem(item: GitResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(item.name, fontSize = 20.sp)
        if (item.description != null && item.description != "") {
            Text("Description: ${item.description}", fontSize = 15.sp)
            Divider()
        }
    }
}

fun fetchRepos(username: String, pageRange: Int, onResult: (List<GitResponse>?, String?) -> Unit) {
    val call = ApiClient.apiService.getRepos(username, 1, 5 * pageRange)
    call.enqueue(object : Callback<List<GitResponse>> {
        override fun onResponse(call: Call<List<GitResponse>>, response: Response<List<GitResponse>>) {
            if (response.isSuccessful) {
                onResult(response.body(), null)
            } else {
                onResult(null, "Error: ${response.code()}")
                Log.e("API_ERROR", "Code: ${response.code()}, message: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<List<GitResponse>>, t: Throwable) {
            onResult(null, "Network Error: ${t.message}")
            Log.e("API_ERROR", "Error: ${t.message}", t)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GithubRestTheme {
        GithubScreen()
    }
}