package com.luckyzyx.luckytool.hook.hookers

import android.util.ArrayMap
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookAppFeatureProvider
import com.luckyzyx.luckytool.hook.scopes.aicall.AICallRegionSpoof
import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

/**
 * AI 通话助手（通话摘要 / 通话翻译）外版解锁
 * 作用于 AICallAssistant / InCallUI / Contacts / AccessibilityAssistant(VoiceScribe)
 */
@Obfuscate
object HookAICall : YukiBaseHooker() {
    override fun onHook() {
        val region = preferences(ModulePrefs).getString("ai_call_spoof_region", "0")
        val forceTranslate =
            preferences(ModulePrefs).getBoolean("force_enable_ai_call_translate", false)

        //通话摘要: 区域白名单
        if (region != "0") loadHookerSafe(AICallRegionSpoof(region))

        val features = ArrayMap<String, Any>().apply {
            //Source AppFeature com.oplus.aicall.call_translate 仅部分国家配置开启
            if (forceTranslate) put("com.oplus.aicall.call_translate", true)
            //Source AppFeature oplus.ai.call_summary.disable
            if (region != "0") put("oplus.ai.call_summary.disable", false)
        }
        if (features.isEmpty()) return
        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            loadHookerSafe(HookAppFeatureProvider(dexKitBridge, features))
        }
    }
}
