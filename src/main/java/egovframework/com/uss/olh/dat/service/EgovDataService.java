package egovframework.com.uss.olh.dat.service;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.exception.FdlException;

public interface EgovDataService {

	List<?> selectDataList(DataVO searchVO);

	int selectDataListCnt(DataVO searchVO);

	DataVO selectDataDetail(DataVO searchVO) throws Exception;

	void insertData(DataVO dataVO) throws FdlException;

	void updateData(DataVO dataVO);

	void deleteData(DataVO dataVO);

}
