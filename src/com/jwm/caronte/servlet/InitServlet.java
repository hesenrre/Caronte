package com.jwm.caronte.servlet;

import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class InitServlet extends HttpServlet {
	
	private static final String LOG4J_PATTERN = "[%d{HH:mm:ss}] %-6p %-15c{1} %x - %m%n";
	// Default log4j configuration
	Properties log4jProps = new Properties();
	private Logger logger;
	{
		log4jProps.setProperty("log4j.rootLogger", "INFO, A1");
		log4jProps.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
		log4jProps.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
		log4jProps.setProperty("log4j.appender.A1.layout.ConversionPattern", LOG4J_PATTERN);
		log4jProps.setProperty("log4j.logger.org.lainsoft", "ALL");
	}

	/**
	 * Initialize log4j logger with default parameters:
	 * Level - INFO
	 * Console Appender
	 * Pattern Layout - [%d{HH:mm:ss}] %-6p %-15c{1} %x - %m%n
	 */
	private void initLog4j() {
		PropertyConfigurator.configure(log4jProps);
		logger = Logger.getLogger(InitServlet.class);
	}

	
	@Override
	public void init(ServletConfig config) throws ServletException {
		initLog4j();
		System.out.println("Starting Up Caronte Config");
		logger.debug("Starting up caronte configuration");
		super.init(config);		
	}

}
