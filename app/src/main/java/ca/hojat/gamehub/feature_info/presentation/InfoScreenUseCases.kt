package ca.hojat.gamehub.feature_info.presentation

import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameImageUrlsUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.game.GetGameInfoUseCase
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ToggleLikeStateUseCase
import javax.inject.Inject

class InfoScreenUseCases @Inject constructor(
    val getGameInfoUseCase: GetGameInfoUseCase,
    val getGameImageUrlsUseCase: GetGameImageUrlsUseCase,
    val toggleLikeStateUseCase: ToggleLikeStateUseCase,
)
