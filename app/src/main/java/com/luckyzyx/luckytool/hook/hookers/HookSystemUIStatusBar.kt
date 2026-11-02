package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.systemui.CustomMusicFluidCloudWhitelist
import com.luckyzyx.luckytool.hook.scopes.systemui.DisableMediaMusicFluidCloudBlacklist
import com.luckyzyx.luckytool.hook.scopes.systemui.StatusBarIconOrder
import com.luckyzyx.luckytool.hook.statusbar.StatusBarBattery
import com.luckyzyx.luckytool.hook.statusbar.StatusBarClock
import com.luckyzyx.luckytool.hook.statusbar.StatusBarControlCenter
import com.luckyzyx.luckytool.hook.statusbar.StatusBarIcon
import com.luckyzyx.luckytool.hook.statusbar.StatusBarLayout
import com.luckyzyx.luckytool.hook.statusbar.StatusBarNetWorkSpeed
import com.luckyzyx.luckytool.hook.statusbar.StatusBarNotifiyLimit
import com.luckyzyx.luckytool.hook.statusbar.StatusBarNotify
import com.luckyzyx.luckytool.hook.statusbar.StatusBarSilder
import com.luckyzyx.luckytool.hook.statusbar.StatusBarTile
import com.luckyzyx.luckytool.hook.statusbar.StatusBarUI
import com.luckyzyx.luckytool.utils.ModulePrefs
import com.luckyzyx.luckytool.utils.getOSVersionCode
import org.lsposed.lsparanoid.Obfuscate
import org.luckypray.dexkit.DexKitBridge

@Obfuscate
class HookSystemUIStatusBar(val dexKitBridge: DexKitBridge) : YukiBaseHooker() {
    override fun onHook() {
        val osCode = getOSVersionCode

        //状态栏
        loadHookerSafe(StatusBarUI)

        //状态栏时钟
        loadHookerSafe(StatusBarClock())

        //状态栏网速
        loadHookerSafe(StatusBarNetWorkSpeed)

        //状态栏通知
        loadHookerSafe(StatusBarNotify)

        //状态栏通知限制
        loadHookerSafe(StatusBarNotifiyLimit)

        //状态栏图标
        loadHookerSafe(StatusBarIcon)

        //状态栏图标位置排序（始终加载：运行期由 dataChannel 推送新顺序）
        loadHookerSafe(StatusBarIconOrder)

        //状态栏控制中心
        loadHookerSafe(StatusBarControlCenter)

        //状态栏磁贴
        loadHookerSafe(StatusBarTile(dexKitBridge))

        //状态栏滑动条
        loadHookerSafe(StatusBarSilder)

        //状态栏布局
        loadHookerSafe(StatusBarLayout)

        //状态栏电池
        loadHookerSafe(StatusBarBattery)

        //自定义音乐流体云白名单
        if (osCode >= 33) {
            if (preferences(ModulePrefs).getBoolean("custom_music_fluid_cloud_whitelist", false)) {
                loadHookerSafe(CustomMusicFluidCloudWhitelist)
            }
            if (preferences(ModulePrefs).getBoolean("disable_media_music_fluid_cloud_blacklist", false)) {
                if (osCode >= 35) loadHookerSafe(DisableMediaMusicFluidCloudBlacklist)
            }
        }


        //Source OplusToggleSliderView
//        "com.oplus.systemui.qs.widget.OplusToggleSliderView".toClass().apply {
//            method { name = "onShapeChanged" }.hook {
//                after {
//                    val type = firstArg().get<Int>() ?: 0
//                    val mSlider = field { name = "mSlider" }.get(instance).any() ?: return@after
//                    val mThumbColorStateList = mSlider.current().field {
//                        name = "mThumbColorStateList"
//                    }.cast<ColorStateList>() ?: return@after
//                    val newColorStateList = mThumbColorStateList.withAlpha(50)
//                    mSlider.current().method { name = "setThumbColor" }.call(newColorStateList)
//                }
//            }
//        }

        //res/layout/bubble_expanded_view.xml
        //<string name="bubble_close">关闭对话</string>
        //<string name="open_app">进入应用</string>
        //flag_conversations
        //bubble_ic_create_bubble 箭头向右下
        //bubble_ic_stop_bubble 箭头向左上

//        "com.android.wm.shell.bubbles.BubbleController".toClass().apply {
//            method { name = "isResizableActivity" }.hook {
//                replaceToTrue()
//            }
//        }
//
//        "com.android.systemui.statusbar.notification.row.NotificationContentView".toClass().apply {
//            method { name = "shouldShowBubbleButton" }.hook {
//                before {
//                    val entry = firstArg().get() ?: return@before
//                    result = entry.current().method {
//                        name = "getBubbleMetadata";emptyParam()
//                    }.call() != null
//                    YLog.debug("${method.name} -> $result")
//                }
//            }
//        }
//
//        "com.android.systemui.wmshell.BubblesManager".toClass().apply {
//            method { name = "areBubblesEnabled" }.hook {
//                replaceToTrue()
//            }
//        }

    }
}
