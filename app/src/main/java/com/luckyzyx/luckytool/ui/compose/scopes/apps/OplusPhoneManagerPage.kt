package com.luckyzyx.luckytool.ui.compose.scopes.apps

import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.compose.scopes.ScopePageSpec
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.preferInstalledPackName

/**
 * 旧 ui.fragment.scopes.apps.OplusPhoneManager 的 Compose 等价物（机械翻译 loadPreferences）。
 * 旧类未覆写 isEnableRestartMenu（基类默认 false）。
 */
object OplusPhoneManagerPage {

    private val PACK_NAME = preferInstalledPackName("com.coloros.phonemanager", "com.oplus.phonemanager")

    val spec = ScopePageSpec(
        pageKey = "oplus_phone_manager",
        prefsName = ModulePrefs,
        packName = PACK_NAME,
        scopes = arrayOf(PACK_NAME),
        restartEnabled = false,
    ) {
        val c = requireNotNull(context) { "ScopeScreen 未注入 Context" }
        switch(
            key = "remove_virus_risk_notification_in_phone_manager",
            title = c.getString(R.string.remove_virus_risk_notification_in_phone_manager),
        )
        switch(
            key = "remove_countdown_add_virus_app_whitelist",
            title = c.getString(R.string.remove_countdown_add_virus_app_whitelist),
        )
    }
}
