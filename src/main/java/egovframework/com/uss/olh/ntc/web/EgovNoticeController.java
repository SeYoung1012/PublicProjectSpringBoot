package egovframework.com.uss.olh.ntc.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.olh.ntc.service.EgovNoticeService;
import egovframework.com.uss.olh.ntc.service.NoticeVO;
import egovframework.com.utl.fcc.service.EgovStringUtil;

/**
 *
 * NOTICE내용을 처리하는 비즈니스 구현 클래스
 * 
 * @author 디지털정부서비스 개방 플랫폼 구축 윤명석
 * @since 2022.10.26
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2022-10-26  윤명석          최초 생성
 *
 *      </pre>
 */

@RestController
public class EgovNoticeController {

    @Resource(name="EgovNoticeService")
    private EgovNoticeService egovNoticeService;

    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

    // 첨부파일 관련
    @Resource(name = "EgovFileMngService")
    private EgovFileMngService fileMngService;

    @Resource(name = "EgovFileMngUtil")
    private EgovFileMngUtil fileUtil;

    /** EgovMessageSource */
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    // Validation 관련
    @Autowired
    private DefaultBeanValidator beanValidator;

    /**
     * NOTICE 목록을 조회
     * 
     * @param searchVO
     * @return "ResultVO"
     * @throws Exception
    */

    @RequestMapping("/uss/olh/ntc/selectNoticeList.do")
    public ResultVO selectNoticeList(@ModelAttribute("searchVO") NoticeVO searchVO, ResultVO resultVO)
            throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        /** EgovPropertyService.SiteList */
        searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
        searchVO.setPageSize(propertiesService.getInt("pageSize"));

        /** pageing */
        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
        paginationInfo.setPageSize(searchVO.getPageSize());

        searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
        searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        List<?> resultList = egovNoticeService.selectNoticeList(searchVO);
        resultMap.put("resultList", resultList);
        int totCnt = egovNoticeService.selectNoticeListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totCnt);
        resultMap.put("paginationInfo", paginationInfo);
        resultVO.setResult(resultMap);

        return resultVO;
    }

    /**
     * NOTICE를 등록
     * 
     * @param searchVO
     * @param noticeVO
     * @param bindingResult
     * @param multiRequest
     * @return "ResultVO"
     * @throws Exception
     */
    @RequestMapping("/uss/olh/ntc/insertNotice.do")
    public ResultVO insertNoticeCn(final MultipartHttpServletRequest multiRequest, // 첨부파일을 위한...
            @ModelAttribute("searchVO") NoticeVO searchVO, @ModelAttribute("NoticeManageVO") NoticeVO noticeVO,
            BindingResult bindingResult, ResultVO resultVO) throws Exception {
        

        beanValidator.validate(noticeVO, bindingResult);

        if (bindingResult.hasErrors()) {
            resultVO.setResultCode(0);
            return resultVO;
        } else {
            // 첨부파일 관련 첨부파일ID 생성
            List<FileVO> result = null;
            String atchFileId = null;

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            // final List<MultipartFile> files = multiRequest.getFiles("atchFileId");
            if (!files.isEmpty()) {
                result = fileUtil.parseFileInf(files, "NTC_", 0, "", "");
                atchFileId = fileMngService.insertFileInfs(result); // 파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
            }

            // 리턴받은 첨부파일ID를 셋팅한다..
            noticeVO.setAtchFileId(atchFileId); // 첨부파일 ID
            // 로그인VO에서 사용자 정보 가져오기
            LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

            String frstRegisterId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());

            noticeVO.setFrstRegisterId(frstRegisterId); // 최초등록자ID
            noticeVO.setLastUpdusrId(frstRegisterId); // 최종수정자ID
            egovNoticeService.insertNotice(noticeVO);
            resultVO.setResultMessage(egovMessageSource.getMessage("success.common.insert"));
            resultVO.setResultCode(200);

            return resultVO;
        }
    }

    /**
     * NOTICE 목록에 대한 상세정보를 조회한다.
     * 
     * @param noticeVO
     * @param searchVO
     * @return "resultVO"
     * @throws Exception
     */
    @RequestMapping("/uss/olh/ntc/selectNoticeDetail.do")
    public ResultVO selectNoticeDetail(NoticeVO noticeVO, @ModelAttribute("searchVO") NoticeVO searchVO, ResultVO resultVO)
            throws Exception {

        NoticeVO vo = egovNoticeService.selectNoticeDetail(searchVO);
        System.out.println(vo);
        Map<String, Object> resultMap = new HashMap<>();
        // 첨부파일 확인
        if (vo != null && vo.getAtchFileId() != null && !vo.getAtchFileId().isEmpty()) {
            FileVO fileVO = new FileVO();
            fileVO.setAtchFileId(vo.getAtchFileId());
            List<FileVO> resultFiles = fileMngService.selectFileInfs(fileVO);
            resultMap.put("resultFiles", resultFiles);
        }
        resultMap.put("result", vo);
        resultVO.setResult(resultMap);
        return resultVO;
    }

    /**
     * NOTICE를 수정처리한다.
     * 
     * @param atchFileAt
     * @param multiRequest
     * @param searchVO
     * @param noticeVO
     * @param bindingResult
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping("/uss/olh/ntc/updateNotice.do")
    public ResultVO updateNotice(final MultipartHttpServletRequest multiRequest, @RequestParam("ntcId") String ntcId,
            @ModelAttribute("searchVO") NoticeVO searchVO, @ModelAttribute("noticeVO") NoticeVO noticeVO,
            BindingResult bindingResult, ResultVO resultVO)
            throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        System.out.println(noticeVO);
        // Validation
        beanValidator.validate(noticeVO, bindingResult);
        if (bindingResult.hasErrors()) {
            resultVO.setResultCode(0);
            return resultVO;
        } else {
            // Primary Key 값 세팅
            noticeVO.setNtcId(ntcId);

            // 변수명은 CoC 에 따라 JSTL사용을 위해
            resultMap.put("noticeVO", egovNoticeService.selectNoticeDetail(noticeVO));

            // 파일업로드 제한
            String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
            String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

            resultMap.put("fileUploadExtensions", whiteListFileUploadExtensions);
            resultMap.put("fileUploadMaxSize", fileUploadMaxSize);

            // 첨부파일 관련 ID 생성 start....
            String atchFileId = noticeVO.getAtchFileId();

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            // final List<MultipartFile> files = multiRequest.getFiles("file_1");
            if (!files.isEmpty()) {
                if (atchFileId == null || "".equals(atchFileId)) {
                    List<FileVO> result = fileUtil.parseFileInf(files, "NTC_", 0, atchFileId, "");
                    atchFileId = fileMngService.insertFileInfs(result);
                    noticeVO.setAtchFileId(atchFileId);
                } else {
                    FileVO fvo = new FileVO();
                    fvo.setAtchFileId(atchFileId);
                    int cnt = fileMngService.getMaxFileSN(fvo);
                    List<FileVO> _result = fileUtil.parseFileInf(files, "NTC_", cnt, atchFileId, "");
                    fileMngService.updateFileInfs(_result);
                }
            }
            // 첨부파일 관련 ID 생성 end...

            // 로그인VO에서 사용자 정보 가져오기
            LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
            String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
            noticeVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID

            egovNoticeService.updateNotice(noticeVO);
            resultVO.setResult(resultMap);
            resultVO.setResultCode(200);
            resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
            return resultVO;
        }
    }

    /**
     * NOTICE를 삭제처리한다.
     * 
     * @param noticeVO
     * @param searchVO
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping("/uss/olh/ntc/deleteNotice.do")
    public ResultVO deleteNotice(ResultVO resultVO, NoticeVO noticeVO, @ModelAttribute("searchVO") NoticeVO searchVO)
            throws Exception {

        // 첨부파일 삭제를 위한 ID 생성 start....
        String _atchFileId = noticeVO.getAtchFileId();

        egovNoticeService.deleteNotice(noticeVO);

        // 첨부파일을 삭제하기 위한 Vo
        FileVO fvo = new FileVO();
        fvo.setAtchFileId(_atchFileId);

        fileMngService.deleteAllFileInf(fvo);
        // 첨부파일 삭제 End.............
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.delete"));

        return resultVO;
    }
}
