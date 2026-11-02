package com.luckyzyx.luckytool.hook.statusbar

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.StatusBarBatteryInfoNotify
import com.luckyzyx.luckytool.hook.scopes.systemui.StatusBarBatteryView
import com.luckyzyx.luckytool.utils.A12
import com.luckyzyx.luckytool.utils.SDK
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object StatusBarBattery : YukiBaseHooker() {
    override fun onHook() {
        //电池图标
        loadHookerSafe(StatusBarBatteryView)

        //电池信息通知
        if (SDK >= A12) loadHookerSafe(StatusBarBatteryInfoNotify)
    }
}