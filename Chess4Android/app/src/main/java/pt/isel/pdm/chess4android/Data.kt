package pt.isel.pdm.chess4android

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep //Keep anotation denotes that the annotated element should not be removed when the code is minified at build time
@Serializable
data class Data(val a: Int, val b: String)
