package com.breckneck.debtbook.settings.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.breckneck.debtbook.BuildConfig
import com.breckneck.debtbook.R
import com.breckneck.debtbook.settings.viewmodel.SynchronizationViewModel
import com.breckneck.debtbook.settings.util.DriveServiceHelper
import com.breckneck.deptbook.domain.model.AppDataLists
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.FileList
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class SynchronizationFragment : Fragment() {

    interface SynchronizationInterface {
        fun onBackButtonClick()
    }

    private val TAG = "Sync fragment"
    private val REQUEST_CODE_SIGN_IN = 1

    private val vm by viewModel<SynchronizationViewModel>()
    private val fileName = "DebtBookSync.json"
    private var synchronizationInterface: SynchronizationInterface? = null
    private val gson = Gson()
    private lateinit var synchronizationProgressBar: ProgressBar


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

        val backButton: ImageView = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            synchronizationInterface!!.onBackButtonClick()
        }
        val setSettingsButton: FloatingActionButton = view.findViewById(R.id.setFinanceButton)
        setSettingsButton.setOnClickListener {
            synchronizationInterface!!.onBackButtonClick()
        }

        if (vm.isRestoreDialogOpened.value == true)
            openRestoreDataDialog()
        if (vm.isLogOutDialogOpened.value == true)
            openLogOutDialog()

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
        if ((vm.isAuthorized.value == true) && (vm.driveServiceHelper.value == null)) {
            try {
                val googleAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
                onSuccessSignIn(googleAccount = googleAccount!!)
            } catch (e: Exception) {
                vm.setIsAuthorized(false)
            }
        }

        val synchronizationStatusTextView: TextView = view.findViewById(R.id.synchronizationStatusTextView)
        vm.lastSyncDate.observe(viewLifecycleOwner) { date ->
            if (date == "")
                synchronizationStatusTextView.text = getString(R.string.the_data_has_not_yet_been_synchronized)
            else
                synchronizationStatusTextView.text = "${getString(R.string.last_synchronization)} $date"
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
        synchronizationProgressBar = view.findViewById(R.id.synchronizationProgressBar)
        googleButtonCardView.setOnClickListener {
            requestGoogleSignIn()
            synchronizationProgressBar.visibility = View.VISIBLE
        }

        val synchronizeButtonLayout: ConstraintLayout = view.findViewById(R.id.synchronizeButtonLayout)
        synchronizeButtonLayout.setOnClickListener {
            saveFileChanges()
            vm.setIsSynchronizing(true)
        }

        val restoreButtonLayout: ConstraintLayout = view.findViewById(R.id.restoreButtonLayout)
        restoreButtonLayout.setOnClickListener {
            openRestoreDataDialog()
        }

        val restoreTextView: TextView = view.findViewById(R.id.restoreTextView)
        val synchronizeTextView: TextView = view.findViewById(R.id.synchronizeTextView)
        vm.fileId.observe(viewLifecycleOwner) { fileId ->
            if ((fileId == null) || (vm.appDataInfoForSync.value == null)) {
                synchronizeTextView.text = getString(R.string.loading)
                restoreTextView.text = getString(R.string.loading)
                synchronizeButtonLayout.isClickable = false
                restoreButtonLayout.isClickable = false
            } else {
                synchronizeTextView.text = getString(R.string.synchronize)
                restoreTextView.text = getString(R.string.restore_data)
                synchronizeButtonLayout.isClickable = true
                restoreButtonLayout.isClickable = true
            }
        }
        vm.appDataInfoForSync.observe(viewLifecycleOwner) { appData ->
            if ((appData == null) || (vm.fileId.value == null)) {
                synchronizeTextView.text = getString(R.string.loading)
                restoreTextView.text = getString(R.string.loading)
                synchronizeButtonLayout.isClickable = false
                restoreButtonLayout.isClickable = false
            } else {
                synchronizeTextView.text = getString(R.string.synchronize)
                restoreTextView.text = getString(R.string.restore_data)
                synchronizeButtonLayout.isClickable = true
                restoreButtonLayout.isClickable = true
            }
        }

        vm.isSynchronizing.observe(viewLifecycleOwner) { isSynchronizing ->
            if (isSynchronizing) {
                synchronizeTextView.text = getString(R.string.loading)
                synchronizeButtonLayout.isClickable = false
                restoreButtonLayout.isClickable = false
            } else {
                synchronizeTextView.text = getString(R.string.synchronize)
                synchronizeButtonLayout.isClickable = true
                restoreButtonLayout.isClickable = true
            }
        }

        vm.isRestoring.observe(viewLifecycleOwner) { isRestoring ->
            if (isRestoring) {
                restoreTextView.text = getString(R.string.loading)
                synchronizeButtonLayout.isClickable = false
                restoreButtonLayout.isClickable = false
            } else {
                restoreTextView.text = getString(R.string.restore_data)
                synchronizeButtonLayout.isClickable = true
                restoreButtonLayout.isClickable = true
            }
        }

        val googleLogOutButtonLayout: ConstraintLayout =
            view.findViewById(R.id.googleLogoutButtonLayout)
        googleLogOutButtonLayout.setOnClickListener {
            openLogOutDialog()
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
        val clickOnPrivacy = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://simpledebtbook-privacy-policy.ucoz.net/")
                )
                startActivity(browserIntent)
            }
        }
        spannableStringBuilder.setSpan(clickOnPrivacy, startIndex, endIndex, 0)
        privacyAgreementTextView.text = spannableStringBuilder
        privacyAgreementTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun requestGoogleSignIn() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("656437719397-2o429amjok2vdf3d59nliukvv6aeku0p.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA))
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), signInOptions)
        startActivityForResult(client.signInIntent, REQUEST_CODE_SIGN_IN)
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount ->
                onSuccessSignIn(googleAccount = googleAccount)
                vm.setIsAuthorized(isAuthorized = true)
                setFragmentResult("settingsFragmentKey", bundleOf("isAuthorized" to true))
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
                throw it
            }
            .addOnCompleteListener {
                synchronizationProgressBar.visibility = View.GONE
            }
    }

    private fun onSuccessSignIn(googleAccount: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(
            requireActivity(), Collections.singleton(DriveScopes.DRIVE_APPDATA)
        )
        credential.selectedAccount = googleAccount.account
        vm.setUser(name = googleAccount.displayName, email = googleAccount.email)
        val driveService =
            Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                .setApplicationName(BuildConfig.APPLICATION_ID)
                .build()

        vm.setDriveServiceHelper(DriveServiceHelper(driveService))
        vm.driveServiceHelper.value!!.queryFiles()
            .addOnSuccessListener { findOrCreateFile(it) }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
                throw it
            }
    }

    private fun requestGoogleLogOut() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val client = GoogleSignIn.getClient(requireActivity(), signInOptions)

        client.signOut().addOnCompleteListener {
            vm.setIsAuthorized(isAuthorized = false)

        }
    }

    private fun saveFileChanges() {
        vm.driveServiceHelper.value!!.saveFile(
            vm.fileId.value,
            fileName,
            gson.toJson(vm.appDataInfoForSync.value)
        )
            .addOnSuccessListener {
                vm.setIsSynchronizing(false)
                vm.setLastSyncDate()
            }
            .addOnFailureListener {
                vm.setIsSynchronizing(false)
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
                throw it
            }
    }

    private fun getFile() {
        vm.driveServiceHelper.value!!.readFile(vm.fileId.value)
            .addOnSuccessListener {
                vm.replaceAllData(gson.fromJson(it.second, AppDataLists::class.java))
            }
            .addOnFailureListener {
                vm.setIsRestoring(false)
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
                throw it
            }
    }

