package com.breckneck.debtbook.presentation.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.set
import androidx.fragment.app.Fragment
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.presentation.viewmodel.SynchronizationFragmentViewModel
import com.breckneck.debtbook.synchronization.DriveServiceHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class SynchronizationFragment: Fragment() {

    interface SynchronizationInterface {
        fun onBackButtonClick()
    }

    private val TAG = "Sync fragment"

    private val vm by viewModel<SynchronizationFragmentViewModel>()

    private val REQUEST_CODE_SIGN_IN = 1
    private val REQUEST_CODE_SIGN_OUT = 201
    private var mDriveServiceHelper: DriveServiceHelper? = null
    private var fileId: String? = null
    private val fileName = "DebtBookSync.txt"
    private var synchronizationInterface: SynchronizationInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        synchronizationInterface = context as SynchronizationInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_synchronization, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: Button = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            synchronizationInterface!!.onBackButtonClick()
        }

        val authorizationLayout: LinearLayout = view.findViewById(R.id.authorizationLayout)
        val accountLayout: LinearLayout = view.findViewById(R.id.accountLayout)
        vm.isAuthorized.observe(viewLifecycleOwner) { isAuthorized ->
            if (isAuthorized) {
                authorizationLayout.visibility = View.GONE
                accountLayout.visibility = View.VISIBLE
            } else {
                authorizationLayout.visibility = View.VISIBLE
                accountLayout.visibility = View.GONE
            }
        }

        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        vm.userName.observe(viewLifecycleOwner) { userName ->
            userNameTextView.text = userName
        }

        val userEmailAddressTextView: TextView = view.findViewById(R.id.userEmailAddressTextView)
        vm.emailAddress.observe(viewLifecycleOwner) { address ->
            userEmailAddressTextView.text = address
        }

        val googleButtonCardView: CardView = view.findViewById(R.id.googleButtonCardView)
        googleButtonCardView.setOnClickListener {
            requestGoogleSignIn()
        }

        val synchronizeButtonLayout: ConstraintLayout = view.findViewById(R.id.synchronizeButtonLayout)
        synchronizeButtonLayout.setOnClickListener {
            requestGoogleLogOut()
        }

        val googleLogOutButtonLayout: ConstraintLayout = view.findViewById(R.id.googleLogoutButtonLayout)
        googleLogOutButtonLayout.setOnClickListener {
            requestGoogleLogOut()
        }

        val privacyAgreementTextView = view.findViewById<TextView>(R.id.privacyAgreementTextView)
        setPrivacyPolicyText(privacyAgreementTextView)
    }

    private fun setPrivacyPolicyText(privacyAgreementTextView: TextView) {
        val privacyPolicy = getString(R.string.privacy_policy_in_agreement)
        val completeString = getString(R.string.by_authorizing_you_agree_to_the, privacyPolicy)
        val startIndex = completeString.indexOf(privacyPolicy)
        val endIndex = completeString.length
        val spannableStringBuilder = SpannableStringBuilder(completeString)
        val clickOnPrivacy = object: ClickableSpan() {
            override fun onClick(p0: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://simpledebtbook-privacy-policy.ucoz.net/"))
                startActivity(browserIntent)
            }
        }
        spannableStringBuilder.setSpan(clickOnPrivacy, startIndex, endIndex, 0)
        privacyAgreementTextView.text = spannableStringBuilder
        privacyAgreementTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun requestGoogleSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), signInOptions)

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun saveFileChanges() {
        Toast.makeText(requireActivity(), "Start save file to Google Drive, pls wait ...", Toast.LENGTH_LONG)
            .show()
        mDriveServiceHelper!!.saveFile(fileId, fileName, "dasds")
            .addOnSuccessListener {
                Toast.makeText(
                    requireActivity(),
                    "Save to google drive successful",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { throw it }
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount ->

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    requireActivity(), Collections.singleton(DriveScopes.DRIVE_APPDATA)
                )
                credential.selectedAccount = googleAccount.account
                vm.setUser(name = googleAccount.displayName, email = googleAccount.email)
                val googleDriveService =
                    Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                        .setApplicationName(BuildConfig.APPLICATION_ID)
                        .build()

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveServiceHelper = DriveServiceHelper(googleDriveService)

                Toast.makeText(requireActivity(), "Sign in successful", Toast.LENGTH_LONG).show()
                mDriveServiceHelper!!.queryFiles()
                    .addOnSuccessListener { findOrCreateFile(it) }
                    .addOnFailureListener { throw it }
                vm.setIsAuthorized(isAuthorized = true)
            }
            .addOnFailureListener { exception -> throw exception }
    }

    private fun requestGoogleLogOut() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), signInOptions)

        client.signOut().addOnCompleteListener {
            vm.setIsAuthorized(isAuthorized = false)
        }
    }

    private fun findOrCreateFile(it: FileList) {
        if (it.files.size == 0) {
            createNewFile()
        } else {
            fileId = it.files.first().id

            Toast.makeText(requireActivity(), "Found file for chat", Toast.LENGTH_LONG).show()
        }
    }

    private fun createNewFile() {
        mDriveServiceHelper!!.createFile(fileName)
            .addOnSuccessListener {
                fileId = it; Toast.makeText(
                requireActivity(),
                "Created file for chat",
                Toast.LENGTH_LONG
            ).show()
            }
            .addOnFailureListener { throw it }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                handleSignInResult(data!!)
            }
        }
    }

}