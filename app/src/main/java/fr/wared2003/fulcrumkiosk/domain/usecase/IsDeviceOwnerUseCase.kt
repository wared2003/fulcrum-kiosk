package fr.wared2003.fulcrumkiosk.domain.usecase

import android.app.admin.DevicePolicyManager
import android.content.Context

class IsDeviceOwnerUseCase(private val context: Context) {
    operator fun invoke(): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }
}
