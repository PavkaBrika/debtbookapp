package com.breckneck.debtbook.auth.presentation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.breakneck.pokedex.ui.theme.DebtBookTheme
import com.breckneck.debtbook.R
import com.breckneck.debtbook.auth.util.BiometricPromptManager
import com.breckneck.debtbook.auth.util.CryptoManager
import com.breckneck.deptbook.domain.util.PINCodeAction.*
import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.core.activity.MainActivity
import com.breckneck.deptbook.domain.util.CRYPTO_FILE_NAME
import com.breckneck.deptbook.domain.util.PINCodeEnterState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileInputStream

class AuthorizationActivity : AppCompatActivity() {

    private val vm by viewModel<AuthorizationViewModel>()

    val biometricPromptManager = BiometricPromptManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val cryptoManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CryptoManager().also {
                val file = File(filesDir, CRYPTO_FILE_NAME)
                if (!file.exists()) {
                    file.createNewFile()
                } else {
                    if (file.length() > 0)
                        vm.setDecryptedPINCode(it.decrypt(
                            inputStream = FileInputStream(file)
                        ).decodeToString())
                }
            }
        } else {
            null
        }

        val bundle = intent.extras
        var pinCodeAction = CHECK
        if (bundle != null)
            pinCodeAction =
                when (bundle.getString("PINCodeState")) {
                    CHECK.toString() -> CHECK
                    ENABLE.toString() -> ENABLE
                    DISABLE.toString() -> DISABLE
                    CHANGE.toString() -> CHANGE
                    else -> pinCodeAction
                }
        vm.setPINCodeAction(pinCodeAction)

        if (vm.pinCodeAction.value == CHECK) {
            vm.isPINCodeEnabled.observe(this) { isPINCodeEnabled ->
                if (!isPINCodeEnabled)
                    startActivity(Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) })
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.pinCodeEnterState.collect { state ->
                    if (state == PINCodeEnterState.INCORRECT) {
                        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)) {
                            vib.vibrate(VibrationEffect.createOneShot(100, 255))
                        }
                    }
                }
            }
        }

        setContent {
            DebtBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthorizationScreen(activity = this, biometricPromptManager = biometricPromptManager, cryptoManager = cryptoManager)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (vm.isFingerprintAuthEnabled.value && vm.pinCodeAction.value == CHECK)
            biometricPromptManager.showBiometricPrompt(
                title = getString(R.string.login_in_the_debtbook),
                description = getString(R.string.scan_your_fingerprint),
                authenticators = BIOMETRIC_STRONG,
                negativeButtonText = getString(R.string.cancel)
            )
    }
}







