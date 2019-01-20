package com.example.livenessproject.httpUtil

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpHelper {

    companion object {
        fun loginWithPassword_Post(username: String, password: String): String {
            // HttpClient 6.0 is already outdated
            var result = ""
            var reader: BufferedReader? = null

            try {
                val url = URL("http://3.0.121.132:3000/auth/login")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.doInput = true
                conn.useCaches = false
                conn.setRequestProperty("Connection", "Keep-Alive")
                conn.setRequestProperty("Charset", "UTF-8")
                conn.setRequestProperty("Content-Type", "application/json")
                // Set accept type here, if not will receive Error 415
                // You can use conn.setRequestProperty("accept","*/*") to accept all types
                conn.setRequestProperty("accept", "application/json")

                val Json = "{\"username\":\"$username\", \"password\":\"$password\"}"
                val writebytes = Json.toByteArray()
                conn.setRequestProperty("Content-Length", writebytes.size.toString())
                val outwritestream = conn.outputStream
                outwritestream.write(Json.toByteArray())
                outwritestream.flush()
                outwritestream.close()
                Log.d("Login_usrnm&pswd", "doJsonPost: conn" + conn.responseCode)

                if (conn.responseCode == 200) {
                    reader = BufferedReader(
                            InputStreamReader(conn.inputStream))
                    result = reader.readLine()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
            return result
        }
    }

}