package com.newestaf.fabricator.util

import com.google.gson.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


object ItemStackCoder {
    @Throws(IllegalStateException::class)
    fun itemTo64(stack: ItemStack?): String {
        try {
            if (stack == null) {
                return "EMPTY"
            }
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            dataOutput.writeObject(stack)

            // Serialize that array
            dataOutput.close()
            return Base64Coder.encodeLines(outputStream.toByteArray())
        }
        catch (e: Exception) {
            throw IllegalStateException("Unable to save item stack.", e)
        }
    }

    @Throws(IOException::class)
    fun itemFrom64(data: String): ItemStack? {
        try {
            if (data == "EMPTY") {
                return null
            }
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            dataInput.use { dataObject ->
                return dataObject.readObject() as ItemStack
            }
        }
        catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }
}
