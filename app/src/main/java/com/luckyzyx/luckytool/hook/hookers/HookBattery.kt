package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureProvider
import com.luckyzyx.luckytool.hook.scopes.battery.DisplayModuleCalculatesBatteryHealthData
import com.luckyzyx.luckytool.hook.scopes.battery.HookBatteryNotify
import com.luckyzyx.luckytool.hook.scopes.battery.LauncherHighTempreatureProtection
import com.luckyzyx.luckytool.hook.scopes.battery.RemoveBatteryRestrictPlugin
import com.luckyzyx.luckytool.hook.scopes.battery.RemoveBatteryTemperatureControl
import com.luckyzyx.luckytool.hook.scopes.battery.UnlockStartupLimit
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookBattery : YukiBaseHooker() {
    override fun onHook() {
        loadHookerSafe(HookGlobalFeatureConfig)

        if (SDK < A13) try {
            DexkitUtils.create(appInfo.sourceDir).close()
        } catch (_: UnsatisfiedLinkError) {
            return
        }

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            loadHookerSafe(HookGlobalFeatureProvider(dexKitBridge))
            //电池通知
            loadHookerSafe(HookBatteryNotify(dexKitBridge))
            if (preferences(ModulePrefs).getBoolean("unlock_startup_limit", false)) {
                if (SDK >= A13) loadHookerSafe(UnlockStartupLimit(dexKitBridge))
            }
            //移除电池温度控制
            if (preferences(ModulePrefs).getBoolean("remove_battery_temperature_control", false)) {
                loadHookerSafe(RemoveBatteryTemperatureControl(dexKitBridge))
                loadHookerSafe(LauncherHighTempreatureProtection(dexKitBridge))
            }
            //移除电池限制插件
            if (preferences(ModulePrefs).getBoolean("remove_battery_restrict_plugin", false)) {
                loadHookerSafe(RemoveBatteryRestrictPlugin(dexKitBridge))
            }
        }

        //显示模块计算电池健康数据
        if (preferences(ModulePrefs).getBoolean("open_battery_health", false)) {
            if (SDK >= A13) loadHookerSafe(DisplayModuleCalculatesBatteryHealthData)
        }
    }
}