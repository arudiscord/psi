package pw.aru.utils

import com.mewna.catnip.Catnip
import com.mewna.catnip.extension.AbstractExtension
import org.kodein.di.DKodeinAware
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct

class KodeinExtension(kodein: Kodein) :
    AbstractExtension("kodein"),
    KodeinAware by kodein,
    DKodeinAware by kodein.direct

fun Catnip.kodein(): KodeinExtension = extension(KodeinExtension::class.java)!!