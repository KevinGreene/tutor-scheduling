package com.surrealanalysis.tutor.scheduling.domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore

@PlanningSolution
class Schedule() {
    @ValueRangeProvider(id = "tutorRange")
    @ProblemFactCollectionProperty
    lateinit var tutors: List<Tutor>

    @ValueRangeProvider(id = "studentRange")
    @ProblemFactCollectionProperty
    lateinit var students: List<Student>

    @PlanningEntityCollectionProperty
    lateinit var sessions: List<TutoringSession>

    @ProblemFactCollectionProperty
    lateinit var hours: List<Hour>
    @ProblemFactCollectionProperty
    lateinit var specialties: List<Specialty>
    @ProblemFactCollectionProperty
    lateinit var tables: List<Table>
    @PlanningScore
    lateinit var score: HardSoftScore

    constructor(tutors: List<Tutor>,
                students: List<Student>,
                sessions: List<TutoringSession>,
                hours: List<Hour>,
                specialties: List<Specialty>,
                tables: List<Table>) : this() {
        this.tutors = tutors
        this.students = students
        this.sessions = sessions
        this.hours = hours
        this.specialties = specialties
        this.tables = tables
    }
}