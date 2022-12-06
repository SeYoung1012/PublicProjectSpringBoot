package egovframework.com.uss.olh.ntc.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import egovframework.com.cmm.service.impl.EgovComAbstractDAO;
import egovframework.com.uss.olh.ntc.service.NoticeVO;

/**
 * 공지사항 관리를 위한 데이터 접근 클래스
 * 
 * @author 디지털정부서비스 개방 플랫폼 구축 윤명석
 * @since 2022.10.26
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -------    --------    ---------------------------
 *  2022.10.26  윤명석          최초 생성
 *
 *      </pre>
 */

@Repository("EgovNoticeDAO")
public class EgovNoticeDAO extends EgovComAbstractDAO{

    public List<?> selectNoticeList(NoticeVO searchVO) {
        return selectList("NoticeManage.selectNoticeList",searchVO);
    }

    public int selectNoticeListCnt(NoticeVO searchVO) {
        return (Integer) selectOne("NoticeManage.selectNoticeListCnt", searchVO);
    }
    
    public void insertNotice(NoticeVO noticeVO) {
        insert("NoticeManage.insertNotice", noticeVO);
    }

    public void updateNoticeInqireCo(NoticeVO searchVO) {
        update("NoticeManage.updateNoticeInqireCo", searchVO);
    }

    public NoticeVO selectNoticeDetail(NoticeVO searchVO) {
        return (NoticeVO) selectOne("NoticeManage.selectNoticeDetail", searchVO);
    }

    public void updateNotice(NoticeVO noticeVO) {
        update("NoticeManage.updateNotice", noticeVO);
    }

    public void deleteNotice(NoticeVO noticeVO) {
        delete("NoticeManage.deleteNotice", noticeVO);
    }
}
