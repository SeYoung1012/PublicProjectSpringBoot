package egovframework.com.sym.log.ulg.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.List;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz Scheduler를 실행하는 스케줄러 클래스를 정의한다.
 *
 * @author 김진만
 * @see
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2010.08.30   김진만     최초 생성
 * </pre>
 */

public class UserLogScheduler {

	private EgovUserLogSchdulService egovUserLogSchdulService;

	/** ID Generation */
	private EgovIdGnrService idgenService;

	/** Quartz 스케줄러 */
	private Scheduler userLogSched;

	private static final Logger LOGGER = LoggerFactory.getLogger(UserLogScheduler.class);

	// 실행 대상을 읽기위한 페이지 크기
	private static final int RECORD_COUNT_PER_PAGE = 10000;


	/**
	 * 클래스 초기화메소드.
	 */
	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		// 스케줄러 생성하기
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
		userLogSched = schedFact.getScheduler();

		// Set up the listener
		UserLogJobListener listener = new UserLogJobListener();

		listener.setEgovUserLogSchdulService(egovUserLogSchdulService);
		listener.setIdgenService(idgenService);

		//sched.addGlobalJobListener(listener);
		userLogSched.getListenerManager().addJobListener(listener);

		// 스케줄러에 Job, Trigger 등록하기

		JobDetail jobDetail = newJob(UserLogScriptJob.class).withIdentity("userLogScheduler").build();
		CronTrigger trigger = newTrigger().withIdentity("userLogScheduler").withSchedule(cronSchedule("00 00 0/1 * * ?")).forJob(jobDetail.getKey().getName()).build();


		userLogSched.scheduleJob(jobDetail, trigger);
		userLogSched.start();
	}

	/**
	 * 클래스 destroy메소드.
	 * Quartz 스케줄러를 shutdown한다.
	 *
	 */
	public void destroy() throws Exception {
		userLogSched.shutdown();
	}


	public EgovUserLogSchdulService getEgovBatchSchdulService() {
		return egovUserLogSchdulService;
	}


	public void setUserLogService(EgovUserLogSchdulService egovBatchSchdulService) {
		this.egovUserLogSchdulService = egovBatchSchdulService;
	}


	public EgovIdGnrService getIdgenService() {
		return idgenService;
	}


	public void setIdgenService(EgovIdGnrService idgenService) {
		this.idgenService = idgenService;
	}
}
