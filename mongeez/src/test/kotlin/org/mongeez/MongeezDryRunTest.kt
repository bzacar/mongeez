package org.mongeez

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

@Tag("dao")
class MongeezDryRunTest {
    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testWhenNoChangeSetRunOnDatabaseYet() {
        val (lastChangeSetSummary, changeSetsToBeRun) = create("mongeez.xml").dryRun()
        assertThat(lastChangeSetSummary).isNull()
        assertThat(changeSetsToBeRun).hasSize(4).containsExactlyElementsOf(ALL_CHANGE_SETS)
    }

    @Test
    fun testWhenThereAreChangeSetsRunOnDatabaseAndThereAreChangeSetsToBeRun() {
        create("mongeez_with_one_changeset.xml").process()
        val (lastChangeSetSummary, changeSetsToBeRun) = create("mongeez.xml").dryRun()
        assertThat(lastChangeSetSummary).isEqualTo(CHANGE_SET2)
        assertThat(changeSetsToBeRun).hasSize(2).containsExactlyElementsOf(CHANGE_SETS_FROM_SECOND_FILE)
    }

    @Test
    fun testWhenThereAreChangeSetsRunOnDatabaseAndNoChangeSetsToBeRun() {
        create("mongeez.xml").process()
        val (lastChangeSetSummary, changeSetsToBeRun) = create("mongeez.xml").dryRun()
        assertThat(lastChangeSetSummary).isEqualTo(CHANGE_SET4)
        assertThat(changeSetsToBeRun).isEmpty()
    }

    @Test
    fun testWhenNoChangeSetRunOnDatabaseYetAndNoChangeSetsToBeRun() {
        val (lastChangeSetSummary, changeSetsToBeRun) = create("mongeez_empty.xml").dryRun()
        assertThat(lastChangeSetSummary).isNull()
        assertThat(changeSetsToBeRun).isEmpty()
    }

    private fun create(path: String): Mongeez {
        return Mongeez().apply {
            setFile(ClassPathResource(path))
            setServerAddress(serverAddress)
            setDbName(DB_NAME)
        }
    }

    private companion object {
        const val CHANGE_SET1 = "mlysaght:ChangeSet-1:changeset1.xml"
        const val CHANGE_SET2 = "mlysaght:ChangeSet-2:changeset1.xml"
        const val CHANGE_SET3 = "mlysaght:ChangeSet-3:changeset2.xml"
        const val CHANGE_SET4 = "mlysaght:ChangeSet-4:changeset2.xml"
        val CHANGE_SETS_FROM_SECOND_FILE = listOf(CHANGE_SET3, CHANGE_SET4)
        val ALL_CHANGE_SETS = listOf(CHANGE_SET1, CHANGE_SET2) + CHANGE_SETS_FROM_SECOND_FILE
    }
}
