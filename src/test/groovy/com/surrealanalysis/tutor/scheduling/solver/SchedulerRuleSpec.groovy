package com.surrealanalysis.tutor.scheduling.solver

import com.surrealanalysis.tutor.scheduling.domain.*
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier
import spock.lang.Specification

class SchedulerRuleSpec extends Specification {

    HardSoftScoreVerifier<Schedule> scoreVerifier

    def setup() {
        scoreVerifier = new HardSoftScoreVerifier<>(SolverFactory.createFromXmlResource("com/surrealanalysis/tutor/scheduling/solver/solverConfig.xml"))
    }

    def "Tutors should be free when scheduled"() {
        given:
        def tutorFull = new Tutor(1, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def tutorOdd = new Tutor(2, "Test Tutor 2", [], EmploymentStatus.HALF_TIME_ODD, 1)
        def tutorEven = new Tutor(3, "Test Tutor 3", [], EmploymentStatus.HALF_TIME_EVEN, 1)

        def firstHour = new Hour(1)
        def secondHour = new Hour(1)
        def table = new Table(1)
        def tutorFullSession = new TutoringSession(1, table, firstHour)
        tutorFullSession.setTutor(tutorFull)
        def tutorOddSession = new TutoringSession(2, table, firstHour)
        tutorOddSession.setTutor(tutorOdd)
        def tutorEvenSession = new TutoringSession(3, table, firstHour)
        tutorEvenSession.setTutor(tutorEven)
        def tutorSecondFullSession = new TutoringSession(4, table, secondHour)
        tutorEvenSession.setTutor(tutorFull)
        def tutorSecondEvenSession = new TutoringSession(5, table, secondHour)
        tutorEvenSession.setTutor(tutorEven)

        when:
        Schedule schedule = new Schedule(
                [tutorFull, tutorOdd, tutorEven],
                [],
                [tutorOddSession, tutorEvenSession, tutorFullSession, tutorSecondFullSession, tutorSecondEvenSession],
                [firstHour],
                [],
                [table],
        )


        then:
        scoreVerifier.assertHardWeight("Tutors should be free when scheduled", -1, schedule)
    }

    def "Tutors should only be scheduled for a single table"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(1)
        def table2 = new Table(2)

        def tutor = new Tutor(1, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def table1Session = new TutoringSession(1, table1, hour)
        table1Session.setTutor(tutor)
        def table2Session = new TutoringSession(2, table2, hour)
        table2Session.setTutor(tutor)

        when:
        Schedule schedule = new Schedule(
                [tutor],
                [],
                [table1Session, table2Session],
                [hour],
                [],
                [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Tutors should only be scheduled for a single table", -1, schedule)
    }

    def "Students should only be scheduled for a single session"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(1)
        def table2 = new Table(2)

        def student = new Student(1, "Test Student 1", [], [], 1)
        def table1Session = new TutoringSession(1, table1, hour)
        table1Session.setStudent(student)
        def table2Session = new TutoringSession(2, table2, hour)
        table2Session.setStudent(student)

        when:
        Schedule schedule = new Schedule(
                [],
                [student],
                [table1Session, table2Session],
                [hour],
                [],
                [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Students should only be scheduled for a single session", -1, schedule)
    }

    def "Students be scheduled for a session"() {
        given:
        def hour = new Hour(1)
        def table1 = new Table(1)
        def table2 = new Table(2)

        def student = new Student(1, "Test Student 1", [], [], 1)

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
        scoreVerifier.assertHardWeight("Students be scheduled for a session", -1, schedule)
    }

    def "Tutors should have at least 2 students"() {
        given:
        def hour1 = new Hour(1)
        def hour2 = new Hour(1)
        def table1 = new Table(1)
        def table2 = new Table(2)
        def tutor1 = new Tutor(1, "Test Tutor 1", [], EmploymentStatus.FULL_TIME, 1)
        def tutor2 = new Tutor(2, "Test Tutor 2", [], EmploymentStatus.FULL_TIME, 1)
        def tutor3 = new Tutor(3, "Test Tutor 3", [], EmploymentStatus.FULL_TIME, 1)

        def student1 = new Student(1, "Test Student 1", [], [], 1)
        def student2 = new Student(2, "Test Student 2", [], [], 1)

        def tableSessions = []
        tableSessions << new TutoringSession(id: 1, table: table1, hour: hour1, tutor: tutor1, student: student1)
        tableSessions << new TutoringSession(id: 2, table: table1, hour: hour2, tutor: tutor1, student: student2)
        tableSessions << new TutoringSession(id: 3, table: table2, hour: hour1, tutor: tutor2, student: student1)
        tableSessions << new TutoringSession(id: 4, table: table2, hour: hour1, tutor: tutor2, student: student2)
        tableSessions << new TutoringSession(id: 3, table: table2, hour: hour1, tutor: tutor3, student: student1)

        when:
        Schedule schedule = new Schedule(
                tutors: [tutor1, tutor2, tutor3 ],
                students: [student1, student2],
                sessions: tableSessions,
                hours: [hour1, hour2],
                specialties: [],
                tables: [table1, table2],
        )

        then:
        scoreVerifier.assertHardWeight("Tutors should have at least 2 students", -1, schedule)
    }

}
