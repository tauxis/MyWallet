package com.ccm2.projet.thematique.mywallet.qrcodeactivity

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccm2.projet.thematique.mywallet.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_qr_code.*
import java.io.FileInputStream

class QRCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        val content: String? = intent.getStringExtra("INTENT")

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content.toString(), BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        imageView.setImageBitmap(bitmap)
    }
}