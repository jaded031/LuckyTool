package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookGlobalFeatureConfig
import com.luckyzyx.luckytool.hook.globals.HookGlobalSystemProperties
import com.luckyzyx.luckytool.hook.scopes.phone.ForceDisplaySIMSomeSwitch
import com.luckyzyx.luckytool.utils.DexkitUtils
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookPhone : YukiBaseHooker() {
    override fun onHook() {
        loadHookerSafe(HookGlobalFeatureConfig)
        loadHookerSafe(HookGlobalSystemProperties)

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //强制显示部分开关
            loadHookerSafe(ForceDisplaySIMSomeSwitch(dexKitBridge))
        }
    }
}