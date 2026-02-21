package fr.wared2003.fulcrumkiosk.domain.usecase

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import fr.wared2003.fulcrumkiosk.DeviceAdminReceiver

class DisableDeviceOwnerUseCase(private val context: Context,
                                private val setDimLockUseCase: SaveIsDimLockEnabledUseCase
) {
    suspend operator fun invoke() {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminName = ComponentName(context, DeviceAdminReceiver::class.java)

        if (dpm.isDeviceOwnerApp(context.packageName)) {

            //On retire les options qui ont besoins du mode device owner
            setDimLockUseCase(false)

            // On annule explicitement chaque restriction avant de perdre les droits
            dpm.clearPackagePersistentPreferredActivities(adminName, context.packageName)
            dpm.setKeyguardDisabled(adminName, false)
            dpm.setStatusBarDisabled(adminName, false)
            dpm.setLockTaskPackages(adminName, arrayOf())

            // On utilise la méthode dépréciée car on est en mode "Admin/Test"
            // mais on sait qu'on a nettoyé avant.
            try {
                dpm.clearDeviceOwnerApp(context.packageName)
            } catch (e: Exception) {
                // Sur les versions très récentes, cela peut échouer
                Log.e("Kiosk", "Échec de clearDeviceOwnerApp: ${e.message}")
            }

            dpm.removeActiveAdmin(adminName)
        }
    }
}