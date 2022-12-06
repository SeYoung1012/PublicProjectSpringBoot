package egovframework.com.uss.olh.dat.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.com.uss.olh.dat.service.DataVO;
import egovframework.com.uss.olh.dat.service.EgovDataService;

@Service("EgovDataService")
public class EgovDataServiceImpl extends EgovAbstractServiceImpl implements EgovDataService {

	@Resource(name = "EgovDataDAO")
	private EgovDataDAO egovDataDao;

	/** ID Generation */
	@Resource(name = "egovDataManageIdGnrService")
	private EgovIdGnrService idgenService;
	
	@Override
	public List<?> selectDataList(DataVO searchVO) {
		return egovDataDao.selectDataList(searchVO);
	}

	@Override
	public int selectDataListCnt(DataVO searchVO) {
		return egovDataDao.selectDataListCnt(searchVO);
	}

	@Override
	public DataVO selectDataDetail(DataVO searchVO) throws Exception {
		
		//조회수 증가
		egovDataDao.updateDataInqireCo(searchVO);
		
		DataVO resultVO = egovDataDao.selectDataDetail(searchVO);
		if (resultVO == null)
			throw processException("info.nodata.msg");
		return resultVO;
	}

	@Override
	public void insertData(DataVO dataVO) throws FdlException {
		String dataId = idgenService.getNextStringId();
		dataVO.setDataId(dataId);
		egovDataDao.insertData(dataVO);
	}

	@Override
	public void updateData(DataVO dataVO) {
		egovDataDao.updateData(dataVO);
	}

	@Override
	public void deleteData(DataVO dataVO) {
		egovDataDao.deleteData(dataVO);
	}

}
