package egovframework.com.sym.log.wlg.service;

import javax.annotation.Resource;


import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class WebLogJobListener implements JobListener{
	
	@Resource(name = "egovWebLogService")
	private EgovWebLogSchdulService egovWebLogSchdulService;

	/** ID Generation */
	private EgovIdGnrService idgenService;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(WebLogJobListener.class);


	public void setEgovWebLogSchdulService(EgovWebLogSchdulService egovBatchSchdulService) {
		this.egovWebLogSchdulService = egovBatchSchdulService;
	}


	public void setIdgenService(EgovIdGnrService idgenService) {
		this.idgenService = idgenService;
	}


	public String getName() {
		return this.getClass().getName();
	}
	@Override
	@Transactional
	public void jobWasExecuted(JobExecutionContext jobContext, JobExecutionException jee) {
		if(jobContext.getJobDetail().getKey().getName().equals("webLogScheduler")) {
			LOGGER.debug("job[{}] jobWasExecuted", jobContext.getJobDetail().getKey().getName());
			LOGGER.debug("job[{}] 수행시간 : {}, {}", jobContext.getJobDetail().getKey().getName(), jobContext.getFireTime(), jobContext.getJobRunTime());
			int jobResult = 99;

			JobDataMap dataMap = jobContext.getJobDetail().getJobDataMap();
			try {
				
				egovWebLogSchdulService.webLogSummary();


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
