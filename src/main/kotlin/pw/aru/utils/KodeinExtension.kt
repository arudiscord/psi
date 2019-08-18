package pw.aru.utils

import com.mewna.catnip.Catnip
import com.mewna.catnip.extension.AbstractExtension
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class KodeinExtension(override val kodein: Kodein) : AbstractExtension("kodein"), KodeinAware

fun Catnip.kodein(): KodeinAware = extension(KodeinExtension::class.java)!!