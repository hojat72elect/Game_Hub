package ca.on.hojat.gamenews.shared.extensions

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapEither
import com.github.michaelbull.result.mapError
import ca.on.hojat.gamenews.shared.domain.common.DomainException
import ca.on.hojat.gamenews.shared.domain.common.DomainResult
import ca.on.hojat.gamenews.shared.domain.common.entities.Error
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

fun <T> Flow<DomainResult<T>>.onEachSuccess(action: suspend (T) -> Unit): Flow<DomainResult<T>> {
    return onEach { it.onSuccess(action) }
}

fun <T> Flow<DomainResult<T>>.onEachFailure(action: suspend (Error) -> Unit): Flow<DomainResult<T>> {
    return onEach { it.onFailure(action) }
}

fun <T> Flow<DomainResult<T>>.resultOrError(): Flow<T> {
    return map {
        if (it is Ok) return@map it.value
        if (it is Err) throw DomainException(it.error)

        error("The result is neither Ok nor Err.")
    }
}

fun <S1, E1, S2, E2> Flow<Result<S1, E1>>.mapResult(
    success: (S1) -> S2,
    failure: (E1) -> E2
): Flow<Result<S2, E2>> {
    return map { it.mapEither(success, failure) }
}

fun <S1, S2, E1> Flow<Result<S1, E1>>.mapSuccess(
    success: (S1) -> S2
): Flow<Result<S2, E1>> {
    return map { it.map(success) }
}

fun <S1, E1, E2> Flow<Result<S1, E1>>.mapError(
    failure: (E1) -> E2
): Flow<Result<S1, E2>> {
    return map { it.mapError(failure) }
}


data class Tuple4<T1, T2, T3, T4>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4
)

data class Tuple5<T1, T2, T3, T4, T5>(
    val first: T1,
    val second: T2,
    val third: T3,
    val fourth: T4,
    val fifth: T5
)

fun <T1, T2> combine(
    f1: Flow<T1>,
    f2: Flow<T2>
): Flow<Pair<T1, T2>> {
    return kotlinx.coroutines.flow.combine(f1, f2) { t1, t2 ->
        Pair(t1, t2)
    }
}

fun <T1, T2, T3> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>
): Flow<Triple<T1, T2, T3>> {
    return kotlinx.coroutines.flow.combine(f1, f2, f3) { t1, t2, t3 ->
        Triple(t1, t2, t3)
    }
}

fun <T1, T2, T3, T4> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>
): Flow<Tuple4<T1, T2, T3, T4>> {
    return kotlinx.coroutines.flow.combine(f1, f2, f3, f4) { t1, t2, t3, t4 ->
        Tuple4(t1, t2, t3, t4)
    }
}

fun <T1, T2, T3, T4, T5> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>
): Flow<Tuple5<T1, T2, T3, T4, T5>> {
    return kotlinx.coroutines.flow.combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        Tuple5(t1, t2, t3, t4, t5)
    }
}

fun <T> Flow<T>.onSuccess(action: suspend FlowCollector<T>.() -> Unit): Flow<T> {
    return onCompletion { error ->
        if (error == null) action()
    }
}

fun <T> Flow<T>.onError(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T> =
    catch(action)

fun <T> Flow<T>.onEachError(action: (cause: Throwable) -> Unit): Flow<T> {
    return onError {
        action(it)
        throw it
    }
}
