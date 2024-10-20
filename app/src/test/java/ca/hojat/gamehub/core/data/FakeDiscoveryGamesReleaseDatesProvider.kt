package ca.hojat.gamehub.core.data

import ca.hojat.gamehub.core.data.games.common.DiscoveryGamesReleaseDatesProvider

class FakeDiscoveryGamesReleaseDatesProvider : DiscoveryGamesReleaseDatesProvider {
    override fun getPopularGamesMinReleaseDate(): Long = 500L
    override fun getRecentlyReleasedGamesMinReleaseDate(): Long = 500L
    override fun getRecentlyReleasedGamesMaxReleaseDate(): Long = 500L
    override fun getComingSoonGamesMinReleaseDate(): Long = 500L
    override fun getMostAnticipatedGamesMinReleaseDate(): Long = 500L
}
