package test.interview.pdfdownload

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import test.interview.pdfdownload.databinding.ActivityMainBinding
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        private const val STORAGE_PERMISSION_CODE = 1000
        private val NEW_DIRECTORY = "interview_downloads"
    }

    private lateinit var activityMainBinding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
                downloadPdf()
            } else {
                Toast.makeText(this@MainActivity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun downloadPdf() {

        CoroutineScope(IO).launch {

            val url = URL("https://www.orimi.com/pdf-test.pdf")
            val fileName = url.path.substring(url.path.lastIndexOf("/") + 1)

            val downloadManagerRequest = DownloadManager.Request(Uri.parse(url.toString()))
            downloadManagerRequest.setTitle(fileName)
            downloadManagerRequest.setMimeType("application/pdf")
            downloadManagerRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
            downloadManagerRequest.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "/${NEW_DIRECTORY}/${fileName}"
            )
            val downloadService = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadService.enqueue(downloadManagerRequest)

            CoroutineScope(Main).launch {
                Toast.makeText(
                    this@MainActivity,
                    "Downloaded in ${Environment.DIRECTORY_DOWNLOADS}/${NEW_DIRECTORY}/${fileName}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.downloadButton.setOnClickListener {
            checkPermission(WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
        }

    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissionLauncher.launch(permission)
        } else {
            downloadPdf()
        }
    }

}