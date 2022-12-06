package egovframework.com.uss.olh.faq.web;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.olh.faq.service.EgovFaqService;
import egovframework.com.uss.olh.faq.service.FaqVO;
import egovframework.com.utl.fcc.service.EgovStringUtil;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FAQ내용을 처리하는 비즈니스 구현 클래스
 *
 * @author 공통서비스 개발팀 박정규
 * @version 1.0
 * @see <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자           수정내용
 *  ----------   --------   ---------------------------
 *  2009.04.01   박정규            최초 생성
 *  2011.08.26   정진오            IncludedInfo annotation 추가
 *  2016.08.03   김연호            표준프레임워크 3.6 개선
 *  2020.10.27   신용호            파일 업로드 수정 (multiRequest.getFiles)
 *  2021.07.29   정진호            경로 오류 수정
 *
 * </pre>
 * @since 2009.04.01
 */

@Controller
public class EgovFaqController {

    @Resource(name = "EgovFaqService")
    private EgovFaqService egovFaqService;

    /**
     * EgovPropertyService
     */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

    // 첨부파일 관련
    @Resource(name = "EgovFileMngService")
    private EgovFileMngService fileMngService;

    @Resource(name = "EgovFileMngUtil")
    private EgovFileMngUtil fileUtil;

    /**
     * EgovMessageSource
     */
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    // Validation 관련
    @Autowired
    private DefaultBeanValidator beanValidator;

    /**
     * FAQ 목록을 조회한다.
     *
     * @param searchVO
     * @param model
     * @throws Exception
     * @return    "/uss/olh/faq/EgovFaqList"
     */
    @IncludedInfo(name = "FAQ관리", listUrl = "/uss/olh/faq/selectFaqList.do", order = 540, gid = 50)
    @RequestMapping(value = "/uss/olh/faq/selectFaqList.do")
    @ResponseBody
    public HashMap<String, Object> selectFaqList(@RequestBody FaqVO searchVO, ModelMap model) throws Exception {

        /** EgovPropertyService.SiteList */
        HashMap<String, Object> result = new HashMap<>();
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

        List<?> FaqList = egovFaqService.selectFaqList(searchVO);
        result.put("resultList", FaqList);
        
        int totCnt = egovFaqService.selectFaqListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totCnt);
        result.put("paginationInfo", paginationInfo);

