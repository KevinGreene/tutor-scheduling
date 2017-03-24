package com.surrealanalysis.tutor.scheduling.domain

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.variable.PlanningVariable

@PlanningEntity
class TutoringSession() {
    var id: Int = 0
    @PlanningVariable(valueRangeProviderRefs = arrayOf("tutorRange"))
    var tutor: Tutor? = null
    @PlanningVariable(valueRangeProviderRefs = arrayOf("studentRange"), nullable = true)
    var student: Student? = null
    lateinit var table: Table
    lateinit var hour: Hour

    constructor(id: Int, table: Table, hour: Hour) : this() {
        this.id = id
        this.table = table
        this.hour = hour
    }

    fun isTutorAssigned(): Boolean {
        return tutor != null
    }

    fun isStudentAssigned(): Boolean {
        return student != null
    }

    override fun toString(): String {
        return "TutoringSession(id=$id, tutor=$tutor, student=$student, table=$table, hour=$hour)"
    }
}