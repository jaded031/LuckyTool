package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.uiengine.EnableRandomTextOnAod
import com.luckyzyx.luckytool.hook.scopes.uiengine.RemoveAodNotificationWhitelist
import com.luckyzyx.luckytool.hook.scopes.uiengine.SetAodNotificationIconStyle
import com.luckyzyx.luckytool.hook.scopes.uiengine.SetAodTypefaceMode
import com.luckyzyx.luckytool.utils.A13
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.SDK
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookUIEngine : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        //移除通知图标白名单
        if (preferences(ModulePrefs).getBoolean("remove_aod_notification_icon_whitelist", false)) {
            if (SDK == A13) loadHookerSafe(RemoveAodNotificationWhitelist)
        }

        //设置息屏样式模式
        if (SDK >= A13) loadHookerSafe(SetAodNotificationIconStyle)

        //启用息屏随机一言
        if (osCode >= 26) loadHookerSafe(EnableRandomTextOnAod)

        //设置息屏字体模式
        if (osCode >= 26) loadHookerSafe(SetAodTypefaceMode)

    }
}