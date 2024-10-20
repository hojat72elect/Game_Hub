package ca.hojat.gamehub.core.domain.games.di

import ca.hojat.gamehub.core.domain.games.common.throttling.GamesRefreshingThrottlerKeyProvider
import ca.hojat.gamehub.core.domain.games.common.throttling.GamesRefreshingThrottlerKeyProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindGamesRefreshingThrottlerKeyProvider(
        binding: GamesRefreshingThrottlerKeyProviderImpl
    ): GamesRefreshingThrottlerKeyProvider
}
