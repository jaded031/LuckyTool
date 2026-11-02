package com.luckyzyx.luckytool.hook.scopes.systemui

import android.content.Context
import android.graphics.drawable.Icon
import android.provider.Settings
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClassOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.R
import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

/**
 * VoLTE 图标相关
 *
 * Source IconManager addHolder: mBlockList 命中的槽位以 blocked 状态创建，StatusIconContainer 不显示
 * 每个 IconManager 各自带 StatusBarLocation，HOME/KEYGUARD 为状态栏，QS 为通知中心/控制中心顶栏
 *
 * Source OplusStatusBarSignalPolicy imsIcon combine:
 * updateVo5GIcon / updateHDPlusNewIcon / updateVolteIcon 依次决定 volteIcon，最后调用 updateVoWifiIcon 再组装 IMSIcon，
 * 在 updateVoWifiIcon 之前改写 volteIcon 即可。外销韩国等地区没有 VoNR，这里只在 UI 上把 VoLTE 显示为 VoNR，
 * 未注册 IMS 的卡显示带 X 的 SIM 卡图标
 */
@Obfuscate
object VolteIconRelated : YukiBaseHooker() {

    private const val VOLTE_SLOT_RES = "status_bar_volte"

    //自定义图标的占位资源 ID，需为正数（updateSlotIconVisibility 中 <= 0 视为隐藏），且不能与 SystemUI 资源冲突
    private const val MARK_X = 0x7fff4c01
    private const val MARK_1_X = 0x7fff4c02
    private const val MARK_X_2 = 0x7fff4c03
    private const val MARK_X_X = 0x7fff4c04

    private val markIcons = mapOf(
        MARK_X to R.drawable.lt_stat_signal_vonr_x,
        MARK_1_X to R.drawable.lt_stat_signal_vonr_1_x,
        MARK_X_2 to R.drawable.lt_stat_signal_vonr_x_2,
        MARK_X_X to R.drawable.lt_stat_signal_vonr_x_x
    )

    override fun onHook() {
        val hideStatusBar = preferences(ModulePrefs).getBoolean("hide_volte_icon_statusbar", false)
        val hideShade = preferences(ModulePrefs).getBoolean("hide_volte_icon_shade", false)
        val mockVonr = preferences(ModulePrefs).getBoolean("mock_vonr_volte_icon", false)

        if (hideStatusBar || hideShade) {
            //Source IconManager
            "com.android.systemui.statusbar.phone.ui.IconManager".toClassOrNull()?.resolve()
                ?.firstMethodOrNullSafe { name = "addHolder"; parameterCount = 4 }?.hook {
                    before {
                        val slot = args[1] as? String
                        if (slot != volteSlot(appContext)) return@before
                        val location = instance.javaClass.getField("mLocation").get(instance)
                            ?.toString() ?: return@before
                        val hide = when (location) {
                            "HOME", "KEYGUARD" -> hideStatusBar
                            "QS" -> hideShade
                            else -> false
                        }
                        if (hide) arg(2).set(true)
                    }
                }
        }

        if (!mockVonr) return

        //Source OplusStatusBarSignalPolicy
        "com.oplus.systemui.statusbar.phone.signal.OplusStatusBarSignalPolicy".toClassOrNull()
            ?.resolve()?.firstMethodOrNullSafe { name = "access\$updateVoWifiIcon" }?.hook {
                before {
                    val policy = args[0] ?: return@before
                    val sim1 = args[1] ?: return@before
                    val sim2 = args[2] ?: return@before
                    val context = appContext ?: return@before
                    if (Settings.Global.getInt(
                            context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0
                        ) == 1
                    ) return@before
                    val icon = mockIcon(context, sim1, sim2) ?: return@before
                    policy.javaClass.getDeclaredField("volteIcon").apply { isAccessible = true }
                        .setInt(policy, icon)
                }
            }

        //Source StatusBarIconControllerImpl setIcon -> Icon.createWithResource(mContext, resId)
        Icon::class.java.resolve().firstMethodOrNullSafe {
            name = "createWithResource"
            parameters(Context::class, Int::class)
        }?.hook {
            before {
                val res = markIcons[args[1] as? Int] ?: return@before
                result = Icon.createWithResource(moduleAppPackageName(), res)
            }
        }
    }

    private var cachedVolteSlot: String? = null

    private fun volteSlot(context: Context?): String? {
        cachedVolteSlot?.let { return it }
        val res = context?.resources ?: return null
        val id = res.getIdentifier(VOLTE_SLOT_RES, "string", context.packageName)
        if (id == 0) return null
        return res.getString(id).also { cachedVolteSlot = it }
    }

    private fun mockIcon(context: Context, sim1: Any, sim2: Any): Int? {
        val s1 = simState(sim1)
        val s2 = simState(sim2)
        return when {
            s1 == null && s2 == null -> null
            s1 == null || s2 == null -> if (s1 ?: s2!!) sysDrawable(context, "stat_signal_vonr") else MARK_X
            s1 && s2 -> sysDrawable(context, "stat_signal_vonr_both")
            s1 -> MARK_1_X
            s2 -> MARK_X_2
            else -> MARK_X_X
        }
    }

    //null: 未插卡；true: 已注册 IMS；false: 未注册 IMS
    private fun simState(model: Any): Boolean? {
        val cls = model.javaClass
        fun bool(name: String) = cls.getDeclaredField(name).apply { isAccessible = true }
            .getBoolean(model)
        if (!bool("simInserted") || bool("noSim")) return null
        return bool("shouldShowVolte") || bool("shouldShowVo5G")
    }

    private fun sysDrawable(context: Context, name: String): Int? =
        context.resources.getIdentifier(name, "drawable", context.packageName).takeIf { it != 0 }

    private fun moduleAppPackageName() = com.luckyzyx.luckytool.BuildConfig.APPLICATION_ID
}
