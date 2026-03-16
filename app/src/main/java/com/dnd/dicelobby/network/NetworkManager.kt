package com.dnd.dicelobby.network

import android.content.Context
import android.net.wifi.WifiManager
import java.net.NetworkInterface

/**
 * Utility to retrieve the device's local WiFi/LAN IP address.
 * The DM shares this IP so other players can connect manually.
 */
object NetworkManager {

    /**
     * Returns the device's primary LAN IP address as a dotted-decimal string,
     * or "Unknown" if it cannot be determined.
     */
    fun getLocalIpAddress(context: Context): String {
        // Try WifiManager first (most reliable on Android)
        val wm = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val wifiInfo = wm?.connectionInfo
        val wifiIp = wifiInfo?.ipAddress
        if (wifiIp != null && wifiIp != 0) {
            return formatIp(wifiIp)
        }

        // Fallback: enumerate all network interfaces
        return try {
            NetworkInterface.getNetworkInterfaces()
                ?.asSequence()
                ?.flatMap { it.inetAddresses.asSequence() }
                ?.firstOrNull { !it.isLoopbackAddress && it.hostAddress?.contains(':') == false }
                ?.hostAddress ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /** Convert the integer IP returned by WifiManager to a human-readable string. */
    private fun formatIp(ip: Int): String {
        return "${ip and 0xff}.${ip shr 8 and 0xff}.${ip shr 16 and 0xff}.${ip shr 24 and 0xff}"
    }
}
