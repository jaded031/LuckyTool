package com.luckyzyx.luckytool.ui.compose.scopes.apps

import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.ui.compose.scopes.ScopePageSpec
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.checkPackName

/**
 * AI 通话助手页：通话摘要区域伪装 / 通话翻译特性
 */
object OplusAICallPage {

    /** 同时出现在 VoiceScribe call_summary_region 与 call_translate 国家特性中的区域 */
    private val regions = arrayOf("SG", "HK", "TW", "IN", "ID", "MY", "TH", "PK")

    val spec = ScopePageSpec(
        pageKey = "oplus_ai_call",
        prefsName = ModulePrefs,
        packName = "com.oplus.aicall",
        scopes = arrayOf(
            "com.oplus.aicall",
            "com.android.incallui",
            "com.android.contacts",
            "com.coloros.accessibilityassistant"
        ),
        restartEnabled = true,
        isVisible = { checkPackName("com.oplus.aicall") },
    ) {
        val c = requireNotNull(context) { "ScopeScreen 未注入 Context" }
        list(
            key = "ai_call_spoof_region",
            title = c.getString(R.string.ai_call_spoof_region),
            entries = arrayOf(c.getString(R.string.ai_call_spoof_region_off), *regions),
            entryValues = arrayOf("0", *regions),
            default = "0",
            summary = c.getString(R.string.ai_call_spoof_region_summary) + "\n" +
                    c.getString(R.string.current_mode) + ": %s",
        )
        switch(
            key = "force_enable_ai_call_translate",
            title = c.getString(R.string.force_enable_ai_call_translate),
            summary = c.getString(R.string.force_enable_ai_call_translate_summary),
        )
    }
}
