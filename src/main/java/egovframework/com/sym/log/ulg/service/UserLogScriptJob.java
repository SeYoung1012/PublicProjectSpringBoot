package egovframework.com.sym.log.ulg.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.Globals;

public class UserLogScriptJob implements Job {

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserLogScriptJob.class);

	@Resource(name = "egovNextUrlWhitelist")
    protected List<String> nextUrlWhitelist;
	
	
	
	/**
	 * (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {

		JobDataMap dataMap = jobContext.getJobDetail().getJobDataMap();

		LOGGER.debug("job[{}] Trigger이름 : ", jobContext.getJobDetail().getKey().getName(), jobContext.getTrigger().getKey().getName());
		//LOGGER.debug("job[{}] BatchOpert이름 : ", jobContext.getJobDetail().getKey().getName(), dataMap.getString("batchOpertId"));
		//LOGGER.debug("job[{}] BatchProgram이름 : ", jobContext.getJobDetail().getKey().getName(), dataMap.getString("batchProgrm"));
		//LOGGER.debug("job[{}] Parameter이름 : ", jobContext.getJobDetail().getKey().getName(), dataMap.getString("paramtr"));
		System.out.println("terstest");
		jobContext.setResult(1);
	}

}