//    private fun isDataDifferent(data: String): Boolean {
//        val appDataLists = gson.fromJson(data, AppDataLists::class.java)
//        if ((vm.appDataInfoForSync.value!!.humanList.size == appDataLists.humanList.size)
//            && (vm.appDataInfoForSync.value!!.debtList.size == appDataLists.debtList.size)
//            && (vm.appDataInfoForSync.value!!.humanList.containsAll(appDataLists.humanList))
//            && (vm.appDataInfoForSync.value!!.debtList.containsAll(appDataLists.debtList))
//        ) {
//            return true
//        } else {
//            return false
//        }
//    }

    private fun findOrCreateFile(it: FileList) {
        if (it.files.size == 0) {
            createNewFile()
        } else {
            vm.setFileId(it.files.first().id)
        }
    }

    private fun createNewFile() {
        vm.driveServiceHelper.value!!.createFile(fileName)
            .addOnSuccessListener {
                vm.setFileId(it)
                Toast.makeText(
                    requireActivity(),
                    "Created file for chat",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
                throw it
            }
    }

    private fun openRestoreDataDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.dialog_are_you_sure)

        bottomSheetDialog.findViewById<TextView>(R.id.dialogMessage)!!.text = getString(R.string.recover_all_data_from_the_cloud_this_will_delete_the_current_application_data)

        bottomSheetDialog.findViewById<Button>(R.id.okButton)!!.setOnClickListener {
            getFile()
            vm.setIsRestoring(true)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
            vm.setIsRestoreDialogOpened(false)
        }

        bottomSheetDialog.setOnCancelListener {
            vm.setIsRestoreDialogOpened(false)
        }

        vm.setIsRestoreDialogOpened(true)
        bottomSheetDialog.show()
    }

    private fun openLogOutDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.dialog_are_you_sure)

        bottomSheetDialog.findViewById<TextView>(R.id.dialogMessage)!!.text =
            getString(R.string.do_you_really_want_to_log_out)

        bottomSheetDialog.findViewById<Button>(R.id.okButton)!!.setOnClickListener {
            requestGoogleLogOut()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnDismissListener {
            vm.setIsLogOutDialogOpened(false)
        }

        bottomSheetDialog.setOnCancelListener {
            vm.setIsLogOutDialogOpened(false)
        }

        vm.setIsLogOutDialogOpened(true)
        bottomSheetDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> {
                handleSignInResult(data!!)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        setFragmentResult("settingsFragmentKey", bundleOf("isAuthorized" to vm.isAuthorized.value, "isListModified" to vm.isListModified.value))
    }

}