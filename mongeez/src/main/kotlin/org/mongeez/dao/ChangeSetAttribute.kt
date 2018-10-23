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

package org.mongeez.dao

import org.mongeez.commands.ChangeSet

enum class ChangeSetAttribute(val dbFieldName: String) {
    FILE("file") {
        override fun getAttributeValue(changeSet: ChangeSet): String? {
            return changeSet.file
        }
    },
    CHANGE_ID("changeId") {
        override fun getAttributeValue(changeSet: ChangeSet): String? {
            return changeSet.changeId
        }
    },
    AUTHOR("author") {
        override fun getAttributeValue(changeSet: ChangeSet): String? {
            return changeSet.author
        }
    },
    RESOURCE_PATH("resourcePath") {
        override fun getAttributeValue(changeSet: ChangeSet): String? {
            return changeSet.resourcePath
        }
    };

    internal abstract fun getAttributeValue(changeSet: ChangeSet): String?
}
