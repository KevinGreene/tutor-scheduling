package com.surrealanalysis.tutor.scheduling.solver

import com.surrealanalysis.tutor.scheduling.IdCounter
import com.surrealanalysis.tutor.scheduling.domain.*
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier
import spock.lang.Specification

class SchedulerRuleSpec extends Specification {

    HardSoftScoreVerifier<Schedule> scoreVerifier
    IdCounter idCounter

    def setup() {
        scoreVerifier = new HardSoftScoreVerifier<>(SolverFactory.createFromXmlResource("com/surrealanalysis/tutor/scheduling/solver/solverConfig.xml"))
        idCounter = new IdCounter()
    }

    def "Tutors should be free when scheduled"() {
        given:
        def tutorFull = new Tutor(idCounter.id, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def tutorOdd = new Tutor(idCounter.id, "Test Tutor 2", [], EmploymentStatus.HALF_TIME_ODD, 1)
        def tutorEven = new Tutor(idCounter.id, "Test Tutor 3", [], EmploymentStatus.HALF_TIME_EVEN, 1)

        def firstHour = new Hour(1)
        def secondHour = new Hour(2)
        def thirdHour = new Hour(3)
        def table = new Table(idCounter.id)

        def sessions = [new TutoringSession(id: idCounter.id, table: table, hour: firstHour, tutor: tutorFull),
                        new TutoringSession(id: idCounter.id, table: table, hour: firstHour, tutor: tutorOdd),
                        new TutoringSession(id: idCounter.id, table: table, hour: firstHour, tutor: tutorEven),
                        new TutoringSession(id: idCounter.id, table: table, hour: secondHour, tutor: tutorFull),
                        new TutoringSession(id: idCounter.id, table: table, hour: secondHour, tutor: tutorEven),
                        new TutoringSession(id: idCounter.id, table: table, hour: thirdHour, tutor: tutorEven),
        ]

        when:
        Schedule schedule = new Schedule(
                tutors: [tutorFull, tutorOdd, tutorEven],
                students: [],
                sessions: sessions,
                hours: [firstHour],
                specialties: [],
                tables: [table],
        )


        then:
        scoreVerifier.assertHardWeight("Tutors should be free when scheduled", -2, schedule)

    }

    def "Tutors should only be scheduled for a single table"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(idCounter.id)
        def table2 = new Table(idCounter.id)

        def tutor = new Tutor(idCounter.id, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def sessions = [
                new TutoringSession(id: idCounter.id, table: table1, hour: hour, tutor: tutor),
                new TutoringSession(id: idCounter.id, table: table2, hour: hour, tutor: tutor),
        ]

        when:
        Schedule schedule = new Schedule(
                tutors: [tutor],
                students: [],
                sessions: sessions,
                hours: [hour],
                specialties: [],
                tables: [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Tutors should only be scheduled for a single table", -1, schedule)
    }

    def "Students should only be scheduled for a single session"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(idCounter.id)
        def table2 = new Table(idCounter.id)

        def student = new Student(idCounter.id, "Test Student 1", [], [], 1)
        def sessions = [
                new TutoringSession(id: idCounter.id, table: table1, hour: hour, student: student),
                new TutoringSession(id: idCounter.id, table: table2, hour: hour, student: student)
        ]

        when:
        Schedule schedule = new Schedule(
                tutors: [],
                students: [student],
                sessions: sessions,
                hours: [hour],
                specialties: [],
                tables: [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Students should only be scheduled for a single session", -1, schedule)
    }

    def "Students should be scheduled for a session"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(idCounter.id)
        def table2 = new Table(idCounter.id)

        def student = new Student(idCounter.id, "Test Student 1", [], [], 1)

        when:
        Schedule schedule = new Schedule(
                tutors: [],
                students: [student],
                sessions: [],
                hours: [hour],
                specialties: [],
                tables: [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Students should be scheduled for a session", -1, schedule)
    }

    def "Tutors should have at least 2 students"() {
        given:
        def hour1 = new Hour(1)
        def hour2 = new Hour(2)
        def table1 = new Table(idCounter.id)
        def table2 = new Table(idCounter.id)
        def tutor1 = new Tutor(idCounter.id, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def tutor2 = new Tutor(idCounter.id, "Test Tutor 2", [], EmploymentStatus.FULL_TIME, 1)
        def tutor3 = new Tutor(idCounter.id, "Test Tutor 3", [], EmploymentStatus.FULL_TIME, 1)

        def student1 = new Student(idCounter.id, "Test Student 1", [], [], 1)
        def student2 = new Student(idCounter.id, "Test Student 2", [], [], 1)

        def sessions = [
                new TutoringSession(id: idCounter.id, table: table1, hour: hour1, tutor: tutor1, student: student1),
                new TutoringSession(id: idCounter.id, table: table1, hour: hour2, tutor: tutor1, student: student2),
                new TutoringSession(id: idCounter.id, table: table2, hour: hour1, tutor: tutor2, student: student1),
                new TutoringSession(id: idCounter.id, table: table2, hour: hour1, tutor: tutor2, student: student2),
                new TutoringSession(id: idCounter.id, table: table2, hour: hour1, tutor: tutor3, student: student1),
        ]

        when:
        Schedule schedule = new Schedule(
                tutors: [tutor1, tutor2, tutor3],
                students: [student1, student2],
                sessions: sessions,
                hours: [hour1, hour2],
                specialties: [],
                tables: [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Tutors should have at least 2 students", -1, schedule)
    }
}
