package ca.hojat.gamehub.feature_info.domain.usecases.game

import ca.hojat.gamehub.core.extensions.combine
import ca.hojat.gamehub.core.extensions.resultOrError
import ca.hojat.gamehub.core.domain.common.DispatcherProvider
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.core.domain.common.usecases.UseCase
import ca.hojat.gamehub.core.domain.entities.Company
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.feature_info.domain.entities.InfoScreenData
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ObserveLikeStateUseCase
import com.paulrybitskyi.hiltbinder.BindType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface GetGameInfoUseCase : UseCase<GetGameInfoUseCase.Params, Flow<InfoScreenData>> {

    data class Params(val gameId: Int)
}

@Singleton
@BindType
class GetGameInfoUseCaseImpl @Inject constructor(
    private val getGameUseCase: GetGameUseCase,
    private val observeLikeStateUseCase: ObserveLikeStateUseCase,
    private val getCompanyDevelopedGamesUseCase: GetCompanyDevelopedGamesUseCase,
    private val getSimilarGamesUseCase: GetSimilarGamesUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : GetGameInfoUseCase {

    private companion object {
        private val RELATED_GAMES_PAGINATION = Pagination()
    }

    override suspend fun execute(params: GetGameInfoUseCase.Params): Flow<InfoScreenData> {
        return getGameUseCase.execute(GetGameUseCase.Params(params.gameId))
            .resultOrError()
            .flatMapConcat { game ->
                combine(
                    flowOf(game),
                    observeGameLikeState(params.gameId),
                    getCompanyGames(game),
                    getSimilarGames(game),
                )
            }
            .map { (game, isGameLiked, companyGames, similarGames) ->
                InfoScreenData(
                    game = game,
                    isGameLiked = isGameLiked,
                    companyGames = companyGames,
                    similarGames = similarGames,
                )
            }
            .flowOn(dispatcherProvider.main)
    }

    private fun observeGameLikeState(gameId: Int): Flow<Boolean> {
        return observeLikeStateUseCase.execute(
            ObserveLikeStateUseCase.Params(gameId)
        )
    }

    private suspend fun getCompanyGames(game: Game): Flow<List<Game>> {
        val company = game.developerCompany
            ?.takeIf(Company::hasDevelopedGames)
            ?: return flowOf(emptyList())

        return getCompanyDevelopedGamesUseCase.execute(
            GetCompanyDevelopedGamesUseCase.Params(company, RELATED_GAMES_PAGINATION),
        )
            .resultOrError()
    }

    private suspend fun getSimilarGames(game: Game): Flow<List<Game>> {
        if (!game.hasSimilarGames) return flowOf(emptyList())

        return getSimilarGamesUseCase.execute(
            GetSimilarGamesUseCase.Params(game, RELATED_GAMES_PAGINATION),
        )
            .resultOrError()
    }
}
