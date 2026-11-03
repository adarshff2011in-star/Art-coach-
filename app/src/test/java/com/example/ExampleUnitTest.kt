package com.example

import com.example.data.model.StepPhase
import com.example.data.repository.DrawingLibraryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testCuratedSubjectsExist() {
        val subjects = DrawingLibraryRepository.curatedSubjects
        assertTrue(subjects.isNotEmpty())
        assertTrue(subjects.size >= 3)
    }

    @Test
    fun testSubjectStepsAreSequentialAndValid() {
        val fox = DrawingLibraryRepository.curatedSubjects.firstOrNull { it.id == "fox_beginner" }
        assertNotNull(fox)
        assertEquals(5, fox!!.steps.size)

        // Verify progressive phases: first step is Geometric Bases
        assertEquals(StepPhase.GEOMETRIC_BASES, fox.steps[0].phase)
        assertEquals(1, fox.steps[0].stepIndex)
        assertTrue(fox.steps[0].instruction.isNotBlank())
        assertTrue(fox.steps[0].spokenCoachingEn.isNotBlank())
        assertTrue(fox.steps[0].spokenCoachingHinglish.isNotBlank())
    }
}
