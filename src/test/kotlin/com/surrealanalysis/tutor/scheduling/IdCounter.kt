package com.surrealanalysis.tutor.scheduling

class IdCounter {
    var id: Int = 1
        get() = field++
}