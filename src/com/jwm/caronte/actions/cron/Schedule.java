package com.jwm.caronte.actions.cron;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.jwm.caronte.cron.CaronteJob;

public class Schedule extends GenericAction {
	
	@Override
	public String execute() throws CommandException {

		for(Entry<String, String[]> e : request().getParameterMap().entrySet()){
			System.out.println(e.getKey() + " = "+Arrays.asList(e.getValue()));
		}
		
		schedule(getExpression(), 
				param("searchbox"), 
				StringUtils.join(Arrays.asList(request().getParameterValues("server")),";"),
				param("searchview"),
				StringUtils.join(Arrays.asList(request().getParameterValues("contentDist")),";"));
		return null;
	}
	
	private String
	getExpression(){
		String expr = "0 ";
		String[] time = param("startfrom").split(":");
		String cronType = param("crontype");
		if (cronType.equals("d")) {
			expr = expr + time[1] + " " + time[0] + " ? * " + param("days");
		} else {
			if (param("freqtype").equals("m")) {
				expr = expr + time[1] + "/" + param("freq") + " " + time[0]
				                                                         + " * * ?";
			} else {
				expr = expr + time[1] + " " + time[0] + "/" + param("freq")
				+ " * * ?";
			}
		}
		return expr;
	}

	public void schedule(String expr, String query, String searchServer, String searchview, String contentDist) {
		File f = new File(application().getRealPath(application().getInitParameter("cronfile")));
		Properties p = new Properties();
		if(f.exists()){
			try {
				p.load(new FileInputStream(f));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!p.containsKey("server."+expr+searchServer+searchview)){
			p.setProperty("server."+expr+searchServer+searchview, 
					"expr=>"+expr+"|pbSeppb|query=>"+query+"|pbSeppb|searchServer=>"+searchServer+"|pbSeppb|searchview=>"+searchview+"|pbSeppb|contentDist=>"+contentDist);
			p.setProperty(expr+searchServer+searchview, "running");
			try {
				p.store(new FileOutputStream(f, false), "added new scheduler "+(expr+searchServer+searchview));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

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
}