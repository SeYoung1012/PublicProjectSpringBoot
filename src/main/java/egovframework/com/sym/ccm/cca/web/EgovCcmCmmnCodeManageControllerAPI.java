package egovframework.com.sym.ccm.cca.web;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.sym.ccm.cca.service.CmmnCode;
import egovframework.com.sym.ccm.cca.service.CmmnCodeVO;
import egovframework.com.sym.ccm.cca.service.EgovCcmCmmnCodeManageService;
import egovframework.com.sym.ccm.ccc.service.CmmnClCodeVO;
import egovframework.com.sym.ccm.ccc.service.EgovCcmCmmnClCodeManageService;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

/**
*
* 공통코드에 관한 요청을 받아 서비스 클래스로 요청을 전달하고 서비스클래스에서 처리한 결과를 웹 화면으로 전달을 위한 Controller를 정의한다
* @author 공통서비스 개발팀 이중호
* @since 2009.04.01
* @version 1.0
* @see
*
* <pre>
* << 개정이력(Modification Information) >>
*
*   수정일      수정자           수정내용
*  -------    --------    ---------------------------
*   2017.08.16	이정은	표준프레임워크 v3.7 개선
*   2022=10-28  김진욱    API컨트롤러로 변경
*
* </pre>
*/

@Controller
@RestController
public class EgovCcmCmmnCodeManageControllerAPI {
	
	@Resource(name = "CmmnCodeManageService")
    private EgovCcmCmmnCodeManageService cmmnCodeManageService;

	@Resource(name = "CmmnClCodeManageService")
    private EgovCcmCmmnClCodeManageService cmmnClCodeManageService;
	
	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
	/** EgovMessageSource */
	@Resource(name = "egovMessageSource")
	EgovMessageSource egovMessageSource;
	
	@Autowired
	private DefaultBeanValidator beanValidator;
	
	
	
	/**
	 * 공통분류코드 목록을 조회한다.
	 * 
	 * @param searchVO
	 * @param model
	 * @return "egovframework/com/sym/ccm/cca/EgovCcmCmmnCodeList"
	 * @throws Exception
	 */
	@IncludedInfo(name = "공통코드", listUrl = "/sym/ccm/cca/SelectCcmCmmnCodeListAPI.do", order = 980, gid = 60)
	@RequestMapping(value = "/sym/ccm/cca/SelectCcmCmmnCodeListAPI.do")
	public ResultVO selectCmmnCodeList(CmmnCodeVO searchVO)
			throws Exception {

		ResultVO resultVO = new ResultVO();
		HashMap<String, Object> result = new HashMap<>();
		
		searchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		searchVO.setPageSize(propertiesService.getInt("pageSize"));
	

		/** pageing */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		System.out.println(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(6);
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> CmmnCodeList = cmmnCodeManageService.selectCmmnCodeList(searchVO);
		result.put("resultList", CmmnCodeList);
		
		int totCnt = cmmnCodeManageService.selectCmmnCodeListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		result.put("paginationInfo", paginationInfo);
		
		resultVO.setResult(result);

		return resultVO;
	}
	
	/**
	 * 공통코드 상세항목을 조회한다.
	 * 
	 * @param loginVO
	 * @param cmmnCodeVO
	 * @param model
	 * @return "egovframework/com/sym/ccm/cca/EgovCcmCmmnCodeDetail"
	 * @throws Exception
	 */
	@RequestMapping(value = "/sym/ccm/cca/SelectCcmCmmnCodeDetailAPI.do")
	public ResultVO selectCmmnCodeDetail(String codeId) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String,Object> result = new HashMap<>();
		codeId = codeId.replace("\"", "");
		CmmnCodeVO cmmnCodeVO = new CmmnCodeVO();
		cmmnCodeVO.setCodeId(codeId);
		cmmnCodeVO.setClCode("EFC");
		CmmnCodeVO vo = cmmnCodeManageService.selectCmmnCodeDetail(cmmnCodeVO);
		
		result.put("result", vo);
		resultVO.setResult(result);

		return resultVO;
	}
	
