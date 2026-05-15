package com.example.gramakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gramakhata.ui.MainScreen
import com.example.gramakhata.ui.theme.GramaKhataTheme
import com.example.gramakhata.viewmodel.LedgerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GramaKhataTheme {

                // ✅ CREATE VIEWMODEL HERE
                val vm: LedgerViewModel = viewModel()

                // ✅ PASS IT
                MainScreen(vm)

            }
        }
    }
}