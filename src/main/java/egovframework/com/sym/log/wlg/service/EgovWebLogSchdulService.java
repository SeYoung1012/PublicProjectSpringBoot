package egovframework.com.sym.log.wlg.service;

public interface EgovWebLogSchdulService {
	
	/**
	 * 웹 로그정보를 요약한다.
	 * 전날의 로그를 요약하여 입력하고, 6개월전의 로그를 삭제한다.
	 * @param
	 */
	public void webLogSummary() throws Exception;
	

}
