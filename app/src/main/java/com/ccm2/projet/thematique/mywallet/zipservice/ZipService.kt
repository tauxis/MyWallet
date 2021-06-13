package com.ccm2.projet.thematique.mywallet.zipservice

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.ccm2.projet.thematique.mywallet.storage.StorageItem
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


open class ZipService {
    private val BUFFER_SIZE =8192
    val path: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    private val dataPath = "/MyWallet/data"
    private val filename = "Fichiers_MyWallet.zip"
    private val mFolder = File(path.absolutePath + dataPath)

    fun zipFilesSelected(selectedItems: ArrayList<StorageItem>):String{
        if (path.exists()) {
            mFolder.mkdirs()
        }
        val zipFile = File(mFolder.absolutePath, filename)
        try {
            val fileOutputStream = FileOutputStream(zipFile);
            val zipOutputStream = ZipOutputStream(BufferedOutputStream(fileOutputStream));
            zipOutputStream.setLevel(9)

            selectedItems.forEachIndexed { index, item ->
                Log.d("FILE+$index", item.itemUrl)
                zip(zipOutputStream, item.itemUrl, item.itemName)
            }
            zipOutputStream.flush()
            fileOutputStream.flush()
            zipOutputStream.close()
            fileOutputStream.close()
        }
        catch (e: FileNotFoundException){
            Log.e("CREATE FILE", e.toString())
        }

        Log.d("ZIPFILE", zipFile.toString())
        return zipFile.toString()
    }

    open fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun cleanZip(){
        mFolder.deleteRecursively()
    }

    private fun zip(
        zipOutputStream: ZipOutputStream,
        fileUrl: String,
        itemName: String
    ) {
        val input: BufferedInputStream
        val data = ByteArray(BUFFER_SIZE)
        val stream = ByteArrayOutputStream()
        getBitmapFromURL(fileUrl)?.compress(CompressFormat.JPEG, 100, stream)

        val inputStream = ByteArrayInputStream(stream.toByteArray())
        input = BufferedInputStream(inputStream, BUFFER_SIZE)

        val entry = ZipEntry("$itemName.jpg")
        zipOutputStream.putNextEntry(entry)

        var length: Int
        while (input.read(data).also { length = it } > 0) {
            zipOutputStream.write(data, 0, length)
        }

        input.close()
        zipOutputStream.closeEntry()
    }
}