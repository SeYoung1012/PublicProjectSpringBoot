package egovframework.com.uss.olh.ntc.service;

import java.util.List;

import org.egovframe.rte.fdl.cmmn.exception.FdlException;

/**
 * 공지사항 관리를 위한 서비스 인터페이스 클래스
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

public interface EgovNoticeService {

    List<?> selectNoticeList(NoticeVO searchVO) throws Exception;

    int selectNoticeListCnt(NoticeVO searchVO);

    void insertNotice(NoticeVO noticeVO) throws FdlException;

    NoticeVO selectNoticeDetail(NoticeVO searchVO) throws Exception;

    void updateNotice(NoticeVO noticeVO);

    void deleteNotice(NoticeVO noticeVO);

}
