package ca.hojat.gamehub.feature_info.domain.usecases.likes

import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.common.usecases.ObservableUseCase
import ca.hojat.gamehub.core.domain.games.repository.LikedGamesLocalDataSource
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ObserveLikeStateUseCase.Params
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface ObserveLikeStateUseCase : ObservableUseCase<Params, Boolean> {
    data class Params(val id: Int)
}

@Singleton
@BindType
class ObserveLikeStateUseCaseImpl @Inject constructor(
    private val likedGamesLocalDataSource: LikedGamesLocalDataSource,
    private val dispatcherProvider: DispatcherProvider,
) : ObserveLikeStateUseCase {

    override fun execute(params: Params): Flow<Boolean> {
        return likedGamesLocalDataSource.observeGameLikeState(params.id)
            .flowOn(dispatcherProvider.main)
    }
}
