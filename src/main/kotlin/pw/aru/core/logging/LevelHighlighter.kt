package pw.aru.core.logging

import ch.qos.logback.classic.Level.*
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import ch.qos.logback.core.pattern.color.ANSIConstants.*

class LevelHighlighter : CompositeConverter<ILoggingEvent>() {
    override fun transform(event: ILoggingEvent, input: String): String {
        val color = getForegroundColorCode(event) ?: return input
        return "$ESC_START$color$ESC_END$input$resetColor"
    }

    private fun getForegroundColorCode(event: ILoggingEvent): String? {
        return when (event.level.toInt()) {
            ERROR_INT -> BOLD + RED_FG
            WARN_INT -> BOLD + YELLOW_FG
            DEBUG_INT -> MAGENTA_FG
            INFO_INT -> CYAN_FG
            TRACE_INT -> BOLD + BLUE_FG
            else -> DEFAULT_FG
        }
    }

    companion object {
        private const val resetColor = "${ESC_START}0;$DEFAULT_FG$ESC_END"
    }
}
