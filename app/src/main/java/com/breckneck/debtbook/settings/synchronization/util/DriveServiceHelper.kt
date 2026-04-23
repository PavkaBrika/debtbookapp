package com.breckneck.debtbook.settings.synchronization.util

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Collections
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DriveServiceHelper(driveService: Drive) {

    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mDriveService: Drive = driveService

    fun createFile(name: String): Task<String> {
        val tcs = TaskCompletionSource<String>()
        mExecutor.execute {
            try {
                val metadata = File()
                    .setParents(Collections.singletonList("appDataFolder"))
                    .setMimeType("text/plain")
                    .setName(name)
                val googleFile = mDriveService.files().create(metadata).execute()
                    ?: throw IOException("Null result when requesting file creation.")
                tcs.setResult(googleFile.id)
            } catch (e: Exception) {
                tcs.setException(e)
            }
        }
        return tcs.task
    }

    fun readFile(fileId: String?): Task<Pair<String, String>> {
        val tcs = TaskCompletionSource<Pair<String, String>>()
        mExecutor.execute {
            try {
                val metadata = mDriveService.files()[fileId].execute()
                val name = metadata.name
                val contents = mDriveService.files()[fileId].executeMediaAsInputStream().use { stream ->
                    BufferedReader(InputStreamReader(stream)).use { reader ->
                        reader.readText()
                    }
                }
                tcs.setResult(Pair(name, contents))
            } catch (e: Exception) {
                tcs.setException(e)
            }
        }
        return tcs.task
    }

    fun saveFile(fileId: String?, name: String?, content: String?): Task<Void?> {
        val tcs = TaskCompletionSource<Void?>()
        mExecutor.execute {
            try {
                val metadata = File().setName(name)
                val contentStream = ByteArrayContent.fromString("text/plain", content)
                mDriveService.files().update(fileId, metadata, contentStream).execute()
                tcs.setResult(null)
            } catch (e: Exception) {
                tcs.setException(e)
            }
        }
        return tcs.task
    }

    fun queryFiles(fileName: String): Task<FileList> {
        val tcs = TaskCompletionSource<FileList>()
        mExecutor.execute {
            try {
                val result = mDriveService.files().list()
                    .setSpaces("appDataFolder")
                    .setQ("name = '$fileName'")
                    .execute()
                tcs.setResult(result)
            } catch (e: Exception) {
                tcs.setException(e)
            }
        }
        return tcs.task
    }
}
