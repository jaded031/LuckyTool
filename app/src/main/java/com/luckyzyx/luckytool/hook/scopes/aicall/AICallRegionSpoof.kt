package com.luckyzyx.luckytool.hook.scopes.aicall

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hookAll
import com.highcapable.yukihookapi.extension.kavaref.toClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import org.lsposed.lsparanoid.Obfuscate

/**
 * AI 通话区域伪装：仅在当前进程内改写 persist.sys.oplus.region
 *
 * Source AICallAssistant AiSummaryUtils isSupportAiSummaryInRegion
 * Source InCallUI OplusPhoneUtils isSupportAiSummaryInRegion
 * Source Contacts FeatureOpt call_summary_region
 * Source AccessibilityAssistant SettingUtils call_summary_region / 云端区域
 * 均以 VoiceScribe 的 call_summary_region 元数据与 persist.sys.oplus.region 比较
 */
@Obfuscate
class AICallRegionSpoof(private val region: String) : YukiBaseHooker() {
    override fun onHook() {
        arrayOf(
            "android.os.SystemProperties",
            "com.oplus.wrapper.os.SystemProperties",
            "android.os.OplusSystemProperties"
        ).forEach { name ->
            runCatching {
                name.toClass().resolve().method {
                    this.name = "get"
                    returnType = String::class
                }.hookAll {
                    before {
                        if (firstArg().get<String>() == REGION_KEY) result = region
                    }
                }
            }.onFailure { YLog.debug("AICallRegionSpoof [$packageName] skip $name -> ${it.message}") }
        }
    }

    companion object {
        const val REGION_KEY = "persist.sys.oplus.region"
    }
}
