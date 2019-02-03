package com.example.livenessproject.util

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpHelper {

    companion object {
        fun loginWithPassword(username: String, password: String): String {
            val json = "{\"username\":\"$username\", \"password\":\"$password\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/auth/login", "POST", json)
        }

        fun oneToOneComparison(img1base64: String, img2base64: String): String {
            val json = "{\"img1\":\"data:image/jpeg;base64,$img1base64\", \"img2\":\"data:image/jpeg;base64,$img2base64\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/test/compare", "POST", json)
        }

        fun oneToManyComparison(imgbase64: String): String {
            val json = "{\"img\":\"data:image/jpeg;base64,$imgbase64\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/auth/loginface", "POST", json)
        }

        fun faceDetection(imgbase64: String): String {
            val json = "{\"img\":\"data:image/jpeg;base64,$imgbase64\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/test/detect", "POST", json)
        }

        fun listAllUsers(): String {
            return httpRequestWrapper("http://3.0.121.132:3000/users", "GET", null)
        }

        fun createNewUser(loginName : String, password: String, displayName: String, email: String,
                          mobile: String, photoURIBase64: String): String {
            val json = "{\"loginName\" : \"$loginName\",\"password\" : \"$password\",\"displayName\"" +
                    ": \"$displayName\",\"email\" : \"$email\",\"mobile\" : \"$mobile\",\"photoURI\" :" +
                    " \"data:image/jpeg;base64,$photoURIBase64\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/users", "POST", json)
        }

        fun deleteUserById(id: String): String {
            return httpRequestWrapper("http://3.0.121.132:3000/users/$id", "DELETE", null)
        }

        fun getUserById(id: String): String {
            return httpRequestWrapper("http://3.0.121.132:3000/users/$id", "GET", null)
        }

        fun updateUserById(id: String, loginName: String, displayName: String, email: String,
                           mobile: String, photoURIBase64: String): String {
            val json = "{\"loginName\" : \"$loginName\",\"displayName\" : \"$displayName\",\"email\" : \"$email\"," +
                    "\"mobile\" : \"$mobile\",\"photoURI\" : \"data:image/jpeg;base64,$photoURIBase64\"}"
            return httpRequestWrapper("http://3.0.121.132:3000/users/$id", "PATCH", json)
        }

        private fun httpRequestWrapper(url: String, requestMethod: String, json: String?): String{
            var result = ""
            var reader: BufferedReader? = null

            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = requestMethod

                if (requestMethod == "DELETE" || requestMethod == "POST" || requestMethod == "PATCH") {
                    conn.doOutput = true
                    conn.doInput = true
                    conn.useCaches = false
                    conn.setRequestProperty("Connection", "Keep-Alive")
                    conn.setRequestProperty("Charset", "UTF-8")
                    conn.setRequestProperty("Content-Type", "application/json")
                    // Set accept type here, if not will receive Error 415
                    // You can use conn.setRequestProperty("accept","*/*") to accept all types
                    conn.setRequestProperty("accept", "application/json")
                }

                if (requestMethod == "POST" || requestMethod == "PATCH") {
                    val writeBytes = json!!.toByteArray()
                    conn.setRequestProperty("Content-Length", writeBytes.size.toString())
                    val outWriteStream = conn.outputStream
                    outWriteStream.write(json.toByteArray())
                    outWriteStream.flush()
                    outWriteStream.close()
                }

                if (conn.responseCode == 200) {
                    reader = BufferedReader(InputStreamReader(conn.inputStream))
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