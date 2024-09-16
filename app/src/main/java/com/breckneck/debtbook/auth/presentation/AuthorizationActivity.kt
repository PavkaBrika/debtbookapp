package com.breckneck.debtbook.auth.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.breakneck.pokedex.ui.theme.DebtBookTheme
import com.breckneck.deptbook.domain.util.PINCodeAction.*
import com.breckneck.debtbook.auth.viewmodel.AuthorizationViewModel
import com.breckneck.debtbook.core.activity.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthorizationActivity : AppCompatActivity() {

    private val vm by viewModel<AuthorizationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        if (pinCodeAction == CHECK) {
            vm.isPINCodeEnabled.observe(this) { isPINCodeEnabled ->
                if (!isPINCodeEnabled)
                    startActivity(Intent(this, MainActivity::class.java))
            }
        }


        setContent {
            DebtBookTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthorizationScreen(activity = this)
                }
            }
        }
    }
}







