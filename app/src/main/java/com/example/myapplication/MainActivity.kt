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
import com.example.myapplication.data.repository.UserPreferencesRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val contentRepository = ContentRepository(applicationContext, service)
        val preferencesRepository = UserPreferencesRepository(applicationContext)
        val viewModel: AppViewModel by viewModels {
            AppViewModelFactory(contentRepository, preferencesRepository)
        }

        setContent {
            val navController = rememberNavController()
            Navigation(navController = navController, viewModel = viewModel)
        }
    }
}