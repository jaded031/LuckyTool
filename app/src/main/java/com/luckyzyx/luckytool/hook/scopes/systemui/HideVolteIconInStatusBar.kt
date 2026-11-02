package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClassOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import org.lsposed.lsparanoid.Obfuscate

/**
 * 隐藏状态栏 VoLTE 图标（保留通知中心/控制中心顶栏中的显示）
 *
 * Source IconManager addHolder: mBlockList 命中的槽位以 blocked 状态创建，StatusIconContainer 不显示
 * 每个 IconManager 各自带 StatusBarLocation，HOME/KEYGUARD 为状态栏，QS 为通知中心/控制中心顶栏
 */
@Obfuscate
object HideVolteIconInStatusBar : YukiBaseHooker() {

    private const val VOLTE_SLOT_RES = "status_bar_volte"

    private var cachedVolteSlot: String? = null

    override fun onHook() {
        //Source IconManager
        "com.android.systemui.statusbar.phone.ui.IconManager".toClassOrNull()?.resolve()
            ?.firstMethodOrNullSafe { name = "addHolder"; parameterCount = 4 }?.hook {
                before {
                    val slot = args[1] as? String ?: return@before
                    if (slot != volteSlot(appContext)) return@before
                    val location = instance.javaClass.getField("mLocation").get(instance)
                        ?.toString() ?: return@before
                    if (location == "HOME" || location == "KEYGUARD") arg(2).set(true)
                }
            }
    }

    private fun volteSlot(context: Context?): String? {
        cachedVolteSlot?.let { return it }
        val res = context?.resources ?: return null
        val id = res.getIdentifier(VOLTE_SLOT_RES, "string", context.packageName)
        if (id == 0) return null
        return res.getString(id).also { cachedVolteSlot = it }
    }
}
