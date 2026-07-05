package com.bmad.expensetracker;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpenseTrackerApiApplication {

	static {
		// AD-9: Asia/Kolkata is the single clock authority - pinned explicitly here rather than
		// trusting the host's default. Some JVM/OS combinations report the deprecated IANA alias
		// "Asia/Calcutta" for this zone, which newer PostgreSQL tzdata builds reject outright at
		// connection time ("FATAL: invalid value for parameter \"TimeZone\""). A static initializer
		// (rather than setting this in main()) guarantees it also applies when Spring Boot's test
		// support loads this class as the configuration source without ever calling main() - see
		// also the surefire argLine in pom.xml, which belt-and-braces the same fix for test JVMs.
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApiApplication.class, args);
	}

}
