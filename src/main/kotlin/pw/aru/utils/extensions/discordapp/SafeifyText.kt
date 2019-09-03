package pw.aru.utils.extensions.discordapp

import pw.aru.utils.extensions.lang.replaceEach

/**
 * Tries to turn a [String] into a safe output
 * by escaping specific characters.
 *
 * _This function is always up for discussion
 * and improvements. Issues and PRs at GitHub
 * are welcome._
 */
fun String.safeUserInput() = replaceEach(
    "@everyone" to "@\u200Beveryone",
    "@here" to "@\u200Bhere"
)