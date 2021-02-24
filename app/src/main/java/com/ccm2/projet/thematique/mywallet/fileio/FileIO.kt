package com.ccm2.projet.thematique.mywallet.fileio

import android.net.Uri
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpPost
import java.io.File
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

    private fun createTempPNG(tempUri: Uri):String{

//        val directory:String = Environment.getDataDirectory().absolutePath.toString() +"/MyWallet/tmp"
//        Log.e("directory", directory)
//        folderExist(directory)
//        val f = File(directory,"tempfile.png")
//        f.createNewFile()
//        val  bos = ByteArrayOutputStream();
//        tempPicture.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//        val bitmapData = bos.toByteArray();
//
//        val fos = FileOutputStream(f);
//        fos.write(bitmapData);
//        fos.flush();
//        fos.close();
        val uri =File(tempUri.path.toString()).toString();
        Log.w("UriStr",uri)
        return uri
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
                Log.d("readtextred",process.inputStream.bufferedReader().readText())
                return process.inputStream.bufferedReader().readText()
            }
        } catch (e: IOException) {
            Log.d("ICIII",e.toString())
            e.printStackTrace()
        }
        return null
    }

    private fun postTmpFile(filePath: String): String? {
        Log.e("postTmpFile", "Entrée dans posttmpfile()")
        //Log.e("postTmpFile","curl -F \"file=@$filePath\" https://file.io?expires1w")
        val result= exec("curl -F \"file=@$filePath\" https://file.io?expires1w", "", true)
        Log.e("postTmpFile", result.toString())
        return result
    }

    fun getLocalLink(holyUri: Uri):String? {
        val tmpFilePath = createTempPNG(holyUri)
        Log.d("tmpFilePath",tmpFilePath)
        val returnedResult = postTmpFile(tmpFilePath)
        Log.d("return result",returnedResult.toString())
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


