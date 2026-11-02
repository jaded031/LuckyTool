package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.scopes.safecenter.UnlockStartupLimitOld
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookSafeCenter : YukiBaseHooker() {
    override fun onHook() {
        loadHookerSafe(HookGlobalFeatureConfig)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //移除自启数量限制
            if (SDK < A13 && preferences(ModulePrefs).getBoolean("unlock_startup_limit", false)) {
                loadHookerSafe(UnlockStartupLimitOld(dexKitBridge))
            }
        }
    }
}