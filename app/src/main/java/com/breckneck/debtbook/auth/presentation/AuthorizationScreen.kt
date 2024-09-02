package com.breckneck.debtbook.auth.presentation

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.BiometricPromptManager.*
import com.breckneck.debtbook.core.activity.MainActivity

@Composable
fun AuthorizationScreen(
    activity: AppCompatActivity,
    settingPIN: Boolean = false
) {
    val promptManager by lazy {
        BiometricPromptManager(activity)
    }
    var PINCode by remember {
        mutableStateOf("")
    }
    val biometricResult by promptManager.promptResults.collectAsState(
        initial = null
    )
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")
        }
    )
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }
    LaunchedEffect(PINCode) {
        if ((PINCode.length == 4)) {
            if (settingPIN)
                enrollLauncher.launch(Intent(activity, MainActivity::class.java))
            else if (PINCode.equals("1111"))
                enrollLauncher.launch(Intent(activity, MainActivity::class.java))
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.baseline_key_24),
            contentDescription = stringResource(R.string.app_icon)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.enter_pin),
            fontSize = 32.sp
        )
        PINCodeSection(PINCode = PINCode)
        biometricResult?.let { result ->
            Text(
                text = when (result) {
                    is BiometricResult.AuthenticationError -> {
                        "Authentication error: ${result.error}"
                    }

                    BiometricResult.AuthenticationFailed -> {
                        "Authentication failed"
                    }

                    BiometricResult.AuthenticationNotSet -> {
                        "Authentication not set"
                    }

                    BiometricResult.AuthenticationSuccess -> {
                        enrollLauncher.launch(
                            Intent(
                                LocalContext.current,
                                MainActivity::class.java
                            )
                        )
                        "Authentication success"
                    }

                    BiometricResult.FeatureUnavailable -> {
                        "Feature unavailable"
                    }

                    BiometricResult.HardwareUnavailable -> {
                        "Hardware unavailable"
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(200.dp))
        ButtonsSection(modifier = Modifier.fillMaxWidth(),
            onFingerprintButtonClick = {
                promptManager.showBiometricPrompt(
                    title = "Sample promt",
                    description = "Sample promt description"
                )
            },
            onDigitButtonClick = { digit ->
                PINCode += digit
            },
            onBackspaceButtonClick = {
                PINCode = PINCode.dropLast(1)
            }
        )
    }
}

@Composable
fun PINCodeSection(
    modifier: Modifier = Modifier,
    PINCode: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_circle_24),
            contentDescription = stringResource(R.string.pin),
            colorFilter = if (PINCode.length >= 1) {
                ColorFilter.tint(Color.Black)
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )
        Image(
            painter = painterResource(id = R.drawable.baseline_circle_24),
            contentDescription = stringResource(R.string.pin),
            colorFilter = if (PINCode.length >= 2) {
                ColorFilter.tint(Color.Black)
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )
        Image(
            painter = painterResource(id = R.drawable.baseline_circle_24),
            contentDescription = stringResource(R.string.pin),
            colorFilter = if (PINCode.length >= 3) {
                ColorFilter.tint(Color.Black)
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )
        Image(
            painter = painterResource(id = R.drawable.baseline_circle_24),
            contentDescription = stringResource(R.string.pin),
            colorFilter = if (PINCode.length >= 4) {
                ColorFilter.tint(Color.Black)
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )
    }

}

@Composable
fun DigitsButtonsRow(
    modifier: Modifier = Modifier,
    numbersList: List<Int>,
    onDigitButtonClick: (String) -> Unit = {}
) {
    Row(
        modifier = modifier
    ) {
        for (i in numbersList.indices) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = CircleShape,
                onClick = { onDigitButtonClick(numbersList[i].toString()) }
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = numbersList[i].toString()
                )
            }
        }
    }
}

@Composable
fun BottomButtonsSection(
    modifier: Modifier = Modifier,
    onFingerprintButtonClick: () -> Unit = {},
    onDigitButtonClick: (String) -> Unit = {},
    onBackspaceButtonClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(48.dp)
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = CircleShape,
            onClick = onFingerprintButtonClick
        ) {
            Image(
                modifier = Modifier.padding(4.dp),
                painter = painterResource(id = R.drawable.baseline_fingerprint_24),
                contentDescription = stringResource(R.string.touch_to_show_fingerprint_scanner),
            )
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = CircleShape,
            onClick = {
                onDigitButtonClick("0")
            }
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = "0"
            )
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = CircleShape,
            onClick = onBackspaceButtonClick
        ) {
            Image(
                modifier = Modifier.padding(4.dp),
                painter = painterResource(id = R.drawable.baseline_backspace_24),
                contentDescription = stringResource(R.string.backspace),
            )
        }
    }
}

@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onFingerprintButtonClick: () -> Unit = {},
    onDigitButtonClick: (String) -> Unit = {},
    onBackspaceButtonClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier
    ) {
        DigitsButtonsRow(
            modifier = Modifier,
            numbersList = listOf(1, 2, 3),
            onDigitButtonClick = onDigitButtonClick
        )
        DigitsButtonsRow(
            modifier = Modifier,
            numbersList = listOf(4, 5, 6),
            onDigitButtonClick = onDigitButtonClick
        )
        DigitsButtonsRow(
            modifier = Modifier,
            numbersList = listOf(7, 8, 9),
            onDigitButtonClick = onDigitButtonClick
        )
        BottomButtonsSection(
            modifier = Modifier,
            onFingerprintButtonClick = onFingerprintButtonClick,
            onBackspaceButtonClick = onBackspaceButtonClick,
            onDigitButtonClick = onDigitButtonClick
        )
    }
}

@Preview
@Composable
fun AuthorizationScreenPreview() {
    AuthorizationScreen(AppCompatActivity())
}

@Preview
@Composable
fun ButtonsSectionPreview() {
    ButtonsSection()
}
