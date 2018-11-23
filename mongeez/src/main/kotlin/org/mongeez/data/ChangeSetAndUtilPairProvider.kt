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
        val (changeSetFiles, utils) = changeSetFileProvider.changeSetFiles
        val changeSets = changeSetFiles.getChangeSets()
        val utilChangeSet = utils.getChangeSets()
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

    private fun Map<String, Resource>.getChangeSets(): Map<String, ChangeSet> {
        return asSequence()
                .mapNotNull { (key, value) ->
                    ChangeSetReaderFactory
                            .getChangeSetReader(value)
                            ?.getChangeSets(value)
                            ?.single()
                            ?.let { key to it }
                }
                .toMap()
    }

    private fun Resource?.getChangeSet(): ChangeSet? {
        return this
                ?.let { ChangeSetReaderFactory.getChangeSetReader(it)?.getChangeSets(it) }
                ?.single()
    }
}
