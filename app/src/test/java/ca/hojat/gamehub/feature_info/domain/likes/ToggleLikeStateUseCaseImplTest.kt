package ca.hojat.gamehub.feature_info.domain.likes

import ca.hojat.gamehub.feature_info.TOGGLE_GAME_LIKE_STATE_USE_CASE_PARAMS
import ca.hojat.gamehub.core.domain.entities.Pagination
import ca.hojat.gamehub.core.domain.games.repository.LikedGamesLocalDataSource
import ca.hojat.gamehub.core.domain.entities.Game
import ca.hojat.gamehub.core.common_testing.domain.MainCoroutineRule
import com.google.common.truth.Truth.assertThat
import ca.hojat.gamehub.feature_info.domain.usecases.likes.ToggleLikeStateUseCaseImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val GAME_ID = 100

private val USE_CASE_PARAMS = TOGGLE_GAME_LIKE_STATE_USE_CASE_PARAMS.copy(id = GAME_ID)

class ToggleLikeStateUseCaseImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var likedGamesLocalDataStore: FakeLikedGamesLocalDataSource
    private lateinit var sut: ToggleLikeStateUseCaseImpl

    @Before
    fun setup() {
        likedGamesLocalDataStore = FakeLikedGamesLocalDataSource()
        sut = ToggleLikeStateUseCaseImpl(likedGamesLocalDataStore)
    }

    @Test
    fun `Toggles game from unliked to liked state`() {
        runTest {
            assertThat(likedGamesLocalDataStore.isGameLiked(GAME_ID)).isFalse()

            sut.execute(USE_CASE_PARAMS)

            assertThat(likedGamesLocalDataStore.isGameLiked(GAME_ID)).isTrue()
        }
    }

    @Test
    fun `Toggles game from liked to unliked state`() {
        runTest {
            likedGamesLocalDataStore.likeGame(GAME_ID)

            assertThat(likedGamesLocalDataStore.isGameLiked(GAME_ID)).isTrue()

            sut.execute(USE_CASE_PARAMS)

            assertThat(likedGamesLocalDataStore.isGameLiked(GAME_ID)).isFalse()
        }
    }

    private class FakeLikedGamesLocalDataSource : LikedGamesLocalDataSource {

        private val likedGameIds = mutableSetOf<Int>()

        override suspend fun likeGame(gameId: Int) {
            likedGameIds.add(gameId)
        }

        override suspend fun unlikeGame(gameId: Int) {
            likedGameIds.remove(gameId)
        }

        override suspend fun isGameLiked(gameId: Int): Boolean {
            return likedGameIds.contains(gameId)
        }

        override fun observeGameLikeState(gameId: Int): Flow<Boolean> {
            return flowOf() // no-op
        }

        override fun observeLikedGames(pagination: Pagination): Flow<List<Game>> {
            return flowOf() // no-op
        }
    }
}
