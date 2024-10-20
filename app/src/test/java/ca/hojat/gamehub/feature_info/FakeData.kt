package ca.hojat.gamehub.feature_info

import ca.hojat.gamehub.core.domain.entities.Company
import ca.hojat.gamehub.core.domain.entities.InvolvedCompany
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAME
import ca.hojat.gamehub.core.common_testing.domain.DOMAIN_GAMES
import ca.hojat.gamehub.core.common_testing.domain.PAGINATION
import ca.hojat.gamehub.feature_info.domain.usecases.game.RefreshSimilarGamesUseCase
import ca.hojat.gamehub.feature_info.domain.entities.InfoScreenData
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetCompanyDevelopedGamesUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetSimilarGamesUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.RefreshCompanyDevelopedGamesUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ObserveLikeStateUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ToggleLikeStateUseCase

private val COMPANY = Company(
    id = 1,
    name = "name",
    websiteUrl = "url",
    logo = null,
    developedGames = listOf(1, 2, 3),
)

val GAME_INFO = InfoScreenData(
    game = DOMAIN_GAME,
    isGameLiked = true,
    companyGames = DOMAIN_GAMES,
    similarGames = DOMAIN_GAMES,
)
val INVOLVED_COMPANY = InvolvedCompany(
    company = COMPANY,
    isDeveloper = false,
    isPublisher = false,
    isPorter = false,
    isSupporting = false,
)

val OBSERVE_GAME_LIKE_STATE_USE_CASE_PARAMS =
    ObserveLikeStateUseCase.Params(id = 10)
val TOGGLE_GAME_LIKE_STATE_USE_CASE_PARAMS = ToggleLikeStateUseCase.Params(id = 10)
val GET_GAME_USE_CASE_PARAMS = GetGameUseCase.Params(gameId = 10)
val GET_COMPANY_DEVELOPED_GAMES_USE_CASE_PARAMS = GetCompanyDevelopedGamesUseCase.Params(
    COMPANY,
    PAGINATION,
)
val REFRESH_COMPANY_DEVELOPED_GAMES_USE_CASE_PARAMS =
    RefreshCompanyDevelopedGamesUseCase.Params(
        COMPANY,
        PAGINATION,
    )
val GET_SIMILAR_GAMES_USE_CASE_PARAMS =
    GetSimilarGamesUseCase.Params(DOMAIN_GAME, PAGINATION)
val REFRESH_SIMILAR_GAMES_USE_CASE_PARAMS =
    RefreshSimilarGamesUseCase.Params(DOMAIN_GAME, PAGINATION)
