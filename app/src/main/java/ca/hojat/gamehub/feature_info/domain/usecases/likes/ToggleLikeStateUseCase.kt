package ca.hojat.gamehub.feature_info.domain.usecases.likes

import ca.hojat.gamehub.core.domain.common.usecases.UseCase
import ca.hojat.gamehub.core.domain.games.repository.LikedGamesLocalDataSource
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ToggleLikeStateUseCase.Params
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject
import javax.inject.Singleton

interface ToggleLikeStateUseCase : UseCase<Params, Unit> {
    data class Params(val id: Int)
}

@Singleton
@BindType
class ToggleLikeStateUseCaseImpl @Inject constructor(
    private val likedGamesLocalDataSource: LikedGamesLocalDataSource,
) : ToggleLikeStateUseCase {

    override suspend fun execute(params: Params) {
        if (likedGamesLocalDataSource.isGameLiked(params.id)) {
            likedGamesLocalDataSource.unlikeGame(params.id)
        } else {
            likedGamesLocalDataSource.likeGame(params.id)
        }
    }
}
