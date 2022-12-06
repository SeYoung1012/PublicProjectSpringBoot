package egovframework.com.uss.olh.irm.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import egovframework.com.uss.olh.irm.service.IrmVO;

@Repository("EgovIrmDAO")
public class EgovIrmDAO extends EgovComAbstractDAO{
	
	public List<?> selectIrmList (IrmVO searchVO){
		return list("IrmManage.selectIrmList", searchVO);
	}
	
	public int selectIrmListCnt(IrmVO searchVO) {
		return (Integer)selectOne("IrmManage.selectIrmListCnt", searchVO);
	}
	
	public IrmVO selectIrmDetail(IrmVO irmVO) {
		return (IrmVO) selectOne("IrmManage.selectIrmDetail", irmVO);
	}
	
	public void insertIrm(IrmVO irmVO) {
		insert("IrmManage.insertIrm", irmVO);
		
	}
	
	public void updateIrm(IrmVO irmVO) {
		update("IrmManage.updateIrm", irmVO);
	}
	
	public void deleteIrm(IrmVO irmVO) {
		delete("IrmManage.deleteIrm", irmVO);
	}
	
	public List<?> selectIrmAnswerList(IrmVO searchVO){
		return list("IrmManage.selectIrmAnswerList", searchVO);
	}
	
	public int selectIrmAnswerListCnt (IrmVO searchVO) {
		return (Integer)selectOne("IrmManage.selectIrmAnswerListCnt", searchVO);
	}
	
	public void updateIrmAnswer(IrmVO irmVO) {
		update("IrmManage.updateIrmAnswer", irmVO);
	}
	
	
	
}
