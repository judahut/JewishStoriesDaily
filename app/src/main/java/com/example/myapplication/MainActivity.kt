package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Navigation
import com.example.myapplication.data.network.service
import com.example.myapplication.data.repository.ContentRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = ContentRepository(applicationContext, service)
        val viewModel: AppViewModel by viewModels {
            AppViewModelFactory(repository)
        }

        setContent {
            val navController = rememberNavController()
            Navigation(navController = navController, viewModel = viewModel)
        }
    }
}