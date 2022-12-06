package egovframework.com.sym.log.wlg.service.impl;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

import egovframework.com.sym.log.wlg.service.EgovWebLogSchdulService;

@Service("EgovWebLogSchdulService")
public class EgovWebLogSchdulServiceImpl extends EgovAbstractServiceImpl implements
 EgovWebLogSchdulService{
	
	@Resource(name ="webLogDAO" )
	private WebLogDAO webLogDAO;

	@Override
	public void webLogSummary() throws Exception {
		
		webLogDAO.logInsertWebLogSummary();
		
	}
	
	
	
	
	

}
