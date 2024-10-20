package ca.hojat.gamehub.core.domain.games.di

import ca.hojat.gamehub.core.domain.games.usecases.ObserveComingSoonGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.ObserveComingSoonGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.ObserveMostAnticipatedGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.ObserveMostAnticipatedGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.ObservePopularGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.ObservePopularGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.ObserveRecentlyReleasedGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.ObserveRecentlyReleasedGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.RefreshComingSoonGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshComingSoonGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.RefreshMostAnticipatedGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshMostAnticipatedGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.RefreshPopularGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshPopularGamesUseCaseImpl
import ca.hojat.gamehub.core.domain.games.usecases.RefreshRecentlyReleasedGamesUseCase
import ca.hojat.gamehub.core.domain.games.usecases.RefreshRecentlyReleasedGamesUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCasesModule {

    @Binds
    fun bindObserveComingSoonGamesUseCase(
        binding: ObserveComingSoonGamesUseCaseImpl
    ): ObserveComingSoonGamesUseCase

    @Binds
    fun bindObserveMostAnticipatedGamesUseCase(
        binding: ObserveMostAnticipatedGamesUseCaseImpl
    ): ObserveMostAnticipatedGamesUseCase

    @Binds
    fun bindObservePopularGamesUseCase(
        binding: ObservePopularGamesUseCaseImpl
    ): ObservePopularGamesUseCase

    @Binds
    fun bindObserveRecentlyReleasedGamesUseCase(
        binding: ObserveRecentlyReleasedGamesUseCaseImpl
    ): ObserveRecentlyReleasedGamesUseCase

    @Binds
    fun bindRefreshComingSoonGamesUseCase(
        binding: RefreshComingSoonGamesUseCaseImpl
    ): RefreshComingSoonGamesUseCase

    @Binds
    fun bindRefreshMostAnticipatedGamesUseCase(
        binding: RefreshMostAnticipatedGamesUseCaseImpl
    ): RefreshMostAnticipatedGamesUseCase

    @Binds
    fun bindRefreshPopularGamesUseCase(
        binding: RefreshPopularGamesUseCaseImpl
    ): RefreshPopularGamesUseCase

    @Binds
    fun bindRefreshRecentlyReleasedGamesUseCase(
        binding: RefreshRecentlyReleasedGamesUseCaseImpl
    ): RefreshRecentlyReleasedGamesUseCase
}
