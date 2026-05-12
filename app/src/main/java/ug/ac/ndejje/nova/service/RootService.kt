package ug.ac.ndejje.nova.service

import java.io.DataOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootService @Inject constructor() {

    fun execute(command: String): Result<String> {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            
            os.writeBytes("$command\n")
            os.writeBytes("exit\n")
            os.flush()
            
            val exitValue = process.waitFor()
            if (exitValue == 0) {
                val output = process.inputStream.bufferedReader().readText()
                Result.success(output.ifBlank { "Success" })
            } else {
                val error = process.errorStream.bufferedReader().readText()
                Result.failure(Exception(error.ifBlank { "Permission Denied or Root error" }))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isRootAvailable(): Boolean {
        return execute("id").getOrNull()?.contains("uid=0") == true
    }
}
