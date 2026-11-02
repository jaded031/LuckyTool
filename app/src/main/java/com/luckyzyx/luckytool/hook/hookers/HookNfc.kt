package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.nfc.EnableNfcAccessCard
import com.luckyzyx.luckytool.hook.scopes.nfc.ScanNfcTagAutoClick
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookNfc : YukiBaseHooker() {
    override fun onHook() {
        //扫描NFC标签自动跳转App
        loadHookerSafe(ScanNfcTagAutoClick)

        //启用NFC门禁卡
        loadHookerSafe(EnableNfcAccessCard)
    }
}