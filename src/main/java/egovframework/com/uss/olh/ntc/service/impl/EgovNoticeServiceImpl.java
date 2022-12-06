package egovframework.com.uss.olh.ntc.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.egovframe.rte.fdl.cmmn.exception.FdlException;
import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.springframework.stereotype.Service;

import egovframework.com.uss.olh.ntc.service.EgovNoticeService;
import egovframework.com.uss.olh.ntc.service.NoticeVO;

/**
 * 공지사항 관리를 위한 서비스 구현 클래스
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
@Service("EgovNoticeService")
public class EgovNoticeServiceImpl extends EgovAbstractServiceImpl implements EgovNoticeService {
    
    @Resource(name = "EgovNoticeDAO")
    private EgovNoticeDAO egovNoticeDAO;

    /** ID Generation */
    @Resource(name = "egovNoticeManageIdGnrService")
    private EgovIdGnrService idgenService;

    @Override
    public List<?> selectNoticeList(NoticeVO searchVO) throws Exception {
        return egovNoticeDAO.selectNoticeList(searchVO);
    }

    @Override
    public int selectNoticeListCnt(NoticeVO searchVO) {
        return egovNoticeDAO.selectNoticeListCnt(searchVO);
    }

    @Override
    public void insertNotice(NoticeVO noticeVO) throws FdlException {
        String ntcId = idgenService.getNextStringId();
        noticeVO.setNtcId(ntcId);
        egovNoticeDAO.insertNotice(noticeVO);
    }

    @Override
    public NoticeVO selectNoticeDetail(NoticeVO searchVO) throws Exception{
        // 조회수 증가
        egovNoticeDAO.updateNoticeInqireCo(searchVO);

        NoticeVO resultVO = egovNoticeDAO.selectNoticeDetail(searchVO);
        if (resultVO == null)
            throw processException("info.nodata.msg");
        return resultVO;
    }

    @Override
    public void updateNotice(NoticeVO noticeVO) {
        egovNoticeDAO.updateNotice(noticeVO);
    }

    @Override
    public void deleteNotice(NoticeVO noticeVO) {
        egovNoticeDAO.deleteNotice(noticeVO);
    }
}
