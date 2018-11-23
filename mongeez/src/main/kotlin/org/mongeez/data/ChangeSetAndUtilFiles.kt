package org.mongeez.data

import org.springframework.core.io.Resource

data class ChangeSetAndUtilFiles(val changeSetFiles: List<Resource>, val utils: Map<String, Resource>)
