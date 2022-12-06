package egovframework.com.uss.olh.dat.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import egovframework.com.uss.olh.dat.service.DataVO;

@Repository("EgovDataDAO")
public class EgovDataDAO extends EgovComAbstractDAO {

	public List<?> selectDataList(DataVO searchVO) {
		return selectList("DataManage.selectDataList", searchVO);
	}

	public int selectDataListCnt(DataVO searchVO) {
		return (Integer) selectOne("DataManage.selectDataListCnt", searchVO);
	}

	public void updateDataInqireCo(DataVO searchVO) {
		update("DataManage.updateDataInqireCo", searchVO);
	}

	public DataVO selectDataDetail(DataVO searchVO) {
		return (DataVO) selectOne("DataManage.selectDataDetail", searchVO);
	}

	public void insertData(DataVO dataVO) {
		insert("DataManage.insertData", dataVO);
	}

	public void updateData(DataVO dataVO) {
		update("DataManage.updateData", dataVO);
	}

	public void deleteData(DataVO dataVO) {
		delete("DataManage.deleteData", dataVO);
	}

}
