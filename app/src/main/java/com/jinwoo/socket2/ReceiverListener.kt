package com.jinwoo.socket2

import android.annotation.SuppressLint
import android.view.View
import com.example.client_side.MainActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.net.UnknownHostException


@SuppressLint("HandlerLeak")
class ReceiverListener : View.OnClickListener {
    override fun onClick(v: View?) {
        // TODO Auto-generated method stub
        object : Thread() {
            override fun run() {
                try {

                    val HOST = "192.168.1.101"
                    val PORT = 9999
                    // Create Socket instance
                    val socket = Socket(HOST, PORT)
                    // Get input buffer
                    val br = BufferedReader(
                            InputStreamReader(socket.getInputStream()))
//                    MainActivity().line = br.readLine()
                    br.close()
                } catch (e: UnknownHostException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
//                MainActivity().handler.sendEmptyMessage(0)
            }
        }.start()
    }
}