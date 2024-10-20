package ca.hojat.gamehub.initializers

import ca.hojat.gamehub.common_ui.images.ImageLoaderInitializer
import com.paulrybitskyi.hiltbinder.BindType
import javax.inject.Inject

@BindType(contributesTo = BindType.Collection.SET)
class ImageLoaderDelegateInitializer @Inject constructor(
    private val imageLoaderInitializer: ImageLoaderInitializer,
) : Initializer {

    override fun init() {
        imageLoaderInitializer.init()
    }
}
