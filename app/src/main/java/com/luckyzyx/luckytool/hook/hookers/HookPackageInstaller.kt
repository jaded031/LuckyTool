package com.luckyzyx.luckytool.hook.hookers

import com.luckyzyx.luckytool.hook.utils.loadHookerSafe
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.DisableStartAppDetail
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.FixInstallButtonDisplayException
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.HookInstallAppProgress
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.HookOPlusUninstallAppProgress
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.HookOplusPackageInstallerActivity
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.HookUninstallerActivity
import com.luckyzyx.luckytool.hook.scopes.packageinstaller.ShowMoreApkPackageInformation
import com.luckyzyx.luckytool.utils.DexkitUtils
import com.luckyzyx.luckytool.utils.ModulePrefs
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object HookPackageInstaller : YukiBaseHooker() {
    override fun onHook() {

        DexkitUtils.create(appInfo.sourceDir) { dexKitBridge ->
            //Source FeatureOption
            dexKitBridge.findClass {
                matcher {
                    usingStrings(
                        "oppo.business.custom",
                        "com.oplus.packageinstaller.skip_appdetail",
                        "com.oplus.unknown_source_app_install"
                    )
                }
            }.singleOrNull() ?: return@create

            //Source OPlusPackageInstallerActivity
            loadHookerSafe(HookOplusPackageInstallerActivity(dexKitBridge))

            //Source InstallAppProgress
            loadHookerSafe(HookInstallAppProgress(dexKitBridge))

            //Source UninstallerActivity
            loadHookerSafe(HookUninstallerActivity(dexKitBridge))

            //Source OPlusUninstallAppProgress
            loadHookerSafe(HookOPlusUninstallAppProgress(dexKitBridge))

            //禁止启动AppDetail
            if (preferences(ModulePrefs).getBoolean("disable_start_app_detail", false)) {
                loadHookerSafe(DisableStartAppDetail(dexKitBridge))
            }
            //修复App安装页面底部按钮异常
            if (preferences(ModulePrefs).getBoolean("fix_install_button_display_exception", false)) {
                loadHookerSafe(FixInstallButtonDisplayException)
            }
            //显示更多Apk包信息
            if (preferences(ModulePrefs).getBoolean("show_more_apk_package_information", false)) {
                loadHookerSafe(ShowMoreApkPackageInformation(dexKitBridge))
            }
        }
    }
}