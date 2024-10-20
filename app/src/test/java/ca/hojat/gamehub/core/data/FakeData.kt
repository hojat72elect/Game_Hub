package ca.hojat.gamehub.core.data

import ca.hojat.gamehub.core.domain.auth.entities.OauthCredentials
import ca.hojat.gamehub.core.domain.entities.Company

val DOMAIN_OAUTH_CREDENTIALS = OauthCredentials(
    accessToken = "access_token",
    tokenType = "token_type",
    tokenTtl = 5000L,
)

val DOMAIN_COMPANY = Company(
    id = 1,
    name = "name",
    websiteUrl = "website_url",
    logo = null,
    developedGames = listOf(1, 2, 3),
)
