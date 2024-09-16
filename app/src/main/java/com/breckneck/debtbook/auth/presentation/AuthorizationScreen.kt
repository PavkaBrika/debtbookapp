package com.breckneck.debtbook.auth.presentation

import android.app.Activity.RESULT_OK
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.BiometricPromptManager.*
import com.breckneck.deptbook.domain.util.PINCodeAction.*
import com.breckneck.deptbook.domain.util.PINCodeEnterState
import com.breckneck.deptbook.domain.util.PINCodeEnterState.*
import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.core.activity.MainActivity
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorizationScreen(
    activity: AppCompatActivity,
    vm: AuthorizationViewModel = koinViewModel()
) {
    val pinCodeAction = vm.pinCodeAction
    val pinCodeEnterState by remember {
        vm.pinCodeEnterState
    }
    val promptManager by lazy {
        BiometricPromptManager(activity)
    }
    val enteredPINCode by remember {
        vm.enteredPINCode
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
    LaunchedEffect(enteredPINCode) {
        if ((enteredPINCode.length == 4)) {
            when (pinCodeAction.value) {
                ENABLE -> {
                    if (vm.pinCodeEnterState.value == FIRST) {
                        vm.setPastPINCode()
                        vm.setPINCodeEnterState(CONFIRMATION)
                    } else if ((vm.pinCodeEnterState.value == CONFIRMATION) && (vm.pastPINCode.value == vm.enteredPINCode.value)) {
                        vm.setPINCode()
                        vm.enablePINCode()
                        val intent = Intent()
                        intent.putExtra("PINCodeState", pinCodeAction.value.toString())
                        activity.setResult(RESULT_OK, intent)
                        activity.finish()
                    } else {
                        vm.resetPINCodes() //pin codes doesnt match
                        vm.setPINCodeEnterState(INCORRECT)
                    }
                }

                DISABLE -> {
                    if (vm.enteredPINCode.value == vm.currentPINCode.value) {
                        vm.turnOffPINCode()
                        vm.changePINCode("") //pin code doesnt match
                        Intent().also {
                            it.putExtra("PINCodeState", pinCodeAction.value.toString())
                            activity.setResult(RESULT_OK, it)
                        }
                        activity.finish()
                    } else {
                        vm.resetPINCodes() //pin codes doesnt match
                        vm.setPINCodeEnterState(INCORRECT)
                    }
                }

                CHANGE -> {
                    if ((vm.pinCodeEnterState.value == FIRST)  && (vm.enteredPINCode.value == vm.currentPINCode.value)) {
                        vm.setPastPINCode()
                        vm.setPINCodeEnterState(CONFIRMATION)
                    } else if (vm.pinCodeEnterState.value == CONFIRMATION) {
                        vm.setPINCode()
                        activity.finish()
                    }
                    else
                        vm.resetPINCodes() //pin codes doesnt match
                }

                CHECK ->
                    if (vm.enteredPINCode.value == vm.currentPINCode.value)
                        enrollLauncher.launch(Intent(activity, MainActivity::class.java))
                    else {
                        vm.setPINCodeEnterState(INCORRECT)
                        vm.resetPINCodes()
                    }
            }
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
            text = when (pinCodeEnterState) {
                FIRST -> stringResource(R.string.enter_pin)
                CONFIRMATION ->
                    if (pinCodeAction.value == CHANGE) stringResource(R.string.enter_new_pin)
                    else stringResource(R.string.confirm_your_pin)
                INCORRECT -> stringResource(R.string.pin_code_is_incorrect)
            },
            fontSize = 32.sp
        )
        PINCodeSection(PINCode = enteredPINCode, pinCodeEnterState = pinCodeEnterState)
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
        ButtonsSection(
            modifier = Modifier.fillMaxWidth(),
            onFingerprintButtonClick = {
                promptManager.showBiometricPrompt(
                    title = "Sample promt",
                    description = "Sample promt description"
                )
            },
            onDigitButtonClick = { digit ->
                vm.changePINCode(PINCode = enteredPINCode + digit)
                if (pinCodeEnterState == INCORRECT)
                    vm.setPINCodeEnterState(FIRST)
            },
            onBackspaceButtonClick = {
                vm.changePINCode(PINCode = enteredPINCode.dropLast(1))
            }
        )
    }
}

@Composable
fun PINCodeSection(
    modifier: Modifier = Modifier,
    PINCode: String,
    pinCodeEnterState: PINCodeEnterState
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..4) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_circle_24),
                contentDescription = stringResource(R.string.pin),
                tint = if (pinCodeEnterState == INCORRECT) {
                    Color.Red
                } else if (PINCode.length >= i) {
                    Color.Black
                } else {
                    Color.Gray
                }
            )
        }
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
            TextButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                shape = CircleShape,
                onClick = { onDigitButtonClick(numbersList[i].toString()) }
            ) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = numbersList[i].toString(),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
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
    onBackspaceButtonClick: () -> Unit = {},
    showFingerprintButton: Boolean = true
) {
    Row(
        modifier = modifier
            .height(48.dp)
    ) {
        if (showFingerprintButton) {
            IconButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                onClick = onFingerprintButtonClick
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_fingerprint_24),
                    contentDescription = stringResource(R.string.touch_to_show_fingerprint_scanner),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        TextButton(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = CircleShape,
            onClick = {
                onDigitButtonClick("0")
            }
        ) {
            Text(
                text = "0",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            onClick = onBackspaceButtonClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_backspace_24),
                contentDescription = stringResource(R.string.backspace),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onFingerprintButtonClick: () -> Unit = {},
    onDigitButtonClick: (String) -> Unit = {},
    onBackspaceButtonClick: () -> Unit = {},
    showFingerprintButton: Boolean = true
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
            onDigitButtonClick = onDigitButtonClick,
            showFingerprintButton = showFingerprintButton
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
    ButtonsSection(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}
