package egovframework.com.sym.log.wlg.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebLogScheduler {
	
	
	private EgovWebLogSchdulService egovWebLogSchdulService;

	/** ID Generation */
	private EgovIdGnrService idgenService;

	/** Quartz 스케줄러 */
	private Scheduler webLogSched;

	private static final Logger LOGGER = LoggerFactory.getLogger(WebLogScheduler.class);

	// 실행 대상을 읽기위한 페이지 크기
	private static final int RECORD_COUNT_PER_PAGE = 10000;


	/**
	 * 클래스 초기화메소드.
	 */
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		// 스케줄러 생성하기
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		webLogSched = schedFact.getScheduler();

		// Set up the listener
		WebLogJobListener listener = new WebLogJobListener();

		listener.setEgovWebLogSchdulService(egovWebLogSchdulService);
		listener.setIdgenService(idgenService);

		//sched.addGlobalJobListener(listener);
		webLogSched.getListenerManager().addJobListener((JobListener) listener);

		// 스케줄러에 Job, Trigger 등록하기

		JobDetail jobDetail = newJob(WebLogScriptJob.class).withIdentity("webLogScheduler").build();
		CronTrigger trigger = newTrigger().withIdentity("webLogScheduler").withSchedule(cronSchedule("00 00 0/1 * * ?")).forJob(jobDetail.getKey().getName()).build();


		webLogSched.scheduleJob(jobDetail, trigger);
		webLogSched.start();
	}

	/**
	 * 클래스 destroy메소드.
	 * Quartz 스케줄러를 shutdown한다.
	 *
	 */
	public void destroy() throws Exception {
		webLogSched.shutdown();
	}


	public EgovWebLogSchdulService getEgovBatchSchdulService() {
		return egovWebLogSchdulService;
	}


	public void setWebLogService(EgovWebLogSchdulService egovBatchSchdulService) {
		this.egovWebLogSchdulService = egovBatchSchdulService;
	}


	public EgovIdGnrService getIdgenService() {
		return idgenService;
	}


	public void setIdgenService(EgovIdGnrService idgenService) {
		this.idgenService = idgenService;
	}
	

}
