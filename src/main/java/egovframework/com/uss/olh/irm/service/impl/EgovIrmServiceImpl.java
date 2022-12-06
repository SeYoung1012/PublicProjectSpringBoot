package egovframework.com.uss.olh.irm.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.com.uss.olh.irm.service.EgovIrmService;
import egovframework.com.uss.olh.irm.service.IrmVO;

@Service("EgovIrmService")
public class EgovIrmServiceImpl extends EgovAbstractServiceImpl implements EgovIrmService {
	
	
	@Resource(name = "EgovIrmDAO")
	private EgovIrmDAO egovIrmDao;
	
	/** ID Generation */
	@Resource(name = "egovIrmManageIdGnrService")
	private EgovIdGnrService idgenService;
	
	
	@Override
	public List<?> selectIrmList(IrmVO searchVO) {
		return egovIrmDao.selectIrmList(searchVO);
	}

	@Override
	public int selectIrmListCnt(IrmVO searchVO) {
		return egovIrmDao.selectIrmListCnt(searchVO);
	}

	@Override
	public IrmVO selectIrmDetail(IrmVO irmVO) throws Exception {
		IrmVO resultVO = egovIrmDao.selectIrmDetail(irmVO);
		if(resultVO == null)
			throw processException("info.nodata.msg");
		return resultVO;
	}

	@Override
	public void insertIrm(IrmVO irmVO) throws FdlException {
		String srvcId = idgenService.getNextStringId();
		irmVO.setSrvcId(srvcId);
		egovIrmDao.insertIrm(irmVO);
	}

	@Override
	public void updateIrm(IrmVO irmVO) {
		egovIrmDao.updateIrm(irmVO);
		
	}

	@Override
	public void deleteIrm(IrmVO irmVO) {
		egovIrmDao.deleteIrm(irmVO);
		
	}

	@Override
	public List<?> selectIrmAnswerList(IrmVO searchVO) {
		return egovIrmDao.selectIrmAnswerList(searchVO);
	}

	@Override
	public int selectIrmAnswerListCnt(IrmVO searchVO) {
		
		return egovIrmDao.selectIrmAnswerListCnt(searchVO);
	}

	@Override
	public void updateIrmAnswer(IrmVO irmVO) {
		egovIrmDao.updateIrmAnswer(irmVO);
		
	}

	
	
	
	
	
}
