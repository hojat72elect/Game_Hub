package ca.hojat.gamehub.core.domain.games.repository

import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.core.domain.entities.Game
import kotlinx.coroutines.flow.Flow

interface LikedGamesLocalDataSource {
    suspend fun likeGame(gameId: Int)
    suspend fun unlikeGame(gameId: Int)
    suspend fun isGameLiked(gameId: Int): Boolean

    /**
     * Checks if a specific game is liked ro not.
     */
    fun observeGameLikeState(gameId: Int): Flow<Boolean>

    /**
     * Returns a list of all liked games.
     */
    fun observeLikedGames(pagination: Pagination): Flow<List<Game>>
}
