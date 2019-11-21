package pw.aru.psi.bootstrap

import pw.aru.psi.PsiApplication
import pw.aru.psi.commands.RegistryPhase

sealed class PsiApplicationEvent {
    abstract val application: PsiApplication
}

class ApplicationStartedEvent(override val application: PsiApplication) : PsiApplicationEvent()

class BeforeRegistryPhaseEvent(override val application: PsiApplication, val phase: RegistryPhase) : PsiApplicationEvent()

class AfterRegistryPhaseEvent(override val application: PsiApplication, val phase: RegistryPhase) : PsiApplicationEvent()

class FailedBootEvent(override val application: PsiApplication, exception: Exception) : PsiApplicationEvent()

class SuccessfulBootEvent(override val application: PsiApplication) : PsiApplicationEvent()

class BeforeShutdownEvent(override val application: PsiApplication) : PsiApplicationEvent()

class ShutdownEvent(override val application: PsiApplication) : PsiApplicationEvent()