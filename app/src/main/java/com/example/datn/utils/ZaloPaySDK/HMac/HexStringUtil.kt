package com.example.zalopaykotlin.HMac

object HexStringUtil {
    private val HEX_CHAR_TABLE = byteArrayOf(
        '0'.code.toByte(),
        '1'.code.toByte(),
        '2'.code.toByte(),
        '3'.code.toByte(),
        '4'.code.toByte(),
        '5'.code.toByte(),
        '6'.code.toByte(),
        '7'.code.toByte(),
        '8'.code.toByte(),
        '9'.code.toByte(),
        'a'.code.toByte(),
        'b'.code.toByte(),
        'c'.code.toByte(),
        'd'.code.toByte(),
        'e'.code.toByte(),
        'f'.code.toByte()
    )

    /**
     * Convert a byte array to a hexadecimal string
     *
     * @param raw A raw byte array
     * @return Hexadecimal string
     */
    @JvmStatic
    fun byteArrayToHexString(raw: ByteArray): String {
        val hex = ByteArray(2 * raw.size)
        var index = 0
        for (b in raw) {
            val v = b.toInt() and 0xFF
            hex[index++] = HEX_CHAR_TABLE[v ushr 4]
            hex[index++] = HEX_CHAR_TABLE[v and 0xF]
        }
        return String(hex, Charsets.UTF_8)
    }

    /**
     * Convert a hexadecimal string to a byte array
     *
     * @param hex A hexadecimal string
     * @return The byte array
     */
    fun hexStringToByteArray(hex: String): ByteArray {
        val hexstandard = hex.lowercase()
        val sz = hexstandard.length / 2
        val bytesResult = ByteArray(sz)
        var idx = 0
        var i = 0
        while (i < sz) {
            bytesResult[i] = hexstandard[idx].code.toByte()
            idx++
            var tmp = hexstandard[idx].code.toByte()
            idx++
            if (bytesResult[i] > HEX_CHAR_TABLE[9]) {
                bytesResult[i] = (bytesResult[i] - ('a'.code - 10)).toByte()
            } else {
                bytesResult[i] = (bytesResult[i] - '0'.code).toByte()
            }
            if (tmp > HEX_CHAR_TABLE[9]) {
                tmp = (tmp - ('a'.code - 10)).toByte()
            } else {
                tmp = (tmp - '0'.code).toByte()
            }
            bytesResult[i] = (bytesResult[i] * 16 + tmp).toByte()
            i++
        }
        return bytesResult
    }
}