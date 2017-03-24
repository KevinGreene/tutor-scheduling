package com.surrealanalysis.tutor.scheduling.domain

data class Tutor(val id: Int,
                 val name: String,
                 val specialties: List<Specialty>,
                 val status: EmploymentStatus,
                 val preferredSessionSize: Int) {

    override fun equals(other: Any?): Boolean = other is Tutor && other.id == id
    override fun hashCode(): Int = id

    fun isFreeForHour(hour: Hour): Boolean {
        when (status) {
            EmploymentStatus.FULL_TIME -> return true
            EmploymentStatus.HALF_TIME_EVEN -> return hour.hour % 2 == 0
            EmploymentStatus.HALF_TIME_ODD -> return hour.hour % 2 == 1
        }
    }
}

data class Student(val id: Int,
                   val name: String,
                   val requiredSpecialties: List<Specialty>,
                   val preferredSpecialties: List<Specialty>,
                   val preferredSessionSize: Int) {
    override fun equals(other: Any?): Boolean = other is Student && other.id == id
    override fun hashCode(): Int = id
}


data class Specialty(val id: Int, val name: String) {
    override fun equals(other: Any?): Boolean = other is Specialty && other.id == id
    override fun hashCode(): Int = id
}


data class Table(val id: Int)
data class Hour(val hour: Int)


enum class EmploymentStatus {
    FULL_TIME,
    HALF_TIME_ODD,
    HALF_TIME_EVEN
}