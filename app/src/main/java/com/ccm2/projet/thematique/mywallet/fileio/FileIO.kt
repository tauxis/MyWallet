package com.ccm2.projet.thematique.mywallet.fileio

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class FileIO {

    private fun folderExist(pathname: String){
        val f = File(
            pathname
        )
        Log.d("pathname", f.getAbsolutePath().toString())
        if (!f.exists()){
            f.mkdirs()
            Log.d("Creation dossier", "Dossier cree")
        }
    }

    private fun createTempPNG(tempPicture: Bitmap):String{

        val directory:String = Environment.getDataDirectory().absolutePath.toString() +"/com.ccm2.projet.thematique.mywallet/files/"
        Log.e("directory", directory)
        folderExist(directory)
        Log.e("directory exist", "directory exist")
        val f = File(directory,"tempfile.png")
        Log.e("directory f created", "directory f created")

        Log.e("file path", f.absolutePath.toString())
//        val image = File.createTempFile(
//            "tmpfile",  // prefix
//            ".png",  // suffix
//            f // directory
//        )
        f.createNewFile()
        Log.e("image created ", "image created")
        //val bos = ByteArrayOutputStream()
        tempPicture.compress(Bitmap.CompressFormat.PNG, 0, FileOutputStream(f));
        Log.e("image completed", "image completed")
        /*val bitmapData = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapData)
        fos.flush()
        fos.close()*/

        return directory+"/"+"tmpfile.png"
    }

    private fun exec(
        cmd: String, stdIn: String = "", captureOutput: Boolean = false, workingDir: File = File(
            "."
        )
    ): String? {
        try {
            val process = ProcessBuilder(*cmd.split("\\s".toRegex()).toTypedArray())
                .directory(workingDir)
                .redirectOutput(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
                .redirectError(if (captureOutput) ProcessBuilder.Redirect.PIPE else ProcessBuilder.Redirect.INHERIT)
                .start().apply {
                    if (stdIn != "") {
                        outputStream.bufferedWriter().apply {
                            write(stdIn)
                            flush()
                            close()
                        }
                    }
                    waitFor(60, TimeUnit.SECONDS)
                }
            if (captureOutput) {
                return process.inputStream.bufferedReader().readText()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun postTmpFile(filePath: String): String? {
        val result= exec("curl -F \"file=@" + filePath + "\" https://file.io?expires1w", "", true)
        if (result != null) {Log.e("tag", result)}
        else{Log.e("tag", "Result is null")}
        return result
    }

    fun getLocalLink(tempPicture: Bitmap):String? {
        val tmpFilePath = createTempPNG(tempPicture)
        val returnedResult = postTmpFile(tmpFilePath)
        val parser: Parser = Parser.default()
        val stringBuilder: StringBuilder = StringBuilder(returnedResult)
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject
        val link = json.string("link")

        if (link != null) {
            Log.i("TAG", link)
            return link
        }
        return null
    }
}


