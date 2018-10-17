package org.mongeez.data

import org.mongeez.commands.ChangeSet
import org.slf4j.LoggerFactory

object ChangeSetLogger {
    private val logger = LoggerFactory.getLogger(ChangeSetLogger::class.java)

    fun log(changeSets: List<ChangeSet>) {
        if (logger.isTraceEnabled) {
            changeSets.forEach(::logChangeSet)
        }
    }

    private fun logChangeSet(changeSet: ChangeSet) {
        logger.trace("Changeset")
        logger.trace("id: " + changeSet.changeId)
        logger.trace("author: " + changeSet.author)
        if (changeSet.getContexts().isNotEmpty()) {
            logger.trace("contexts: {}", changeSet.getContexts())
        }
        logger.trace("script")
        logger.trace(changeSet.getMergedScript().body)
    }
}
