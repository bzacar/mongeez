package org.mongeez

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag(DAO_TAG)
class MongeezDryRunTest {
    @BeforeEach
    fun setUp() {
        db.drop()
    }

    @Test
    fun testWhenNoChangeSetRunOnDatabaseYet() {
        val (lastChangeSetSummary, changeSetsToBeRun) = createMongeezWithoutShell(FILE_SET_WITH_TWO_CHANGE_SET_FILES).dryRun()
        assertThat(lastChangeSetSummary).isNull()
        assertThat(changeSetsToBeRun).hasSize(4).containsExactlyElementsOf(ALL_CHANGE_SETS)
    }

    @Test
    fun testWhenThereAreChangeSetsRunOnDatabaseAndThereAreChangeSetsToBeRun() {
        createMongeezWithoutShell(FILE_SET_WITH_ONE_CHANGE_SET_FILE).process()
        val (lastChangeSetSummary, changeSetsToBeRun) = createMongeezWithoutShell(FILE_SET_WITH_TWO_CHANGE_SET_FILES).dryRun()
        assertThat(lastChangeSetSummary).isEqualTo(CHANGE_SET2)
        assertThat(changeSetsToBeRun).hasSize(2).containsExactlyElementsOf(CHANGE_SETS_FROM_SECOND_FILE)
    }

    @Test
    fun testWhenThereAreChangeSetsRunOnDatabaseAndNoChangeSetsToBeRun() {
        createMongeezWithoutShell(FILE_SET_WITH_TWO_CHANGE_SET_FILES).process()
        val (lastChangeSetSummary, changeSetsToBeRun) = createMongeezWithoutShell(FILE_SET_WITH_TWO_CHANGE_SET_FILES).dryRun()
        assertThat(lastChangeSetSummary).isEqualTo(CHANGE_SET4)
        assertThat(changeSetsToBeRun).isEmpty()
    }

    @Test
    fun testWhenNoChangeSetRunOnDatabaseYetAndNoChangeSetsToBeRun() {
        val (lastChangeSetSummary, changeSetsToBeRun) = createMongeezWithoutShell(FILE_SET_WITH_NO_CHANGE_SET_FILES).dryRun()
        assertThat(lastChangeSetSummary).isNull()
        assertThat(changeSetsToBeRun).isEmpty()
    }

    private companion object {
        const val FILE_SET_WITH_TWO_CHANGE_SET_FILES = "mongeez.xml"
        const val FILE_SET_WITH_ONE_CHANGE_SET_FILE = "mongeez_with_one_changeset.xml"
        const val FILE_SET_WITH_NO_CHANGE_SET_FILES = "mongeez_empty.xml"
        const val CHANGE_SET1 = "mlysaght:ChangeSet-1:changeset1.xml"
        const val CHANGE_SET2 = "mlysaght:ChangeSet-2:changeset1.xml"
        const val CHANGE_SET3 = "mlysaght:ChangeSet-3:changeset2.xml"
        const val CHANGE_SET4 = "mlysaght:ChangeSet-4:changeset2.xml"
        val CHANGE_SETS_FROM_SECOND_FILE = listOf(CHANGE_SET3, CHANGE_SET4)
        val ALL_CHANGE_SETS = listOf(CHANGE_SET1, CHANGE_SET2) + CHANGE_SETS_FROM_SECOND_FILE
    }
}
