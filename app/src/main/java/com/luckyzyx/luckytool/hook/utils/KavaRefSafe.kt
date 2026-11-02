package com.luckyzyx.luckytool.hook.utils

import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.condition.ConstructorCondition
import com.highcapable.kavaref.condition.FieldCondition
import com.highcapable.kavaref.condition.MethodCondition
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.FieldResolver
import com.highcapable.kavaref.resolver.MethodResolver

/*
 * KavaRef 1.1.x 的 firstXxxOrNull 在未声明 optional() 时找不到成员仍会抛出 NoSuchXxxException，
 * 而 MemberScope.optional() 会改写整个 Scope（后续非 OrNull 查找也变为可选）。
 * 这里提供真正「找不到返回 null」的版本，只吞掉未找到成员的异常，不影响同一 Scope 的其它查找。
 */

fun <T : Any> KavaRef.MemberScope<T>.firstMethodOrNullSafe(
    condition: MethodCondition<T>.() -> Unit
): MethodResolver<T>? = try {
    firstMethodOrNull(condition)
} catch (_: NoSuchMethodException) {
    null
}

fun <T : Any> KavaRef.MemberScope<T>.firstMethodOrNullSafe(
    condition: MethodCondition<T>
): MethodResolver<T>? = try {
    firstMethodOrNull(condition)
} catch (_: NoSuchMethodException) {
    null
}

fun <T : Any> KavaRef.MemberScope<T>.firstFieldOrNullSafe(
    condition: FieldCondition<T>.() -> Unit
): FieldResolver<T>? = try {
    firstFieldOrNull(condition)
} catch (_: NoSuchFieldException) {
    null
}

fun <T : Any> KavaRef.MemberScope<T>.firstConstructorOrNullSafe(
    condition: ConstructorCondition<T>.() -> Unit
): ConstructorResolver<T>? = try {
    firstConstructorOrNull(condition)
} catch (_: NoSuchMethodException) {
    null
}
