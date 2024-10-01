package com.breckneck.debtbook.auth.presentation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.breakneck.pokedex.ui.theme.DebtBookTheme
import com.breakneck.pokedex.ui.theme.Red
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.BiometricPromptManager.*
import com.breckneck.debtbook.auth.util.CryptoManager
import com.breckneck.deptbook.domain.util.PINCodeAction.*
import com.breckneck.deptbook.domain.util.PINCodeEnterState
import com.breckneck.deptbook.domain.util.PINCodeEnterState.*
import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.core.activity.MainActivity
import com.breckneck.deptbook.domain.util.CRYPTO_FILE_NAME
import com.breckneck.deptbook.domain.util.PINCodeAction
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun AuthorizationScreen(
    activity: AppCompatActivity,
    vm: AuthorizationViewModel = koinViewModel(),
    biometricPromptManager: BiometricPromptManager,
    cryptoManager: CryptoManager?
) {
    fun setPINCode() {
        val file = File(activity.filesDir, CRYPTO_FILE_NAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cryptoManager?.encrypt(
                bytes = vm.enteredPINCode.value.encodeToByteArray(),
                outputStream = FileOutputStream(file)
            )
        } else {
            vm.setPINCode()
        }
    }
    val pinCodeEnterState by vm.pinCodeEnterState.collectAsState()
    val enteredPINCode by vm.enteredPINCode.collectAsState()

    val startActivityLauncher = rememberLauncherForActivityResult(
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
                        setPINCode()
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
                        setPINCode()
                        activity.finish()
                    } else {
                        vm.resetPINCodes() //pin codes doesnt match
                        vm.setPINCodeEnterState(INCORRECT)
                    }
                }

                CHECK ->
                    if (vm.enteredPINCode.value == vm.currentPINCode.value) {
                        startActivityLauncher.launch(
                            Intent(
                                activity,
                                MainActivity::class.java
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK) })
                        activity.finish()
                    } else {
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
        promptManager = biometricPromptManager,
        onFingerprintButtonClick = {
            biometricPromptManager.showBiometricPrompt(
                title = activity.getString(R.string.login_in_the_debtbook),
                description = activity.getString(R.string.scan_your_fingerprint),
                authenticators = BIOMETRIC_STRONG,
                negativeButtonText = activity.getString(R.string.cancel)
            )
        },
        onDigitButtonClick = { digit ->
            vm.changeEnteredPINCode(PINCode = enteredPINCode + digit)
            if (pinCodeEnterState == INCORRECT)
                vm.setPINCodeEnterState(FIRST)
        },
        onBackspaceButtonClick = {
            vm.changeEnteredPINCode(PINCode = enteredPINCode.dropLast(1))
        },
        onCloseAppButtonClick = {
            activity.finishAndRemoveTask()
        },
        onWriteToDevelopersButtonClick = {
            startActivityLauncher.launch(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:pavlikbrichkin@yandex.ru")
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    "${activity.getString(R.string.email_subject)} ${BuildConfig.VERSION_NAME}"
                )
            })
        },
        onSuperButtonClick = {
            vm.turnOffPINCode()
            Toast.makeText(activity, R.string.your_pin_code_is_reset, Toast.LENGTH_SHORT).show()
            startActivityLauncher.launch(
                Intent(
                    activity,
                    MainActivity::class.java
                ).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK) })
        },
        showFingerprintButton = vm.isFingerprintAuthEnabled.value && vm.pinCodeAction.value == CHECK
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
    onWriteToDevelopersButtonClick: () -> Unit = {},
    onSuperButtonClick: () -> Unit = {},
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
    var showFingerprintButton by remember {
        mutableStateOf(showFingerprintButton)
    }
    LaunchedEffect(biometricResult) {
        if (biometricResult is BiometricResult.AuthenticationNotSet) {
            if (Build.VERSION.SDK_INT >= 30) {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG
                    )
                }
                enrollLauncher.launch(enrollIntent)
            }
        }
    }
    biometricResult?.let { result ->
        when (result) {
            BiometricResult.HardwareUnavailable,
            BiometricResult.FeatureUnavailable,
            BiometricResult.AuthenticationNotSet -> {
                showFingerprintButton = false
            }

            BiometricResult.AuthenticationSuccess -> {
                enrollLauncher.launch(
                    Intent(
                        LocalContext.current,
                        MainActivity::class.java
                    )
                )
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            IconButton(
                onClick = { onWriteToDevelopersButtonClick() },
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .shadow(10.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .border(1.dp, color = MaterialTheme.colorScheme.outline, shape = CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = stringResource(id = R.string.write_email_to_developer),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { contentPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding),
        ) {

            val (appIcon, pinCodeSection, errorText, buttonSection) = createRefs()
            createVerticalChain(
                appIcon,
                pinCodeSection,
                errorText,
                buttonSection,
                chainStyle = ChainStyle.SpreadInside
            )

            Image(
                modifier = Modifier
                    .constrainAs(appIcon) {
                        top.linkTo(parent.top)
                        bottom.linkTo(pinCodeSection.top)
                        centerHorizontallyTo(parent)
                        height = Dimension.fillToConstraints
                    }
                    .size(100.dp)
                    .pointerInput(Unit) {
                        if (pinCodeAction == CHECK) {
                            detectTapGestures(
                                onLongPress = {
                                    onSuperButtonClick()
                                }
                            )
                        }
                    },
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = stringResource(R.string.app_icon)
            )
            Column(
                modifier = Modifier
                    .constrainAs(pinCodeSection) {
                        top.linkTo(appIcon.bottom)
                        bottom.linkTo(errorText.top)
                        centerHorizontallyTo(parent)
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = when (pinCodeEnterState) {
                        FIRST -> stringResource(R.string.enter_pin)
                        CONFIRMATION ->
                            if (pinCodeAction == CHANGE) stringResource(R.string.enter_new_pin)
                            else stringResource(R.string.confirm_your_pin)

                        INCORRECT -> stringResource(R.string.pin_code_is_incorrect)
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                PINCodeSection(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    PINCode = enteredPINCode,
                    pinCodeEnterState = pinCodeEnterState
                )
            }
            Box(
                modifier = Modifier
                    .constrainAs(errorText) {
                        top.linkTo(pinCodeSection.bottom)
                        bottom.linkTo(buttonSection.top)
                        centerHorizontallyTo(parent)
                        centerVerticallyTo(parent)
                        height = Dimension.fillToConstraints
                    }
                    .padding(horizontal = 40.dp)
                    .alpha(
                        if (biometricResult is BiometricResult.AuthenticationError) 1f
                        else 0f
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.something_went_wrong_please_login_with_your_pin_code),
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier
                    .constrainAs(buttonSection) {
                        top.linkTo(errorText.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.wrapContent
                    }
                    .height(IntrinsicSize.Min),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (pinCodeAction == CHECK) {
                    TextButton(
                        modifier = Modifier.offset(y = 24.dp),
                        onClick = {
                            onCloseAppButtonClick()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.exit),
                            fontSize = 16.sp,
                            color = Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                ButtonsSection(
                    onFingerprintButtonClick = onFingerprintButtonClick,
                    onDigitButtonClick = onDigitButtonClick,
                    onBackspaceButtonClick = onBackspaceButtonClick,
                    showFingerprintButton = showFingerprintButton
                )
            }
        }
    }
}

@Composable
fun PINCodeSection(
    modifier: Modifier = Modifier,
    PINCode: String,
    pinCodeEnterState: PINCodeEnterState
) {
    val shake = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = pinCodeEnterState) {
        if (pinCodeEnterState == INCORRECT) {
            for (i in 0..10) {
                when (i % 2) {
                    0 -> shake.animateTo(5f, spring(stiffness = 100_000f))
                    else -> shake.animateTo(-5f, spring(stiffness = 100_000f))
                }
            }
            shake.animateTo(0f)
        }
    }
    Row(
        modifier = modifier
            .offset { IntOffset(x = shake.value.roundToInt(), y = 0) },
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in 1..4) {
            val color = animateColorAsState(
                targetValue = if (pinCodeEnterState == INCORRECT) {
                    Color.Red
                } else if (PINCode.length >= i) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    Color.Gray
                },
                label = "Color",
                animationSpec = tween(
                    durationMillis = 300
                )
            )

            val scale = animateFloatAsState(
                targetValue =
                if (i <= PINCode.length) 1.3f
                else 1f,
                label = "Anim",
                animationSpec = tween(
                    durationMillis = 300
                )
            )

            Icon(
                painter = painterResource(id = R.drawable.baseline_circle_24),
                contentDescription = stringResource(R.string.pin),
                tint = color.value,
                modifier = Modifier
                    .size(32.dp)
                    .scale(scale.value)
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
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
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

@Preview(device = Devices.NEXUS_5)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun UnlockScreenPreview() {
    DebtBookTheme {
        UnlockScreen(
            pinCodeEnterState = FIRST,
            enteredPINCode = "11",
            pinCodeAction = CHECK,
            promptManager = BiometricPromptManager(AppCompatActivity())
        )
    }
}

//@Preview
//@Composable
//fun ButtonsSectionPreview() {
//    ButtonsSection(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.background)
//            .padding(32.dp)
//    )
//}
