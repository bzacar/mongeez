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

class ChangeFileSet {
    private var changeFiles: MutableList<ChangeFile> = ArrayList()
    private var utils: MutableMap<String, ChangeFile> = HashMap()
    var util: ChangeFile? = null

    fun add(changeFile: ChangeFile) {
        this.changeFiles.add(changeFile)
    }

    fun getChangeFiles(): List<ChangeFile> {
        return changeFiles
    }

    fun setChangeFiles(changeFile: MutableList<ChangeFile>) {
        this.changeFiles = changeFile
    }

    fun addUtil(utilFile: ChangeFile) {
        utils[utilFile.path] = utilFile
    }

    fun getUtils(): MutableMap<String, ChangeFile> {
        return utils
    }

    fun setUtils(utils: MutableMap<String, ChangeFile>) {
        this.utils = utils
    }
}
