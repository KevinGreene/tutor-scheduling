package com.surrealanalysis.tutor.scheduling.solver

import com.github.javafaker.Faker
import com.surrealanalysis.tutor.scheduling.IdCounter
import com.surrealanalysis.tutor.scheduling.domain.*
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.impl.score.director.ScoreDirector
import spock.lang.Specification

class SchedulerSpec extends Specification {

    Faker faker
    Random random

    def setup() {
        faker = new Faker()
        random = new Random()
    }

    def makeTutor(int id, List<Specialty> specialties, EmploymentStatus status) {
        def fakeName = faker.name()
        def formattedName = "${fakeName.firstName()} ${fakeName.lastName()}"
        def specialtyCount = random.nextInt(3)
        def tutorSpecialties = []
        specialtyCount.each {
            tutorSpecialties << specialties[random.nextInt(specialties.size())]
        }
        tutorSpecialties = tutorSpecialties.unique()
        def preferredSessionSize = random.nextInt(3) + 1
        new Tutor(id, formattedName, tutorSpecialties, status, preferredSessionSize)
    }

    def makeStudent(int id, List<Specialty> specialties) {
        def fakeName = faker.name()
        def formattedName = "${fakeName.firstName()} ${fakeName.lastName()}"
        def specialtyCount = random.nextInt(2)
        def requiredSpecialties = []
        specialtyCount.each {
            requiredSpecialties << specialties[random.nextInt(specialties.size())]
        }
        specialtyCount = random.nextInt(2)
        def preferredSpecialties = []
        specialtyCount.each {
            preferredSpecialties << specialties[random.nextInt(specialties.size())]
        }
        def preferredSessionSize = random.nextInt(3) + 1
        new Student(id, formattedName, requiredSpecialties, preferredSpecialties, preferredSessionSize)
    }

    def "should run schedule"() {
        given:
        Random random = new Random();
        def hours = (1..8).collect {
            new Hour(it)
        }
        def tables = (1..6).collect {
            new Table(it)
        }
        def specialties = (1..10).collect {
            new Specialty(it, faker.color().name())
        }

        def tutors = (1..3).collect {
            makeTutor(it, specialties, EmploymentStatus.FULL_TIME)
        } + (4..6).collect {
            makeTutor(it, specialties, EmploymentStatus.HALF_TIME_EVEN)
        } + (7..9).collect {
            makeTutor(it, specialties, EmploymentStatus.HALF_TIME_ODD)
        }.flatten()

        def students = (1..90).collect {
            makeStudent(it, specialties)
        }

        List<TutoringSession> tutoringSessions = []
        tables.eachWithIndex { Table table, int i ->
            hours.eachWithIndex { Hour hour, int j ->
                (1..3).collect {
                    def id = i + j + it
                    tutoringSessions << new TutoringSession(id, table, hour)
                }
            }
        }

        def schedule = new Schedule(
                tutors,
                students,
                tutoringSessions,
                hours,
                specialties,
                tables)

        SolverFactory<Schedule> solverFactory = SolverFactory.createFromXmlResource(
                "com/surrealanalysis/tutor/scheduling/solver/solverConfig.xml")
        def solver = solverFactory.buildSolver()

        when:
        def newSchedule = solver.solve(schedule)
        ScoreDirector scoreDirector = solver.getScoreDirectorFactory().buildScoreDirector()
        scoreDirector.setWorkingSolution(newSchedule)
        scoreDirector.constraintMatchTotals.each { constraintMatchTotal ->
            println "=================================="
            println "Constraint Violation: ${constraintMatchTotal.constraintName}"
            println "Count: ${constraintMatchTotal.constraintMatchCount}"
            println "=================================="

            constraintMatchTotal.constraintMatchSet.each { constraintMatch ->
                println "=================================="
                println "Constraint Match: ${constraintMatch.justificationList}"
                println "=================================="
            }
        }

        then:
        newSchedule.score.isFeasible()
    }
}
