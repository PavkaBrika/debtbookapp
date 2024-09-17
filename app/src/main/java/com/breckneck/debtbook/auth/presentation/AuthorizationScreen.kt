package com.breckneck.debtbook.auth.presentation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakneck.pokedex.ui.theme.Green
import com.breakneck.pokedex.ui.theme.Red
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.BiometricPromptManager.*
import com.breckneck.deptbook.domain.util.PINCodeAction.*
import com.breckneck.deptbook.domain.util.PINCodeEnterState
import com.breckneck.deptbook.domain.util.PINCodeEnterState.*
import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.core.activity.MainActivity
import com.breckneck.deptbook.domain.util.PINCodeAction
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorizationScreen(
    activity: AppCompatActivity,
    vm: AuthorizationViewModel = koinViewModel()
) {
    val pinCodeEnterState by remember {
        vm.pinCodeEnterState
    }
    val promptManager by lazy {
        BiometricPromptManager(activity)
    }
    val enteredPINCode by remember {
        vm.enteredPINCode
    }
    val enrollLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            println("Activity result: $it")
        }
    )
    LaunchedEffect(enteredPINCode) {
        if ((enteredPINCode.length == 4)) {
            when (vm.pinCodeAction.value) {
                ENABLE -> {
                    if (vm.pinCodeEnterState.value == FIRST) {
                        vm.setPastPINCode()
                        vm.setPINCodeEnterState(CONFIRMATION)
                    } else if ((vm.pinCodeEnterState.value == CONFIRMATION) && (vm.pastPINCode.value == vm.enteredPINCode.value)) {
                        vm.setPINCode()
                        vm.enablePINCode()
                        val intent = Intent()
                        intent.putExtra("PINCodeState", vm.pinCodeAction.value.toString())
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
                            it.putExtra("PINCodeState", vm.pinCodeAction.value.toString())
                            activity.setResult(RESULT_OK, it)
                        }
                        activity.finish()
                    } else {
                        vm.resetPINCodes() //pin codes doesnt match
                        vm.setPINCodeEnterState(INCORRECT)
                    }
                }

                CHANGE -> {
                    if ((vm.pinCodeEnterState.value == FIRST) && (vm.enteredPINCode.value == vm.currentPINCode.value)) {
                        vm.setPastPINCode()
                        vm.setPINCodeEnterState(CONFIRMATION)
                    } else if (vm.pinCodeEnterState.value == CONFIRMATION) {
                        vm.setPINCode()
                        activity.finish()
                    } else {
                        vm.resetPINCodes() //pin codes doesnt match
                        vm.setPINCodeEnterState(INCORRECT)
                    }
                }

                CHECK ->
                    if (vm.enteredPINCode.value == vm.currentPINCode.value)
                        enrollLauncher.launch(Intent(activity, MainActivity::class.java))
                    else {
                        vm.resetPINCodes()
                        vm.setPINCodeEnterState(INCORRECT)
                    }
            }
        }
    }
    UnlockScreen(
        pinCodeEnterState = pinCodeEnterState,
        enteredPINCode = enteredPINCode,
        pinCodeAction = vm.pinCodeAction.value,
        promptManager = promptManager,
        onFingerprintButtonClick = {
            promptManager.showBiometricPrompt(
                title = activity.getString(R.string.login_in_the_debtbook),
                description = activity.getString(R.string.scan_your_fingerprint)
            )
        },
        onDigitButtonClick = { digit ->
            vm.changePINCode(PINCode = enteredPINCode + digit)
            if (pinCodeEnterState == INCORRECT)
                vm.setPINCodeEnterState(FIRST)
        },
        onBackspaceButtonClick = {
            vm.changePINCode(PINCode = enteredPINCode.dropLast(1))
        },
        onCloseAppButtonClick = {
            activity.finishAndRemoveTask()
        },
        showFingerprintButton = vm.isFingerprintAuthEnabled.value
    )
}

@Composable
fun UnlockScreen(
    pinCodeEnterState: PINCodeEnterState,
    enteredPINCode: String,
    pinCodeAction: PINCodeAction,
    promptManager: BiometricPromptManager,
    onFingerprintButtonClick: () -> Unit = {},
    onDigitButtonClick: (String) -> Unit = {},
    onBackspaceButtonClick: () -> Unit = {},
    onCloseAppButtonClick: () -> Unit = {},
    showFingerprintButton: Boolean = true
) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = stringResource(R.string.app_icon)
        )
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = when (pinCodeEnterState) {
                FIRST -> stringResource(R.string.enter_pin)
                CONFIRMATION ->
                    if (pinCodeAction == CHANGE) stringResource(R.string.enter_new_pin)
                    else stringResource(R.string.confirm_your_pin)

                INCORRECT -> stringResource(R.string.pin_code_is_incorrect)
            },
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(28.dp))
        PINCodeSection(
            modifier = Modifier.fillMaxWidth(0.5f),
            PINCode = enteredPINCode,
            pinCodeEnterState = pinCodeEnterState
        )
        if (biometricResult != null) {
            biometricResult?.let { result ->
                Spacer(modifier = Modifier.height(28.dp))
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
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (pinCodeAction == CHECK) {
            Spacer(modifier = Modifier.height(140.dp))
            TextButton(
                modifier = Modifier.offset(y = 24.dp),
                onClick = {
                    onCloseAppButtonClick()
                }
            ) {
                Text(
                    text = "Close app",
                    fontSize = 20.sp,
                    color = Red,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Spacer(modifier = Modifier.height(164.dp))
        }
        ButtonsSection(
            modifier = Modifier
                .fillMaxWidth(),
            onFingerprintButtonClick = onFingerprintButtonClick,
            onDigitButtonClick = onDigitButtonClick,
            onBackspaceButtonClick = onBackspaceButtonClick,
            showFingerprintButton = showFingerprintButton
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
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_circle_24),
                contentDescription = stringResource(R.string.pin),
                tint = if (pinCodeEnterState == INCORRECT) {
                    Color.Red
                } else if (PINCode.length >= i) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color.Gray
                },
                modifier = Modifier.size(32.dp)
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
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
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
                fontSize = 22.sp,
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
fun TopShadow(
    modifier: Modifier = Modifier,
    alpha: Float = 0.1f,
    height: Dp = 8.dp,
    shape: Shape = RectangleShape
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = alpha),
                    )
                )
            )
    )
}

@Composable
fun ButtonsSection(
    modifier: Modifier = Modifier,
    onFingerprintButtonClick: () -> Unit = {},
    onDigitButtonClick: (String) -> Unit = {},
    onBackspaceButtonClick: () -> Unit = {},
    showFingerprintButton: Boolean = true
) {
    TopShadow(
        modifier = Modifier.offset(y = 32.dp),
        height = 40.dp,
        alpha = 0.3f,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    )
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
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
}

@Preview
@Composable
fun UnlockScreenPreview() {
    UnlockScreen(
        pinCodeEnterState = FIRST,
        enteredPINCode = "11",
        pinCodeAction = CHECK,
        promptManager = BiometricPromptManager(AppCompatActivity())
    )
}

@Preview
@Composable
fun ButtonsSectionPreview() {
    ButtonsSection(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
    )
}
