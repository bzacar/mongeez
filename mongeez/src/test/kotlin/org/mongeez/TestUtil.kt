package org.mongeez

import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Condition
import org.assertj.core.api.ListAssert
import org.assertj.core.groups.Tuple
import org.mongeez.commands.ChangeSet
import org.mongeez.commands.Script
import java.util.function.Predicate

internal const val DB_NAME = "test_mongeez"
internal val serverAddress = ServerAddress()
internal val db: MongoDatabase by lazy { MongoClient(serverAddress).getDatabase(DB_NAME) }

internal fun ListAssert<ChangeSet>.containsChangeSets(vararg changeSetInfo: Pair<Tuple, String>) {
    val changeSetSummaries = changeSetInfo.map { it.first }
    val scriptBodies = changeSetInfo.map { it.second }
    extracting("author", "changeId", "isRunAlways", "file")
            .containsExactlyElementsOf(changeSetSummaries)
    Condition<List<Script>>(Predicate { it.size == 1 }, "")
    extracting<List<Script>> {
        it.getCommands()
    }.allMatch { it.size == 1 }
            .extracting<String> { (it as? List<*>)?.filterIsInstance<Script>()?.firstOrNull()?.body }
            .containsExactlyElementsOf(scriptBodies)

}

internal fun assertThatCollections(vararg collectionName: String) = CollectionCountAssertion(db, collectionName)

internal class CollectionCountAssertion(private val db: MongoDatabase, private val collectionNames: Array<out String>) {
    private var expectedCount: LongArray = LongArray(0)

    fun have(vararg count: Long): CollectionCountAssertion {
        expectedCount = count
        return this
    }

    fun documents() {
        val actualCounts = collectionNames.map { db.getCollection(it).countDocuments() }
        assertThat(actualCounts).containsExactlyElementsOf(expectedCount.asIterable())
    }

    fun containsNoDocuments() {
        val actualCounts = collectionNames.map { db.getCollection(it).countDocuments() }
        assertThat(actualCounts).allMatch { it == 0L }
    }
}
