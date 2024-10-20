package ca.hojat.gamehub.feature_info.domain.usecases.game

import ca.hojat.gamehub.core.factories.ImageViewerGameUrlFactory
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.DomainResult
import ca.hojat.gamehub.core.domain.common.usecases.UseCase
import com.github.michaelbull.result.Err
import ca.hojat.gamehub.core.domain.entities.Error
import ca.hojat.gamehub.core.extensions.mapSuccess
import ca.hojat.gamehub.core.extensions.onError
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameImageUrlsUseCase.Params
import ca.hojat.gamehub.feature_info.domain.entities.GameImageType
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

interface GetGameImageUrlsUseCase : UseCase<Params, Flow<DomainResult<List<String>>>> {

    data class Params(
        val gameId: Int,
        val gameImageType: GameImageType,
    )
}

@Singleton
@BindType
class GetGameImageUrlsUseCaseImpl @Inject constructor(
    private val getGameUseCase: GetGameUseCase,
    private val gameUrlFactory: ImageViewerGameUrlFactory,
    private val dispatcherProvider: DispatcherProvider,
) : GetGameImageUrlsUseCase {

    override suspend fun execute(params: Params): Flow<DomainResult<List<String>>> {
        return getGameUseCase.execute(GetGameUseCase.Params(params.gameId))
            .flowOn(dispatcherProvider.main)
            .mapSuccess { game ->
                when (params.gameImageType) {
                    GameImageType.COVER -> {
                        gameUrlFactory.createCoverImageUrl(game)
                            ?.let(::listOf)
                            ?: error("Cannot create a game cover image url.")
                    }
                    GameImageType.ARTWORK -> gameUrlFactory.createArtworkImageUrls(game)
                    GameImageType.SCREENSHOT -> gameUrlFactory.createScreenshotImageUrls(game)
                }
            }
            .onError { error -> emit(Err(Error.Unknown(error.message.orEmpty()))) }
            .flowOn(dispatcherProvider.computation)
    }
}
