package egovframework.com.config;

import java.util.Collections;
import java.util.HashMap;

import javax.sql.DataSource;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import egovframework.com.sym.bat.service.BatchScheduler;
import egovframework.com.sym.bat.service.EgovBatchSchdulService;
import egovframework.com.sym.log.lgm.service.EgovSysLogAspect;
import egovframework.com.sym.log.lgm.service.EgovSysLogService;
import egovframework.com.sym.log.ulg.service.EgovUserLogSchdulService;
import egovframework.com.sym.log.ulg.service.EgovUserLogService;
import egovframework.com.sym.log.ulg.service.UserLogScheduler;
import egovframework.com.sym.log.wlg.service.EgovWebLogSchdulService;
import egovframework.com.sym.log.wlg.service.EgovWebLogScheduling;
import egovframework.com.sym.log.wlg.service.EgovWebLogService;
import egovframework.com.sym.log.wlg.service.WebLogScheduler;


/**
 * @ClassName : EgovConfigAppTransaction.java
 * @Description : Transaction 설정
 *
 * @author : 윤주호
 * @since  : 2021. 7. 20
 * @version : 1.0
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일              수정자               수정내용
 *  -------------  ------------   ---------------------
 *   2021. 7. 20    윤주호               최초 생성
 * </pre>
 *
 */
@Configuration
public class EgovConfigAppSchduler {

	@Autowired
	EgovBatchSchdulService egovBatchSchdulService;
	
	@Autowired
	EgovIdGnrService egovBatchResultIdGnrService;
	
	@Autowired
	EgovUserLogSchdulService egovUserLogService;
	
	@Autowired
	EgovWebLogSchdulService egovWebLogService;

	@Bean(initMethod ="init", destroyMethod = "destroy")
	public BatchScheduler batchScheduler() {
		BatchScheduler batchScheduler = new BatchScheduler();
		batchScheduler.setEgovBatchSchdulService(egovBatchSchdulService);
		batchScheduler.setIdgenService(egovBatchResultIdGnrService);
		return batchScheduler;
	}
	
	@Bean(initMethod ="init", destroyMethod = "destroy")
	public UserLogScheduler userLogScheduler() {
		UserLogScheduler userLogScheduler = new UserLogScheduler();
		userLogScheduler.setUserLogService(egovUserLogService);
		userLogScheduler.setIdgenService(egovBatchResultIdGnrService);
		return userLogScheduler;
	}
	
	@Bean(initMethod ="init", destroyMethod = "destroy")
	public WebLogScheduler webLogScheduler()  {
		WebLogScheduler webLogScheduler = new WebLogScheduler();
		webLogScheduler.setWebLogService(egovWebLogService);
		webLogScheduler.setIdgenService(egovBatchResultIdGnrService);
		return webLogScheduler;
		
		
	}
	
	
	
	
}