	/**
	 * 공통코드 등록을 위한 등록페이지로 이동한다.
	 * 
	 * @param cmmnCodeVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/sym/ccm/cca/RegistCcmCmmnCodeView.do")
	public String insertCmmnCodeView(@ModelAttribute("cmmnCodeVO")CmmnCodeVO cmmnCodeVO, ModelMap model) throws Exception {
		
		CmmnClCodeVO searchVO = new CmmnClCodeVO();
		searchVO.setFirstIndex(0);
        List<?> CmmnCodeList = cmmnClCodeManageService.selectCmmnClCodeList(searchVO);
        
        model.addAttribute("clCodeList", CmmnCodeList);

		return "egovframework/com/sym/ccm/cca/EgovCcmCmmnCodeRegist";
	}
	
	/**
     * 공통코드를 등록한다.
     * 
     * @param CmmnCodeVO
     * @param CmmnCodeVO
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/sym/ccm/cca/RegistCcmCmmnCodeAPI.do")
    public ResultVO insertCmmnCode(@RequestBody CmmnCodeVO cmmnCodeVO, BindingResult bindingResult) throws Exception {
    	
    	System.out.println(cmmnCodeVO);
    	ResultVO resultVO = new ResultVO();
    	cmmnCodeVO.setClCode("EFC");
		LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
		CmmnClCodeVO searchVO = new CmmnClCodeVO();
		beanValidator.validate(cmmnCodeVO, bindingResult);
		if (bindingResult.hasErrors()) {
			
	        List<?> CmmnCodeList = cmmnClCodeManageService.selectCmmnClCodeList(searchVO);
	        resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		}
		
		if(cmmnCodeVO.getCodeId() != null){
			CmmnCode vo = cmmnCodeManageService.selectCmmnCodeDetail(cmmnCodeVO);
			if(vo != null){
				List<?> CmmnCodeList = cmmnClCodeManageService.selectCmmnClCodeList(searchVO);
		        resultVO.setResultCode(ResponseCode.SAVE_ERROR.getCode());
		        resultVO.setResultMessage(ResponseCode.SAVE_ERROR.getMessage());
		        
			    return resultVO;
			}
		}
	
		cmmnCodeVO.setFrstRegisterId((user == null || user.getUniqId() == null) ? "" : user.getUniqId());
		cmmnCodeManageService.insertCmmnCode(cmmnCodeVO);
		
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
    }
        
    /**
     * 공통코드를 삭제한다.
     * 
     * @param cmmnCodeVO
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/sym/ccm/cca/RemoveCcmCmmnCodeAPI.do")
    public void deleteCmmnCode(String codeId) throws Exception {

    	codeId = codeId.replace("\"", "");
    	LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
    	CmmnCodeVO cmmnCodeVO = new CmmnCodeVO();
    	cmmnCodeVO.setClCode("EFC");
    	cmmnCodeVO.setCodeId(codeId);

		cmmnCodeVO.setLastUpdusrId((user == null || user.getUniqId() == null) ? "" : user.getUniqId());
		cmmnCodeManageService.deleteCmmnCode(cmmnCodeVO);
    }
    
    /**
     * 공통코드 수정을 위한 수정페이지로 이동한다.
     * 
     * @param cmmnCodeVO
     * @param model
     * @return "egovframework/com/sym/ccm/cca/EgovCcmCmmnCodeUpdt"
     * @throws Exception
     */
    @RequestMapping("/sym/ccm/cca/UpdateCcmCmmnCodeView.do")
    public String updateCmmnCodeView(@ModelAttribute("cmmnCodeVO") CmmnCodeVO cmmnCodeVO, ModelMap model)
	    throws Exception {
		
    	CmmnCode result = cmmnCodeManageService.selectCmmnCodeDetail(cmmnCodeVO);
		
		model.addAttribute("cmmnCodeVO", result);
	
		return "egovframework/com/sym/ccm/cca/EgovCcmCmmnCodeUpdt";  
    }
    
    /**
     * 공통코드를 수정한다.
     * 
     * @param cmmnCodeVO
     * @param status
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/sym/ccm/cca/UpdateCcmCmmnCodeAPI.do")
    public ResultVO updateCmmnCode(@RequestBody CmmnCodeVO cmmnCodeVO, BindingResult bindingResult) throws Exception {
    	ResultVO resultVO = new ResultVO();
    	HashMap<String,Object> result = new HashMap<>();

    	LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
	
		beanValidator.validate(cmmnCodeVO, bindingResult);
		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		}
	
		cmmnCodeVO.setLastUpdusrId((user == null || user.getUniqId() == null) ? "" : user.getUniqId());
		cmmnCodeManageService.updateCmmnCode(cmmnCodeVO);
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
    }
	
	
}