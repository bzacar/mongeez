/*
 * Copyright 2011 SecondMarket Labs, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mongeez

import org.mongeez.commands.ChangeSet
import org.mongeez.data.ChangeSetAndUtilPair
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class ChangeSetsExecutor
constructor(private val context: String?,
            private val changeSetExecutor: ChangeSetExecutor) {

    fun execute(changeSets: ChangeSetAndUtilPair) {
        getExecutableChangeSets(changeSets)
                .forEach { changeSet ->
                    changeSetExecutor.execute(changeSet, changeSets.getUtilScript(changeSet.getUtilsList()))
                    logger.info("ChangeSet " + changeSet.changeId + " has been executed")
                }
    }

    fun getExecutables(changeSets: ChangeSetAndUtilPair): Pair<String?, List<String>> {
        return changeSetExecutor.getLastExecutedChangeSet()?.summary() to
                getExecutableChangeSets(changeSets).map { it.summary() }.toList()
    }

    private fun getExecutableChangeSets(changeSets: ChangeSetAndUtilPair): Sequence<ChangeSet> {
        return changeSets.changeSets.asSequence()
                .filterElse({ it.canBeAppliedInContext(context) })
                { logger.info("Not executing Changeset {} it cannot run in the context {}", it.changeId, context) }
                .filterElse({ it.isRunAlways || changeSetExecutor.notExecuted(it) })
                { logger.info("ChangeSet already executed: " + it.changeId) }
    }

    internal companion object {
        val logger: Logger = LoggerFactory.getLogger(ChangeSetsExecutor::class.java)

        private fun Sequence<ChangeSet>.filterElse(predicate: (ChangeSet) -> Boolean, elseBlock: (ChangeSet) -> Unit): Sequence<ChangeSet> {
            return filter { changeSet ->
                val predicateResult = predicate(changeSet)
                if (!predicateResult) elseBlock(changeSet)
                predicateResult
            }
        }
    }
}
