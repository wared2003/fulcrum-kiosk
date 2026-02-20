package fr.wared2003.fulcrumkiosk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.wared2003.fulcrumkiosk.data.local.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                val launchIntent = Intent(context, MainActivity::class.java)
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        }
}
