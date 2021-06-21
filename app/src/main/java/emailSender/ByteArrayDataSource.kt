package emailSender

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.activation.DataSource

class ByteArrayDataSource(data: ByteArray, type: String?) : DataSource {

        private var data: ByteArray=data
        private var type: String? = type



        fun setType(type: String?) {
            this.type = type
        }


    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(data)
    }

    override fun getOutputStream(): OutputStream {
        throw IOException("Not Supported")
    }

    override fun getContentType(): String {
        return type ?: "application/octet-stream"
    }

    override fun getName(): String {
        return "ByteArrayDataSource"
    }
}