        return result;
    }

    /**
     * FAQ 목록에 대한 상세정보를 조회한다.
     *
     * @param faqVO
     * @param searchVO
     * @param model
     * @throws Exception
     * @return    "/uss/olh/faq/EgovFaqDetail"
     */
    @RequestMapping(value = "/uss/olh/faq/selectFaqDetail.do")
    @ResponseBody
    public HashMap<String, Object> selectFaqDetail(@ModelAttribute("searchVO") FaqVO searchVO,
                                                   @RequestBody FaqVO faqVO) throws Exception {
        HashMap<String, Object> result = new HashMap<>();
        FaqVO vo = egovFaqService.selectFaqDetail(faqVO);
        if (vo != null && vo.getAtchFileId() != null && !vo.getAtchFileId().isEmpty()) {
			FileVO fileVO = new FileVO();
			fileVO.setAtchFileId(vo.getAtchFileId());
			List<FileVO> resultFiles = fileMngService.selectFileInfs(fileVO);
			System.out.println(resultFiles);
			result.put("resultFiles", resultFiles);
		}

        result.put("result", vo);
        return result;
    }

    /**
     * FAQ를 등록하기 위한 전 처리
     *
     * @param searchVO
     * @param model
     * @throws Exception
     * @return    "/uss/olh/faq/EgovFaqRegist"
     */
    @RequestMapping(value = "/uss/olh/faq/insertFaqView.do")
    @ResponseBody
    public HashMap<String, Object> insertFaqView(@ModelAttribute("searchVO") FaqVO searchVO,
                                                 @ModelAttribute FaqVO faqVO, Model model) throws Exception {

        HashMap<String, Object> result = new HashMap<>();

        model.addAttribute("faqVO", new FaqVO());
        result.put("faqVO", faqVO);
        // 파일업로드 제한
        String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
        String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

        model.addAttribute("fileUploadExtensions", whiteListFileUploadExtensions);
        model.addAttribute("fileUploadMaxSize", fileUploadMaxSize);

        result.put("fileUploadExtensions", whiteListFileUploadExtensions);
        result.put("fileUploadMaxSize", fileUploadMaxSize);
        return result;

    }

    /**
     * FAQ를 등록한다.
     *
     * @param multiRequest
     * @param searchVO
     * @param faqVO
     * @param bindingResult
     * @throws Exception
     * @return    "forward:/uss/olh/faq/selectFaqList.do"
     */
    @RequestMapping(value = "/uss/olh/faq/insertFaq.do")
    @ResponseBody
    public HashMap<String, Object> insertFaqCn(final MultipartHttpServletRequest multiRequest, // 첨부파일을 위한...
                                               @ModelAttribute("searchVO") FaqVO searchVO, @ModelAttribute("faqManageVO") FaqVO faqVO,
                                               BindingResult bindingResult) throws Exception {

        HashMap<String, Object> result = new HashMap<>();
        //Map<String, Object> result = new HashMap<String, Object>();
        beanValidator.validate(faqVO, bindingResult);
        
        if (bindingResult.hasErrors()) {
            
			return result;
        } else {
            // 첨부파일 관련 첨부파일ID 생성
            List<FileVO> _result = null;
            String _atchFileId = "";

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            // final List<MultipartFile> files = multiRequest.getFiles("atchFileId");
            if (!files.isEmpty()) {
            	 _result = fileUtil.parseFileInf(files, "FAQ_", 0, "", "");
                _atchFileId = fileMngService.insertFileInfs(_result); // 파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
                
                System.out.println("if문안=============================_atchFileId::::::::::::" +_atchFileId );
            }
            System.out.println("result=========" +  _result);
            System.out.println("atchFileId============="+ _atchFileId );

        
	        // 로그인VO에서  사용자 정보 가져오기
	        LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
	
	        String frstRegisterId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
	
	        //faqVO.setAtchFileId(_atchFileId);
	        faqVO.setFrstRegisterId(frstRegisterId); // 최초등록자ID
	        faqVO.setLastUpdusrId(frstRegisterId); // 최종수정자ID
	        faqVO.setAtchFileId(_atchFileId);  //setAtchFileId 값 
	        System.out.println("=============================egovFaqService.insertFaq(faqVO)1111111111" + faqVO);
	        egovFaqService.insertFaq(faqVO);
	
	        //System.out.println("=============================egovFaqService.insertFaq(faqVO)" + faqVO);
	
	        
	        result.put("resultCode", "200");
	        return result;
       
        }
        
       

    }

    /**
     * FAQ를 수정하기 위한 전 처리
     *
     * @param faqId
     * @param searchVO
     * @param model
     * @throws Exception
     * @return    "/uss/olh/faq/EgovFaqUpdt"
     */
    @RequestMapping(value = "/uss/olh/faq/updateFaqView.do")
    @ResponseBody
    public HashMap<String, Object> updateFaqView(@RequestParam("faqId") String faqId, @ModelAttribute("searchVO") FaqVO searchVO, ModelMap model) throws Exception {

        HashMap<String, Object> result = new HashMap<>();
        FaqVO faqVO = new FaqVO();

        // Primary Key 값 세팅
        faqVO.setFaqId(faqId);

        // 변수명은 CoC 에 따라 JSTL사용을 위해
        model.addAttribute("faqVO", egovFaqService.selectFaqDetail(faqVO));

        // 파일업로드 제한
        String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
        String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

        model.addAttribute("fileUploadExtensions", whiteListFileUploadExtensions);
        model.addAttribute("fileUploadMaxSize", fileUploadMaxSize);

        return result;
    }

    /**
     * FAQ를 수정처리한다.
     *
     * @param atchFileAt
     * @param multiRequest
     * @param searchVO
     * @param faqVO
     * @param bindingResult
     * @param model
     * @throws Exception
     * @return    "forward:/uss/olh/faq/selectFaqList.do"
     */
    @RequestMapping(value = "/uss/olh/faq/updateFaq.do")
    @ResponseBody
    public HashMap<String, Object> updateFaqCn(final MultipartHttpServletRequest multiRequest,
                                               @ModelAttribute("searchVO") FaqVO searchVO, @ModelAttribute("faqVO") FaqVO faqVO, BindingResult bindingResult, ModelMap model)
            throws Exception {
        System.out.println("--------------------faqVO" + faqVO);
        
        
        HashMap<String, Object> result = new HashMap<>();
        // Validation
        beanValidator.validate(faqVO, bindingResult);
        if (bindingResult.hasErrors()) {
            result.put("resultMsg", "403");
            return result;
        }


        // 첨부파일 관련 ID 생성 start....
        String atchFileId = faqVO.getAtchFileId();

        final Map<String, MultipartFile> files = multiRequest.getFileMap();
        //final List<MultipartFile> files = multiRequest.getFiles("file_1");
        if (!files.isEmpty()) {
            if (atchFileId == null || "".equals(atchFileId)) {
                List<FileVO> result1 = fileUtil.parseFileInf(files, "FAQ_", 0, atchFileId, "");
                atchFileId = fileMngService.insertFileInfs(result1);
                faqVO.setAtchFileId(atchFileId);
            } else {
                FileVO fvo = new FileVO();
                fvo.setAtchFileId(atchFileId);
                int cnt = fileMngService.getMaxFileSN(fvo);
                List<FileVO> _result = fileUtil.parseFileInf(files, "FAQ_", cnt, atchFileId, "");
                fileMngService.updateFileInfs(_result);
            }
        }
        // 첨부파일 관련 ID 생성 end...

        // 로그인VO에서  사용자 정보 가져오기
        LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
        String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
        faqVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID
        faqVO.setAtchFileId(atchFileId);  //setAtchFileId 값 
        System.out.println("=============================egovFaqService.insertFaq(faqVO)1111111111" + faqVO);
        egovFaqService.updateFaq(faqVO);
        result.put("resultMsg", "200");
        System.out.println("===================수정=======" + faqVO);

        return result;

    }

    /**
     * FAQ를 삭제처리한다.
     *
     * @param faqVO
     * @param searchVO
     * @throws Exception
     * @return    "forward:/uss/olh/faq/selectFaqList.do"
     */
    @RequestMapping(value = "/uss/olh/faq/deleteFaq.do")
    @ResponseBody
    public HashMap<String, Object> deleteFaq(@RequestBody FaqVO faqVO, @ModelAttribute("searchVO") FaqVO searchVO) throws Exception {

        HashMap<String, Object> result = new HashMap<>();
        // 첨부파일 삭제를 위한 ID 생성 start....
        String _atchFileId = faqVO.getAtchFileId();

        egovFaqService.deleteFaq(faqVO);
        result.put("resultMsg", _atchFileId);

        // 첨부파일을 삭제하기 위한  Vo
        FileVO fvo = new FileVO();
        fvo.setAtchFileId(_atchFileId);

        fileMngService.deleteAllFileInf(fvo);
        result.put("fileDelete", fvo);
        // 첨부파일 삭제 End.............

        return result;
    }

}
