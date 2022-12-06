package egovframework.com.uss.olh.irm.service;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.exception.FdlException;

public interface EgovIrmService {
	
	
	List<?> selectIrmList(IrmVO searchVO);
	
	int selectIrmListCnt(IrmVO searchVO);
	
	IrmVO selectIrmDetail(IrmVO irmVO) throws Exception;
	
	void insertIrm(IrmVO irmVO) throws FdlException;
	
	void updateIrm(IrmVO irmVO);
	
	void deleteIrm(IrmVO irmVO);
	
	List<?> selectIrmAnswerList(IrmVO searchVO);
	
	int selectIrmAnswerListCnt(IrmVO searchVO);
	
	void updateIrmAnswer(IrmVO irmVO);
	
	
	
	

}
