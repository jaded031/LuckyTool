package com.luckyzyx.luckytool.hook.scopes.externalstorage

import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClass
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object RemoveStorageLimit : YukiBaseHooker() {
    override fun onHook() {
        //Source ExternalStorageProvider
        "com.android.externalstorage.ExternalStorageProvider".toClass().resolve().apply {
            firstMethodOrNullSafe { name = "shouldBlockDirectoryFromTree" }?.hook {
                intercept(false)
            }
        }
    }
}