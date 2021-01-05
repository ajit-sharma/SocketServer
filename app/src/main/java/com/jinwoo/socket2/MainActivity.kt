package com.example.client_side

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.jinwoo.socket2.R
import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var clientThread: ClientThread? = null
    private var thread: Thread? = null
    private var msgList: LinearLayout? = null
    private var handler: Handler? = null
    private var clientTextColor = 0
    private var edMessage: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.server_activity)
        title = "Client"
        clientTextColor = ContextCompat.getColor(this, R.color.green)
        handler = Handler()
        msgList = findViewById(R.id.msgList)
        edMessage = findViewById(R.id.edMessage)
    }

    fun textView(message: String?, color: Int, value: Boolean): TextView {
        var message = message
        if (null == message || message.trim { it <= ' ' }.isEmpty()) {
            message = "<Empty Message>"
        }
        val tv = TextView(this)
        tv.setTextColor(color)
        tv.text = "$message [$time]"
        tv.textSize = 20f
        tv.setPadding(0, 5, 0, 0)
        tv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0F)
        if (value) {
            tv.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        }
        return tv
    }

    fun showMessage(message: String?, color: Int, value: Boolean) {
        handler!!.post { msgList!!.addView(textView(message, color, value)) }
    }

    fun onClick(view: View) {
        if (view.id == R.id.connect_server) {
            msgList!!.removeAllViews()
            clientThread = ClientThread()
            thread = Thread(clientThread)
            thread!!.start()
            return
        }
        if (view.id == R.id.send_data) {
            val clientMessage = edMessage!!.text.toString().trim { it <= ' ' }
            showMessage(clientMessage, Color.BLUE, false)
            if (null != clientThread) {
                if (clientMessage.length > 0) {
                    clientThread!!.sendMessage(clientMessage)
                }
                edMessage!!.setText("")
            }
        }
    }

    /* clientThread class defined to run the client connection to the socket network using the server ip and port
     * and send message */
    internal inner class ClientThread : Runnable {
        private var socket: Socket? = null
        private var input: BufferedReader? = null
        override fun run() {
            try {
                val serverAddr = InetAddress.getByName(SERVER_IP)
                showMessage("Connecting to Server...", clientTextColor, true)
                socket = Socket(serverAddr, SERVER_PORT)
                if (socket!!.isBound) {
                    showMessage("Connected to Server...", clientTextColor, true)
                }
                while (!Thread.currentThread().isInterrupted) {
                    input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                    var message = input!!.readLine()
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted()
                        message = "Server Disconnected..."
                        showMessage(message, Color.RED, false)
                        break
                    }
                    showMessage("Server: $message", clientTextColor, true)
                }
            } catch (e1: UnknownHostException) {
                e1.printStackTrace()
            } catch (e1: IOException) {
                showMessage("Problem Connecting to server... Check your server IP and Port and try again", Color.RED, false)
                Thread.interrupted()
                e1.printStackTrace()
            } catch (e3: NullPointerException) {
                showMessage("error returned", Color.RED, true)
            }
        }

        fun sendMessage(message: String?) {
            Thread {
                try {
                    if (null != socket) {
                        val out = PrintWriter(BufferedWriter(
                                OutputStreamWriter(socket!!.getOutputStream())),
                                true)
                        out.println(message)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    val time: String
        get() {
            val sdf = SimpleDateFormat("HH:mm:ss")
            return sdf.format(Date())
        }


    override fun onDestroy() {
        super.onDestroy()
        if (null != clientThread) {
            clientThread!!.sendMessage("Disconnect")
            clientThread = null
        }
    }

    companion object {
        const val SERVER_PORT = 5050
        const val SERVER_IP = "192.168.43.212"
    }

}