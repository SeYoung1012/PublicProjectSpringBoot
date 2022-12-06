package egovframework.com.uss.olh.irm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.olh.faq.service.FaqVO;
import egovframework.com.uss.olh.irm.service.EgovIrmService;
import egovframework.com.uss.olh.irm.service.IrmVO;
import egovframework.com.uss.olh.qna.service.QnaVO;
import egovframework.com.utl.fcc.service.EgovStringUtil;


/**
 * 서비스 개선 요청을 처리하는 비즈니스 구현 클래스
 *
 * @author 디지털정부서비스 개방 플랫폼 개발 김세영
 * @version 1.0
 * @see <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일                수정자           수정내용
 *  ----------   --------   ---------------------------
 * 2022-11-18   김세영			최초 생성
 * </pre>
 * @since 2022.11.18
 */


@RestController
public class EgovIrmControllerAPI {
		
	@Resource(name="EgovIrmService")
	private EgovIrmService egovIrmService;
	
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
	
    @Resource(name = "EgovCmmUseService")
	private EgovCmmUseService cmmUseService;
    
    
    /**
     * EgovMessageSource
     */
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    // Validation 관련
    @Autowired
    private DefaultBeanValidator beanValidator;
    
    
    
    /**
     * Irm 목록을 조회한다.
     *
     * @param searchVO
     * @throws Exception
     */
    
    @IncludedInfo(name = "Irm관리", listUrl = "/mp/cst/irm/SelectImproveRequestList.do", order = 540, gid = 50)
    @RequestMapping(value = "/mp/cst/irm/SelectImproveRequestList.do")  
    public ResultVO selectImproveRequestManage(@ModelAttribute IrmVO searchVO) throws Exception {
    	
    	ResultVO resultVO = new ResultVO();
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
        
        List<?> IrmList = egovIrmService.selectIrmList(searchVO);
        
        Map<String, Object> resultMap = new HashMap<>();
        
        resultMap.put("resutlList", IrmList);
        
        int totCnt = egovIrmService.selectIrmListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totCnt);
        resultMap.put("paginationInfo", paginationInfo);
        
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
       
