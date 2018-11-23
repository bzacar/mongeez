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

package org.mongeez.commands

import java.util.ArrayList

class ChangeSet {
    lateinit var changeId: String
    lateinit var author: String
    lateinit var file: String
    var resourcePath: String? = null
    private var contextsStr: String = ""
    private var contexts: Set<String> = emptySet()

    var isFailOnError = true
    var isRunAlways: Boolean = false
    private var utilsStr = ""
    private var utils: List<String> = emptyList()

    private val commands = ArrayList<Script>()

    fun add(command: Script) {
        commands.add(command)
    }

    fun getCommands(): List<Script> {
        return commands
    }

    fun getMergedScript(): Script {
        return Script(commands.joinToString("\n") { it.body })
    }

    fun getContexts(): String {
        return contextsStr
    }

    fun setContexts(contextsStr: String?) {
        this.contextsStr = contextsStr.orEmpty()
        contexts = emptySet()
    }

    fun canBeAppliedInContext(context: String?): Boolean {
        if (contextsStr.isEmpty()) {
            return true
        }
        if (contexts.isEmpty()) {
            contexts = contextsStr
                    .split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .asSequence()
                    .map { requiredContext ->
                        requiredContext.toLowerCase().trim()
                    }
                    .filter { it.isNotEmpty() }
                    .toSet()
        }
        return contexts.isEmpty() || context != null && contexts.contains(context.toLowerCase().trim())
    }

    fun setUtils(utilsStr: String?) {
        this.utilsStr = utilsStr.orEmpty()
        utils = emptyList()
    }

    fun getUtils(): String {
        return utilsStr
    }

    fun getUtilsList(): List<String> {
        if (utilsStr.isEmpty()) {
            return emptyList()
        }
        if (utils.isEmpty()) {
            utils = utilsStr
                    .split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .asSequence()
                    .map { utilPath ->
                        utilPath.trim()
                    }
                    .filter { it.isNotEmpty() }
                    .toList()
        }
        return utils
    }

    fun summary(): String = "$author:$changeId:${resourcePath ?: file}"
}
