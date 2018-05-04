package cc.aoeiuv020.panovel.sql

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*

/**
 * Created by AoEiuV020 on 2018.04.26-10:32:17.
 */
object SqlTestUtil : AnkoLogger {
    val timeWatcher = object : TestWatcher() {
        var start = 0L
        override fun starting(description: Description) {
            info { "Starting test: " + description.methodName }
            start = System.currentTimeMillis()
        }

        override fun finished(description: Description) {
            val end = System.currentTimeMillis()
            info { "Test " + description.methodName + " took " + (end - start) + "ms" }
        }
    }

    private val random = Random()
    private var number: Int = 0

}