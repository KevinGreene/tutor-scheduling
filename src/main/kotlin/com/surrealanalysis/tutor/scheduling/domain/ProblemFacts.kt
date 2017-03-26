package com.surrealanalysis.tutor.scheduling.domain

abstract class ProblemFact {
    abstract val id: Int
    override fun equals(other: Any?): Boolean {
        return other != null
                && other is ProblemFact
                && other.javaClass === this.javaClass
                && other.id == id
    }

    override fun hashCode(): Int = id
}

data class Tutor(override val id: Int,
                 val name: String,
                 val specialties: List<Specialty>,
                 val status: EmploymentStatus,
                 val preferredSessionSize: Int) : ProblemFact() {

    fun isFreeForHour(hour: Hour): Boolean {
        when (status) {
            EmploymentStatus.FULL_TIME -> return true
            EmploymentStatus.HALF_TIME_EVEN -> return hour.hour % 2 == 0
            EmploymentStatus.HALF_TIME_ODD -> return hour.hour % 2 == 1
        }
    }
}

data class Student(override val id: Int,
                   val name: String,
                   val requiredSpecialties: List<Specialty>,
                   val preferredSpecialties: List<Specialty>,
                   val preferredSessionSize: Int) : ProblemFact()


data class Specialty(override val id: Int, val name: String) : ProblemFact()

data class Table(val id: Int)
data class Hour(val hour: Int)


enum class EmploymentStatus {
    FULL_TIME,
    HALF_TIME_ODD,
    HALF_TIME_EVEN
}