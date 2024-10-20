package ca.hojat.gamehub.feature_discovery.widgets

import ca.hojat.gamehub.core.factories.IgdbImageSize
import ca.hojat.gamehub.core.factories.IgdbImageUrlFactory
import ca.hojat.gamehub.core.domain.entities.Game
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

/**
 * This mapper converts Domain entities to stuff we can use
 * in the UI layer of "discover" feature.
 */
abstract class DiscoverItemModelMapper {

    /**
     * You give it a [Game] and it will be converted to the normal data we use
     * in discover screen; which is [DiscoverScreenItemData].
     */
    abstract fun mapToUiModel(game: Game): DiscoverScreenItemData

    /**
     * You give it a list of [Game]s and they will be converted to a list of [DiscoverScreenItemData].
     */
    fun mapToUiModels(
        games: List<Game>,
    ): List<DiscoverScreenItemData> {
        return games.map(::mapToUiModel)
    }
}

@BindType(installIn = BindType.Component.VIEW_MODEL)
class DiscoverItemModelMapperImpl @Inject constructor(
    private val igdbImageUrlFactory: IgdbImageUrlFactory
) : DiscoverItemModelMapper() {

    override fun mapToUiModel(game: Game): DiscoverScreenItemData {
        return DiscoverScreenItemData(
            id = game.id,
            title = game.name,
            coverUrl = game.cover?.let { cover ->
                igdbImageUrlFactory.createUrl(
                    cover,
                    IgdbImageUrlFactory.Config(IgdbImageSize.BIG_COVER)
                )
            }
        )
    }
}
