package org.mongeez.data

import org.mongeez.commands.ChangeSet
import org.mongeez.reader.ChangeSetFileProvider
import org.mongeez.reader.ChangeSetReaderFactory
import org.mongeez.validation.ChangeSetsValidator
import org.mongeez.validation.DefaultChangeSetsValidator
import org.springframework.core.io.Resource

internal class ChangeSetAndUtilPairProvider {
    lateinit var changeSetFileProvider: ChangeSetFileProvider
    var changeSetsValidator: ChangeSetsValidator = DefaultChangeSetsValidator()

    fun get(): ChangeSetAndUtilPair {
        val (changeSetFiles, util) = changeSetFileProvider.changeSetFiles
        val changeSets = changeSetFiles.getChangeSets()
        val utilChangeSet = util.getChangeSet()
        return ChangeSetAndUtilPair(changeSets, utilChangeSet)
    }

    private fun List<Resource>.getChangeSets(): List<ChangeSet> {
        return asSequence()
                .mapNotNull { ChangeSetReaderFactory.getChangeSetReader(it)?.getChangeSets(it) }
                .flatten().toList()
                .also {
                    ChangeSetLogger.log(it)
                    changeSetsValidator.validate(it)
                }
    }

    private fun Resource?.getChangeSet(): ChangeSet? {
        return this
                ?.let { ChangeSetReaderFactory.getChangeSetReader(it)?.getChangeSets(it) }
                ?.single()
    }
}
