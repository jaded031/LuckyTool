package com.luckyzyx.luckytool.hook.scopes.systemui

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.extension.kavaref.hook
import com.highcapable.yukihookapi.extension.kavaref.toClassOrNull
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.luckyzyx.luckytool.hook.utils.firstMethodOrNullSafe
import org.lsposed.lsparanoid.Obfuscate

/**
 * 通话流体云胶囊点击展开卡片（国内版行为）
 *
 * Source SystemUIPlugin(com.oplus.systemui.plugins) BaseCapsuleContainerViewModel capsuleAction
 * 外销版 RUS 中 laid_com.android.incallui 的 clickCustomAction=1，
 * handleClick / toggle 均走 clickNormalAction -> 通话胶囊 PendingIntent，直接打开全屏通话界面。
 * 国内版为 0：胶囊态点击 -> EVENT_CAPSULE_LONG_CLICK 展开卡片，卡片态点击 -> EVENT_CAPSULE_CLICK 收起。
 * 这里在 capsuleAction 前将通话胶囊配置的 clickCustomAction 置 0，交由插件原生逻辑处理（含动画中断等状态）。
 *
 * 插件由 PluginInstance$PluginFactory.createClassLoader 以独立 ClassLoader 加载，需在其创建后再 Hook。
 */
@Obfuscate
object InCallCapsuleClickExpandCard : YukiBaseHooker() {

    private const val PLUGIN_PKG = "com.oplus.systemui.plugins"
    private const val INCALLUI_KEY = "|com.android.incallui|"
    private const val VIEW_MODEL = "com.oplus.systemui.plugins.seedling.capsule.ui.viewmodel.u"
    private const val CAPSULE_MODEL = "com.oplus.systemui.plugins.seedling.capsule.data.model.a"
    private const val CAPSULE_CONFIG = "com.oplus.systemui.plugins.seedling.data.config.e"

    private var hookedLoader: ClassLoader? = null

    override fun onHook() {
        //Source PluginInstance$PluginFactory createClassLoader
        "com.android.systemui.shared.plugins.PluginInstance\$PluginFactory".toClassOrNull()
            ?.resolve()?.firstMethodOrNullSafe { name = "createClassLoader"; emptyParameters() }
            ?.hook {
                after {
                    val loader = result as? ClassLoader ?: return@after
                    if (loader === hookedLoader) return@after
                    val appInfo = instance.javaClass.declaredFields
                        .firstOrNull { it.type == android.content.pm.ApplicationInfo::class.java }
                        ?.apply { isAccessible = true }?.get(instance) as? android.content.pm.ApplicationInfo
                    if (appInfo?.packageName != PLUGIN_PKG) return@after
                    runCatching { hookPlugin(loader) }.onSuccess { hookedLoader = loader }
                        .onFailure { YLog.error("InCallCapsuleClickExpandCard -> ${it.message}") }
                }
            }
    }

    private fun hookPlugin(loader: ClassLoader) {
        val viewModel = loader.loadClass(VIEW_MODEL)
        val capsuleModel = loader.loadClass(CAPSULE_MODEL)
        val capsuleConfig = loader.loadClass(CAPSULE_CONFIG)
        //Source BaseCapsuleContainerViewModel capsule list
        val capsuleList = viewModel.getDeclaredField("m").apply { isAccessible = true }
        //Source CapsuleModel key / config
        val capsuleKey = capsuleModel.getDeclaredField("a").apply { isAccessible = true }
        val capsuleCfg = capsuleModel.getDeclaredField("s").apply { isAccessible = true }
        //Source CapsuleConfig clickCustomAction
        val clickCustomAction = capsuleConfig.getDeclaredField("g").apply { isAccessible = true }

        //Source BaseCapsuleContainerViewModel capsuleAction
        viewModel.resolve().firstMethod {
            name = "k"; parameterCount = 1
        }.hook {
            before {
                val list = capsuleList.get(instance) as? List<*> ?: return@before
                list.forEach { capsule ->
                    if (capsule == null || !capsuleModel.isInstance(capsule)) return@forEach
                    val key = capsuleKey.get(capsule) as? String ?: return@forEach
                    if (!key.contains(INCALLUI_KEY)) return@forEach
                    val config = capsuleCfg.get(capsule) ?: return@forEach
                    if (clickCustomAction.getInt(config) != 0) clickCustomAction.setInt(config, 0)
                }
            }
        }
    }
}
