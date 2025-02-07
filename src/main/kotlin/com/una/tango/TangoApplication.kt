package com.una.tango

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TangoApplication

fun main(args: Array<String>) {
	runApplication<TangoApplication>(*args)
}
