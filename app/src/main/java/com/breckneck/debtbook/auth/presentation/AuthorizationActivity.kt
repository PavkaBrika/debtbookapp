package com.breckneck.debtbook.auth.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.breakneck.pokedex.ui.theme.DebtBookTheme
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.BiometricPromptManager.*
import com.breckneck.debtbook.core.activity.MainActivity
import com.breckneck.deptbook.domain.usecase.Settings.GetPINCodeEnabled
import org.koin.android.ext.android.inject

class AuthorizationActivity : AppCompatActivity() {

    private val getPINCodeEnabled: GetPINCodeEnabled by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!getPINCodeEnabled.execute())
            startActivity(Intent(this, MainActivity::class.java))

        val bundle = intent.extras
        var settingPIN = false
        if (bundle != null)
            settingPIN = bundle.getBoolean("settingPIN", false)

        setContent {
            DebtBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthorizationScreen(activity = this, settingPIN = settingPIN)
                }
            }
        }
    }
}







