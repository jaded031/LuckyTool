package com.luckyzyx.luckytool.hook.scopes.nfc

import android.os.Binder
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClassOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.globals.HookFeatureConfigManager
import com.luckyzyx.luckytool.hook.globals.HookSystemProperties
import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

/**
 * 启用 NFC 门禁卡（替代 accesscardenabler 模块）
 *
 * Source WirelessSettings NfcAccessCardUtils / Settings NFCUtils isAccessCardRegionSupport
 * Source AccessCard FeatureOption sAccessCardRegionSupport
 * -> OplusFeatureConfigManager.hasFeature("com.oplus.nfc.HceAccesscardRegionControl")
 *
 * Source NfcNci NfcFeatureFetcher isSupportHceAccessCard -> ro.oplus.nfc.hce_accesscard
 *
 * Source NfcNci OplusNfcAdapterService enforceNfcAccessPermission
 * ColorOS 16+ 要求 signature|privileged 权限 com.oplus.permission.safe.NFC，
 * 旧版门禁卡 App 未声明该权限，VendorNfcAdapter.hasFeature / setHceTypeAConfig 等调用全部抛出 SecurityException
 */
@Obfuscate
object EnableNfcAccessCard : YukiBaseHooker() {

    private const val REGION_FEATURE = "com.oplus.nfc.HceAccesscardRegionControl"
    private const val HCE_ACCESSCARD_PROP = "ro.oplus.nfc.hce_accesscard"
    private const val ACCESS_CARD_PKG = "com.oplus.accesscard"

    override fun onHook() {
        if (!preferences(ModulePrefs).getBoolean("enable_nfc_access_card", false)) return
        loadHookerSafe(HookFeatureConfigManager(mapOf(REGION_FEATURE to true)))
        loadHookerSafe(HookSystemProperties(mapOf(HCE_ACCESSCARD_PROP to true)))

        if (packageName != "com.android.nfc") return
        "com.oplus.nfc.OplusNfcAdapterService".toClassOrNull()?.resolve()
            ?.firstMethodOrNullSafe { name = "enforceNfcAccessPermission"; emptyParameters() }
            ?.hook {
                before {
                    val pm = appContext?.packageManager ?: return@before
                    val pkgs = pm.getPackagesForUid(Binder.getCallingUid()) ?: return@before
                    if (ACCESS_CARD_PKG in pkgs) result = null
                }
            }
    }
}
