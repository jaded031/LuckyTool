package com.luckyzyx.luckytool.ui.compose.scopes.apps

import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.compose.scopes.ScopePageSpec
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.preferInstalledPackName

/**
 * 旧 ui.fragment.scopes.apps.OplusOShare 的 Compose 等价物（机械翻译 loadPreferences）。
 */
object OplusOSharePage {

    private val PACK_NAME = preferInstalledPackName("com.coloros.oshare", "com.oneplus.oshare")

    val spec = ScopePageSpec(
        pageKey = "oplus_oshare",
        prefsName = ModulePrefs,
        packName = PACK_NAME,
        scopes = arrayOf(PACK_NAME),
        restartEnabled = true,
    ) {
        val c = requireNotNull(context) { "ScopeScreen 未注入 Context" }
        switch(
            key = "remove_oshare_close_countdown",
            title = c.getString(R.string.remove_oshare_close_countdown),
        )
    }
}
