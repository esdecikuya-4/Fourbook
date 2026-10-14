package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import com.example.data.remote.FourbookApiClient
import com.example.data.remote.RemoteAppVersionInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class Available(val info: RemoteAppVersionInfo, val isManual: Boolean = false) : UpdateState()
    data class UpToDate(val versionName: String, val isManual: Boolean = false) : UpdateState()
    data class Downloading(val progress: Float, val downloadedMb: String, val totalMb: String) : UpdateState()
    data class ReadyToInstall(val apkFile: File, val info: RemoteAppVersionInfo) : UpdateState()
    data class Error(val message: String, val isManual: Boolean = false) : UpdateState()
}

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main)

    private val downloadClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getCurrentVersionCode(context: Context): Int {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (e: Exception) {
            BuildConfig.VERSION_CODE
        }
    }

    fun getCurrentVersionName(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            BuildConfig.VERSION_NAME
        }
    }

    fun checkForUpdate(context: Context, isManual: Boolean = false) {
        scope.launch {
            if (_updateState.value is UpdateState.Downloading) {
                return@launch
            }
            if (isManual) {
                _updateState.value = UpdateState.Checking
            }

            try {
                val response = withContext(Dispatchers.IO) {
                    FourbookApiClient.service.checkAppVersion()
                }

                if (response.isSuccessful && response.body()?.status == true) {
                    val remoteInfo = response.body()?.data
                    if (remoteInfo != null) {
                        val currentCode = getCurrentVersionCode(context)
                        if (remoteInfo.versionCode > currentCode) {
                            _updateState.value = UpdateState.Available(remoteInfo, isManual)
                        } else {
                            if (isManual) {
                                _updateState.value = UpdateState.UpToDate(getCurrentVersionName(context), isManual)
                            } else {
                                _updateState.value = UpdateState.Idle
                            }
                        }
                    } else {
                        if (isManual) {
                            _updateState.value = UpdateState.Error("Data versi tidak ditemukan di server.", isManual)
                        }
                    }
                } else {
                    if (isManual) {
                        _updateState.value = UpdateState.Error("Gagal memeriksa versi ke server Fourbook.", isManual)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Check update error", e)
                if (isManual) {
                    _updateState.value = UpdateState.Error("Koneksi gagal: ${e.localizedMessage ?: "Server offline"}", isManual)
                }
            }
        }
    }

    fun startDownloadAndInstall(context: Context, info: RemoteAppVersionInfo) {
        scope.launch {
            _updateState.value = UpdateState.Downloading(0f, "0 MB", info.apkSizeMb)

            val apkFile = withContext(Dispatchers.IO) {
                try {
                    val targetDir = File(context.cacheDir, "updates").apply { mkdirs() }
                    val destFile = File(targetDir, "fourbook_v${info.versionName}.apk")

                    val request = Request.Builder()
                        .url(info.apkUrl)
                        .build()

                    val response = downloadClient.newCall(request).execute()
                    if (!response.isSuccessful) {
                        return@withContext null
                    }

                    val body = response.body ?: return@withContext null
                    val totalBytes = body.contentLength()
                    var bytesReadTotal: Long = 0

                    body.byteStream().use { input ->
                        FileOutputStream(destFile).use { output ->
                            val buffer = ByteArray(8192)
                            var read: Int
                            while (input.read(buffer).also { read = it } != -1) {
                                output.write(buffer, 0, read)
                                bytesReadTotal += read
                                if (totalBytes > 0) {
                                    val progress = bytesReadTotal.toFloat() / totalBytes.toFloat()
                                    val downloadedMb = String.format("%.1f MB", bytesReadTotal / (1024f * 1024f))
                                    val totalMb = String.format("%.1f MB", totalBytes / (1024f * 1024f))
                                    withContext(Dispatchers.Main) {
                                        _updateState.value = UpdateState.Downloading(progress, downloadedMb, totalMb)
                                    }
                                }
                            }
                            output.flush()
                        }
                    }
                    destFile
                } catch (e: Exception) {
                    Log.e(TAG, "Download failed", e)
                    null
                }
            }

            if (apkFile != null && apkFile.exists() && apkFile.length() > 0) {
                _updateState.value = UpdateState.ReadyToInstall(apkFile, info)
                installApk(context, apkFile)
            } else {
                _updateState.value = UpdateState.Error("Gagal mengunduh file update APK. Periksa jaringan internet Anda.")
            }
        }
    }

    fun installApk(context: Context, apkFile: File) {
        try {
            // Check Android 8.0+ permission to install packages
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                }
            }

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch installer", e)
            _updateState.value = UpdateState.Error("Gagal membuka installer Android: ${e.message}")
        }
    }

    fun dismiss() {
        _updateState.value = UpdateState.Idle
    }
}