        return resultVO;
            
    	
    }
    
    /**
     * 서비스 개선 요청 목록에 대한 상세정보를 조회한다.
     *
     * @param irmVO
     * @param searchVO
     * @throws Exception

     */
    @RequestMapping(value = "/mp/cst/irm/SelectImproveRequestDetail.do")
    public ResultVO selectImproveRequestManageDetail(@ModelAttribute("searchVO") IrmVO searchVO,
            IrmVO irmVO) throws Exception {
    	
    	ResultVO resultVO = new ResultVO();
    	
    	IrmVO vo = egovIrmService.selectIrmDetail(irmVO);
    	
    	Map<String, Object> resultMap = new HashMap<>();
    	
    	   if (vo != null && vo.getAtchFileId() != null && !vo.getAtchFileId().isEmpty()) {
   			FileVO fileVO = new FileVO();
   			fileVO.setAtchFileId(vo.getAtchFileId());
   			List<FileVO> resultFiles = fileMngService.selectFileInfs(fileVO);
   			System.out.println(resultFiles);
   			resultMap.put("resultFiles", resultFiles);
   		}

           resultMap.put("result", vo);
           resultVO.setResult(resultMap);
           
           return resultVO;
    	
    }
    

    /**
     * 서비스 개선요청을 등록한다.
     *
     * @param multiRequest
     * @param searchVO
     * @param irmVO
     * @param bindingResult
     * @throws Exception
     */
    @RequestMapping(value = "/mp/cst/irm/InsertImproveRequestView.do")
    public ResultVO insertImproveRequestManage (final MultipartHttpServletRequest multiRequest, // 첨부파일을 위한...
    									@ModelAttribute("searchVO") IrmVO searchVO, 
    									@ModelAttribute("irmManageVO") IrmVO irmVO,
    									BindingResult bindingResult,  ResultVO resultVO) throws Exception {
    	
    	 beanValidator.validate(irmVO, bindingResult);
         
         if (bindingResult.hasErrors()) {
         	 resultVO.setResultCode(0);
 			return resultVO;
         } else {
             // 첨부파일 관련 첨부파일ID 생성
             List<FileVO> _result = null;
             String _atchFileId = "";

             final Map<String, MultipartFile> files = multiRequest.getFileMap();
             // final List<MultipartFile> files = multiRequest.getFiles("atchFileId");
             if (!files.isEmpty()) {
             	 _result = fileUtil.parseFileInf(files, "FAQ_", 0, "", "");
                 _atchFileId = fileMngService.insertFileInfs(_result); // 파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
                 
                
             }
                  
 	        // 로그인VO에서  사용자 정보 가져오기
 	        LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
 	        
 	        String frstRegisterId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
 	
 	        //faqVO.setAtchFileId(_atchFileId);
 	        irmVO.setFrstRgtrId(frstRegisterId); // 최초등록자ID
 	        irmVO.setLastChnrgId(frstRegisterId); // 최종수정자ID
 	        irmVO.setAtchFileId(_atchFileId);  //setAtchFileId 값 
 	      
 	        egovIrmService.insertIrm(irmVO);
 	
 	        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.insert"));
            resultVO.setResultCode(200);
 	        
 	        return resultVO;
         }
    	
    
    }
    /**
     * 서비스 개선 요청 처리를 수정처리한다.
     *
     * @param atchFileAt
     * @param multiRequest
     * @param searchVO
     * @param irmVO
     * @param bindingResult
     * @param model
     * @throws Exception
     */
    
    @RequestMapping(value = "/mp/cst/irm/UpdateImproveRequestView.do")
    public ResultVO updateImproveRequestManage(final MultipartHttpServletRequest multiRequest, @RequestParam("srvcId") String srvcId,
            @ModelAttribute("searchVO") IrmVO searchVO, @ModelAttribute("irmVO") IrmVO irmVO, 
            BindingResult bindingResult, ResultVO resultVO) throws Exception{
		
    	 Map<String, Object> resultMap = new HashMap<>();
         // Validation
         beanValidator.validate(irmVO, bindingResult);
         if (bindingResult.hasErrors()) {
         	  resultVO.setResultCode(0);
               return resultVO;
         }else {
         	
        	 irmVO.setSrvcId(srvcId);
         	
         	// 변수명은 CoC 에 따라 JSTL사용을 위해
             resultMap.put("irmVO", egovIrmService.selectIrmDetail(irmVO));
             
             // 파일업로드 제한
             
             String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
             String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

             resultMap.put("fileUploadExtensions", whiteListFileUploadExtensions);
             resultMap.put("fileUploadMaxSize", fileUploadMaxSize);
 
             // 첨부파일 관련 ID 생성 start....
             String atchFileId = irmVO.getAtchFileId();
             
             //
             final Map<String, MultipartFile> files = multiRequest.getFileMap();
             //final List<MultipartFile> files = multiRequest.getFiles("file_1");
             if (!files.isEmpty()) {
                 if (atchFileId == null || "".equals(atchFileId)) {
                     List<FileVO> result1 = fileUtil.parseFileInf(files, "IRM_", 0, atchFileId, "");
                     atchFileId = fileMngService.insertFileInfs(result1);
                     irmVO.setAtchFileId(atchFileId);
                 } else {
                     FileVO fvo = new FileVO();
                     fvo.setAtchFileId(atchFileId);
                     int cnt = fileMngService.getMaxFileSN(fvo);
                     List<FileVO> _result = fileUtil.parseFileInf(files, "IRM_", cnt, atchFileId, "");
                     fileMngService.updateFileInfs(_result);
                 }
             }
             // 첨부파일 관련 ID 생성 end...
         	

             // 로그인VO에서  사용자 정보 가져오기
             LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
             String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
             irmVO.setLastChnrgId(lastUpdusrId); // 최종수정자ID
             irmVO.setAtchFileId(atchFileId);  //setAtchFileId 값 
            
             egovIrmService.updateIrm(irmVO);
             resultVO.setResult(resultMap);
             resultVO.setResultCode(200);
             resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
             return resultVO;
         	
         }	
    	
	}
    
    /**
     * 서비스 개선 요청을 삭제처리한다.
     *
     * @param irmVO
     * @param searchVO
     * @throws Exception
     */
    @RequestMapping(value = "/mp/cst/irm/DeleteImproveRequestManageView.do")
    public ResultVO deleteImproveRequestManage(IrmVO irmVO,ResultVO resultVO, @ModelAttribute("searchVO") IrmVO searchVO) throws Exception {

      
        // 첨부파일 삭제를 위한 ID 생성 start....
        String _atchFileId = irmVO.getAtchFileId();

        egovIrmService.deleteIrm(irmVO);
      
        // 첨부파일을 삭제하기 위한  Vo
        FileVO fvo = new FileVO();
        fvo.setAtchFileId(_atchFileId);

        fileMngService.deleteAllFileInf(fvo);
        
        // 첨부파일 삭제 End.............
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.delete"));
        
        return resultVO;
    }
    
    /**
	 * 서비스 개선 요청 답변정보 목록을 조회한다. (pageing)
	 * @param searchVO
	 * @param model
	 * @throws Exception
	 */
    @IncludedInfo(name = "Irm답변관리", listUrl = "/mp/cst/irm/SelectImproveRequesAnswertList.do", order = 540, gid = 50)
    @RequestMapping(value = "/mp/cst/irm/SelectImproveRequestAnswerList.do")  
	public ResultVO selectImproveRequestManageAnswerList(@ModelAttribute("searchVO") IrmVO searchVO) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		
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

		List<?> IrmAnswerList = egovIrmService.selectIrmAnswerList(searchVO);
		
		Map<String, Object> resultMap = new HashMap<>();
      
   
        resultMap.put("resultList", IrmAnswerList);

		int totCnt = egovIrmService.selectIrmAnswerListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		resultMap.put("paginationInfo", paginationInfo);
		
		 resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	     resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
	     resultVO.setResult(resultMap);
	        

		return resultVO;
	}
    
    /**
	 * 서비스 개선요청 답변정보 목록에 대한 상세정보를 조회한다.
	 * @param irmVO
	 * @param searchVO
	 * @throws Exception
	 */
    @RequestMapping(value = "/mp/cst/irm/SelectImproveRequestAnswerDetail.do")
	public ResultVO selectImproveRequestManageAnswerDetail(IrmVO irmVO, @ModelAttribute("searchVO") IrmVO searchVO) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		IrmVO vo = egovIrmService.selectIrmDetail(irmVO);		
		ComDefaultCodeVO codeVo = new ComDefaultCodeVO();
		codeVo.setCodeId("COM103");

		List<?> codeList = cmmUseService.selectCmmCodeDetail(codeVo);
		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("result", vo);
		resultMap.put("codeList", codeList);
		resultVO.setResult(resultMap);

		return resultVO;
	}

	/**
	 * 서비스 개선 요청 답변정보를 수정처리한다.
	 * @param irmVO
	 * @param searchVO
	 * @throws Exception
	 */
    @RequestMapping(value = "/mp/cst/irm/UpdateImproveRequestAnswerView.do")
	public ResultVO updateImproveRequestManageAnswer(IrmVO irmVO, @ModelAttribute("searchVO") IrmVO searchVO,ResultVO resultVO) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<>();
		// 로그인VO에서  사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
		String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
		irmVO.setLastChnrgId(lastUpdusrId); // 최종수정자ID

		egovIrmService.updateIrmAnswer(irmVO);
		resultVO.setResult(resultMap);
        resultVO.setResultCode(200);
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
		return resultVO;

	}
    
    
    
    

}
