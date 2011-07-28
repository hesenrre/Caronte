package com.jwm.caronte.servlet;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.jwm.caronte.cron.CaronteJob;

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
		super.init(config);
		File f = new File(config.getServletContext().getRealPath(config.getServletContext().getInitParameter("cronfile")));
		Properties p = new Properties();		
		if(f.exists()){		
			try {
				p.load(new FileInputStream(f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(Enumeration<Object> e = p.keys(); e.hasMoreElements(); ){
			String prop = (String) e.nextElement();			
			if(prop.startsWith("server.")){
				Map<String, String> data = process(p.getProperty(prop));
				schedule(data.get("expr"), data.get("query"),
						data.get("searchServer"), data.get("searchview"),
						data.get("contentDist"));
			}
		}
				
	}

	private Map<String, String> process(final String val) {
		return new HashMap<String, String>(){{
			for(String token : val.split("\\|pbSeppb\\|")){
				String kv[] = token.split("\\s*=>\\s*");
				put(kv[0], kv[1]);
			}
		}};
	}

	public void schedule(String expr, String query, String searchServer, String searchview, String contentDist) {
		logger.info("creating scheduler with: ");
		logger.info("\texpr: "+expr);
		logger.info("\tquery: "+query);
		logger.info("\tsearchServer: "+searchServer);
		logger.info("\tsearchView: "+searchview);
		logger.info("\tcontentDist: "+contentDist);
		try {
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
			JobDetail job = newJob(CaronteJob.class)
			.withIdentity(expr+searchServer+searchview,"group1")
			.usingJobData("query", query)
			.usingJobData("searchServer", searchServer)
			.usingJobData("searchview", searchview)
			.usingJobData("contentDist", contentDist)
			.build();

			Trigger trigger = newTrigger()
			.withIdentity(expr+searchServer+searchview, "group1")
			.withSchedule(cronSchedule(expr))
			.forJob(expr+searchServer+searchview, "group1")
			.build();

			scheduler.scheduleJob(job, trigger);

		} catch (Exception e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		}		
	}

}
