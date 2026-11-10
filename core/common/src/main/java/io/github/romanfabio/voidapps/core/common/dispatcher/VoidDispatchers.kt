package io.github.romanfabio.voidapps.core.common.dispatcher

import javax.inject.Qualifier

/** Dispatchers that can be injected with [Dispatcher] (docs/ARCHITECTURE.md §8). */
enum class VoidDispatchers {
    IO,
    Default,
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: VoidDispatchers)
