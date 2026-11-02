package com.luckyzyx.luckytool.hook.scopes.systemui

import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveLowBatteryDialogWarning : YukiBaseHooker() {
    override fun onHook() {
        //Source OplusPowerNotificationWarnings
        VariousClass(
            "com.oplusos.systemui.notification.power.OplusPowerNotificationWarnings", //C13
            "com.oplus.systemui.statusbar.notification.power.OplusPowerNotificationWarnings" //C14
        ).toClass().resolve().apply {
            firstMethodOrNullSafe { name = "createSavePowerDialog" }?.hook {
                intercept()
            }
            firstMethodOrNullSafe { name = "createSuperSavePowerDialog" }?.hook {
                intercept()
            }
            firstMethodOrNullSafe { name = "showLowBatteryWarning" }?.hook {
                intercept(false)
            }
        }
    }
}