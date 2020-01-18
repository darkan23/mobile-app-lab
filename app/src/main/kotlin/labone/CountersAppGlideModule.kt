package labone

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

/**
 * @author rbarykin
 * @since 26.10.18.
 *
 * @see [Glide denerated API](https://bumptech.github.io/glide/doc/generatedapi.html)
 */
@GlideModule
class CountersAppGlideModule : AppGlideModule() {
    override fun isManifestParsingEnabled(): Boolean = false

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        (context.applicationContext as? PerformancesApp)?.component?.let { component ->
            registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                OkHttpUrlLoader.Factory(component.okHttpClient())
            )
        }
    }
}
