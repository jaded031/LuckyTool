package com.luckyzyx.luckytool.hook.utils

import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * 隔离加载子 Hooker：单个 Hooker 抛出异常（如外版/内版缺少目标类）时只跳过它自身，
 * 不再中断同一 PackageParam 中后续 Hooker 的加载
 * @receiver PackageParam
 * @param hooker YukiBaseHooker
 */
fun PackageParam.loadHookerSafe(hooker: YukiBaseHooker) {
    runCatching { loadHooker(hooker) }.onFailure {
        YLog.error("[$packageName] skip ${hooker.javaClass.name} -> ${it.javaClass.simpleName}: ${it.message}")
    }
}
