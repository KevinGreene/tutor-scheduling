package com.surrealanalysis.tutor.scheduling.solver

import com.surrealanalysis.tutor.scheduling.domain.Tutor

class TutorStrengthComparator : Comparator<Tutor> {
    override fun compare(o1: Tutor?, o2: Tutor?): Int {
        return 1
    }

}