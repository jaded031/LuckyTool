package com.luckyzyx.luckytool.hook.scopes.systemui

import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClass
import com.highcapable.yukihookapi.extension.kavaref.toClassOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveStatusBarBottomNetworkWarn : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode
        if (osCode >= 34) {
            //C17 外版已移除 OplusQSSecurityController
            if ("com.oplus.systemui.qs.policy.OplusQSSecurityController".toClassOrNull() != null) {
                loadHookerSafe(StatusBarBottomNetworkWarn)
            } else loadHookerSafe(StatusBarBottomNetworkWarnV17)
        }
        else loadHookerSafe(StatusBarBottomNetworkWarnOld)
    }

    @Obfuscate
    object StatusBarBottomNetworkWarn : YukiBaseHooker() {
        override fun onHook() {
            var removeMode = preferences(ModulePrefs).getString("remove_control_center_networkwarn", "0")
            dataChannel.wait<String>("remove_control_center_networkwarn") { removeMode = it }

            //Source OplusQSSecurityController
            "com.oplus.systemui.qs.policy.OplusQSSecurityController".toClass().resolve().apply {
                firstMethod { name = "showDeviceMonitoringDialog" }.hook {
                    if (removeMode == "1" || removeMode == "2") intercept()
                }
                (firstMethodOrNullSafe { name = "handleRefreshState" }
                    ?: firstMethod { name { it.contains("handleRefreshState") } }).hook {
                    if (removeMode == "2") intercept()
                }
            }
        }
    }

    @Obfuscate
    object StatusBarBottomNetworkWarnV17 : YukiBaseHooker() {
        override fun onHook() {
            var removeMode = preferences(ModulePrefs).getString("remove_control_center_networkwarn", "0")
            dataChannel.wait<String>("remove_control_center_networkwarn") { removeMode = it }

            //Source OplusQSSecurityViewInteractor
            "com.oplus.systemui.qs.policy.OplusQSSecurityViewInteractor".toClass().resolve().apply {
                firstMethod { name = "showSecurityDialog" }.hook {
                    if (removeMode == "1" || removeMode == "2") intercept()
                }
            }
            //Source SecurityReposityExKt
            "com.android.systemui.security.SecurityReposityExKt".toClass().resolve().apply {
                firstMethod { name = "isSecurityDisclosureVisible" }.hook {
                    if (removeMode == "2") intercept(false)
                }
            }
        }
    }

    @Obfuscate
    object StatusBarBottomNetworkWarnOld : YukiBaseHooker() {
        override fun onHook() {
            var removeMode = preferences(ModulePrefs).getString("remove_control_center_networkwarn", "0")
            dataChannel.wait<String>("remove_control_center_networkwarn") { removeMode = it }

            //Source OplusQSSecurityText
            VariousClass(
                "com.oplusos.systemui.qs.widget.OplusQSSecurityText", //C13
                "com.oplus.systemui.qs.widget.OplusQSSecurityText" //C14
            ).toClass().resolve().apply {
                firstMethod { name = "handleClick" }.hook {
                    if (removeMode == "1" || removeMode == "2") intercept()
                }
                firstMethod { name = "handleRefreshState" }.hook {
                    if (removeMode == "2") intercept()
                }
            }
        }
    }
}