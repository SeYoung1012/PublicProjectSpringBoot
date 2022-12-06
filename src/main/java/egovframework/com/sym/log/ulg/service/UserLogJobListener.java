package egovframework.com.sym.log.ulg.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;



public class UserLogJobListener implements JobListener {

	@Resource(name = "egovUserLogService")
	private EgovUserLogSchdulService egovUserLogSchdulService;

	/** ID Generation */
	private EgovIdGnrService idgenService;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserLogJobListener.class);


	public void setEgovUserLogSchdulService(EgovUserLogSchdulService egovBatchSchdulService) {
		this.egovUserLogSchdulService = egovBatchSchdulService;
	}


	public void setIdgenService(EgovIdGnrService idgenService) {
		this.idgenService = idgenService;
	}


	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	@Transactional
	public void jobWasExecuted(JobExecutionContext jobContext, JobExecutionException jee) {
		if(jobContext.getJobDetail().getKey().getName().equals("userLogScheduler")) {
			LOGGER.debug("job[{}] jobWasExecuted", jobContext.getJobDetail().getKey().getName());
			LOGGER.debug("job[{}] 수행시간 : {}, {}", jobContext.getJobDetail().getKey().getName(), jobContext.getFireTime(), jobContext.getJobRunTime());
			int jobResult = 99;

			JobDataMap dataMap = jobContext.getJobDetail().getJobDataMap();
			try {
				
				egovUserLogSchdulService.logInsertUserLog();


			} catch (ClassCastException e) {//KISA 보안약점 조치 (2018-10-29, 윤창원)

			} catch (Exception e) {

			}
		}
		
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}



}
