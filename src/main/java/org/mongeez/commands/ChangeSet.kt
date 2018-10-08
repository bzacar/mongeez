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
    lateinit var resourcePath: String
    private var contextsStr: String? = null
    private var contexts: List<String>? = null

    var isFailOnError = true
    var isRunAlways: Boolean = false

    private val commands = ArrayList<Script>()

    fun add(command: Script) {
        commands.add(command)
    }

    fun getCommands(): List<Script> {
        return commands
    }

    fun getContexts(): String {
        return getContextStr()
    }

    fun setContexts(contextsStr: String?) {
        this.contextsStr = contextsStr
        contexts = null
    }

    fun canBeAppliedInContext(context: String?): Boolean {
        if (contextsStr == null) {
            return true
        }
        if (contexts == null) {
            contexts = getContextStr()
                    .split(",".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .asSequence()
                    .map { requiredContext ->
                        requiredContext.toLowerCase().trim()
                    }
                    .filter { it.isNotEmpty() }
                    .toList()
        }
        return getContextsList().isEmpty() || context != null && getContextsList().contains(context.toLowerCase().trim())
    }

    private fun getContextStr() = contextsStr ?: ""

    private fun getContextsList() = contexts ?: emptyList()
}
