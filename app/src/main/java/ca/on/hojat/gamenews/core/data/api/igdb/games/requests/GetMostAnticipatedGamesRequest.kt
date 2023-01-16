package ca.on.hojat.gamenews.core.data.api.igdb.games.requests

import ca.on.hojat.gamenews.core.data.api.common.ApiRequest

data class GetMostAnticipatedGamesRequest(
    val minReleaseDateTimestamp: Long,
    override val offset: Int,
    override val limit: Int,
) : ApiRequest
