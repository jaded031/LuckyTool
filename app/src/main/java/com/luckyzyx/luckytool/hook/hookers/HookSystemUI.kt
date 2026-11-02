package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.HookSystemUIFeature
import com.luckyzyx.luckytool.utils.DexkitUtils
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookSystemUI : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //系统界面Feature
            loadHookerSafe(HookSystemUIFeature(dexKitBridge))

            //状态栏功能
            loadHookerSafe(HookSystemUIStatusBar(dexKitBridge))

            //全面屏手势相关
            loadHookerSafe(HookSystemUIGesture(dexKitBridge))
        }

        //锁屏
        loadHookerSafe(HookSystemUILockScreen)

        //对话框相关
        loadHookerSafe(HookSystemUIDialog)

        //指纹相关
        loadHookerSafe(HookSystemUIFingerPrint)

        //杂项
        loadHookerSafe(HookSystemUiMiscellaneous)

        //自启
        loadHookerSafe(HookSystemUIAutoStart)

    }
}