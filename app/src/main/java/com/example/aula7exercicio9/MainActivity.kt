package com.example.aula7exercicio9

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var btnCapturar: Button
    lateinit var imgFoto: ImageView
    lateinit var txtResultadoQRCode: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialização dos componentes da interface
        btnCapturar = findViewById(R.id.btnCapturar)
        imgFoto = findViewById(R.id.imgFoto)
        txtResultadoQRCode = findViewById(R.id.txtResultado)

        // Configuração do scanner de código de barras
        val scanner = BarcodeScanning.getClient()

        // Registro do contrato para a captura de foto
        val register = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { image: Bitmap? ->
            // Exibir a imagem capturada na ImageView
            imgFoto.setImageBitmap(image)

            // Criar uma imagem a partir do Bitmap para processamento de código de barras
            val bitmap = InputImage.fromBitmap(image!!, 0)

            // Processamento do código de barras
            val result = scanner.process(bitmap)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val valueType = barcode.valueType
                        when (valueType) {
                            Barcode.TYPE_URL -> {
                                // Se for um URL, exibir na TextView
                                val url = barcode.url!!.url
                                txtResultadoQRCode.text = url.toString()
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    // Tratar falha no processamento do código de barras
                    Log.e("======>", it.printStackTrace().toString())
                }

            // Iniciar a animação de fade na ImageView
            imgFoto.animate().alpha(1f).setDuration(1000).start()

            // Exibir um Snackbar com a hora e data da foto tirada
            showSnackbarWithDateTime()
        }

        // Configuração do listener para o botão de capturar foto
        btnCapturar.setOnClickListener {
            // Iniciar a captura de foto
            register.launch(null)
        }
    }

    // Método para exibir um Snackbar com a hora e data da foto tirada
    private fun showSnackbarWithDateTime() {
        val currentDateTime = getCurrentDateTime()
        val snackbarText = "Foto tirada em: $currentDateTime"

        // Exibir o Snackbar
        Toast.makeText(this, snackbarText, Toast.LENGTH_LONG).show()
    }

    // Método para obter a data e hora atuais em um formato específico
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}
