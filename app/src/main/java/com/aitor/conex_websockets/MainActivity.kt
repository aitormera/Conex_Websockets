package com.aitor.conex_websockets

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class MainActivity : AppCompatActivity() {

    lateinit var wsclient: MyWebSocketClient

    inner class MyWebSocketClient(serverUri: URI) : WebSocketClient(serverUri) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            println("Connectat al servidor")
            println("Codi d'estat: ${handshakedata?.httpStatus}")
            println("Descripció: ${handshakedata?.httpStatusMessage}")
        }

        override fun onMessage(message: String?) {
            println("Rebut: $message")

            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.append(message + "\n")

                val sv = findViewById<ScrollView>(R.id.scrollView)
                sv.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("Connexió tancada")
            println("Codi: $code, Raó: $reason, Remote: $remote")
        }

        override fun onError(ex: Exception?) {
            println("Error: ${ex?.message}")
            ex?.printStackTrace()
        }
    }

    fun connectaWS() {
        // uso localhost con adb reverse porque con 10.0.2.2 no me funciona
        val uri = URI("ws://localhost:8888")
        wsclient = MyWebSocketClient(uri)
        wsclient.connect()
    }

    fun enviaMissatge(text: String) {
        if (wsclient.isOpen && text != "") {
            wsclient.send(text)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        connectaWS()

        val buttonEnviar = findViewById<Button>(R.id.buttonEnviar)
        val editTextMissatge = findViewById<EditText>(R.id.editTextMissatge)

        buttonEnviar.setOnClickListener {
            val text = editTextMissatge.text.toString()
            enviaMissatge(text)
            editTextMissatge.setText("")
        }
    }
}
