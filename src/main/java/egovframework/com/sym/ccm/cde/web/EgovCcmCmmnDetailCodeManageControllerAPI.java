package egovframework.com.sym.ccm.cde.web;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.CmmnDetailCode;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.sym.ccm.cca.service.CmmnCodeVO;
import egovframework.com.sym.ccm.cca.service.EgovCcmCmmnCodeManageService;
import egovframework.com.sym.ccm.ccc.service.CmmnClCodeVO;
import egovframework.com.sym.ccm.ccc.service.EgovCcmCmmnClCodeManageService;
import egovframework.com.sym.ccm.cde.service.CmmnDetailCodeVO;
import egovframework.com.sym.ccm.cde.service.EgovCcmCmmnDetailCodeManageService;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

/**
*
* 공통상세코드에 관한 요청을 받아 서비스 클래스로 요청을 전달하고 서비스클래스에서 처리한 결과를 웹 화면으로 전달을 위한 Controller를 정의한다
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
*   2009.04.01  이중호       최초 생성
*   2011.08.26	정진오	IncludedInfo annotation 추가
*   2017.08.08	이정은	표준프레임워크 v3.7 개선
*
* </pre>
*/

@Controller
@RestController
public class EgovCcmCmmnDetailCodeManageControllerAPI {

	@Resource(name = "CmmnDetailCodeManageService")
	private EgovCcmCmmnDetailCodeManageService cmmnDetailCodeManageService;

	@Resource(name = "CmmnClCodeManageService")
	private EgovCcmCmmnClCodeManageService cmmnClCodeManageService;

	@Resource(name = "CmmnCodeManageService")
	private EgovCcmCmmnCodeManageService cmmnCodeManageService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	/** EgovMessageSource */
	@Resource(name = "egovMessageSource")
	EgovMessageSource egovMessageSource;

	@Autowired
	private DefaultBeanValidator beanValidator;

	/**
	 * 공통상세코드 목록을 조회한다.
	  * @param loginVO
	  * @param searchVO
	  * @param model
	  * @return "egovframework/com/sym/ccm/cde/EgovCcmCmmnDetailCodeList"
	  * @throws Exception
	  */
	@IncludedInfo(name = "공통상세코드", listUrl = "/sym/ccm/cde/SelectCcmCmmnDetailCodeListAPI.do", order = 970, gid = 60)
	@RequestMapping(value = "/sym/ccm/cde/SelectCcmCmmnDetailCodeListAPI.do")
	public ResultVO selectCmmnDetailCodeList(CmmnDetailCodeVO searchVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		/** EgovPropertyService.sample */
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

		List<?> CmmnCodeList = cmmnDetailCodeManageService.selectCmmnDetailCodeList(searchVO);
		result.put("resultList", CmmnCodeList);

		int totCnt = cmmnDetailCodeManageService.selectCmmnDetailCodeListTotCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		result.put("paginationInfo", paginationInfo);
		
		resultVO.setResult(result);  

		return resultVO;
	}

	/**
	 * 공통상세코드 상세항목을 조회한다.
	 * @param loginVO
	 * @param cmmnDetailCodeVO
	 * @param model
	 * @return "egovframework/com/sym/ccm/cde/EgovCcmCmmnDetailCodeDetail"
	 * @throws Exception
	 */
	@RequestMapping(value = "/sym/ccm/cde/SelectCcmCmmnDetailCodeDetailAPI.do")
	public ResultVO selectCmmnDetailCodeDetail(@ModelAttribute("loginVO") LoginVO loginVO, CmmnDetailCodeVO cmmnDetailCodeVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String, Object> result = new HashMap<>();
		CmmnDetailCode vo = cmmnDetailCodeManageService.selectCmmnDetailCodeDetail(cmmnDetailCodeVO);
		
		result.put("result",vo);
		resultVO.setResult(result);

		return resultVO;
	}

	/**
	 * 공통상세코드를 삭제한다.
	 * @param loginVO
	 * @param cmmnDetailCodeVO
	 * @param model
	 * @return "forward:/sym/ccm/cde/EgovCcmCmmnDetailCodeList.do"
	 * @throws Exception
	 */
	@RequestMapping(value = "/sym/ccm/cde/RemoveCcmCmmnDetailCodeAPI.do")
	public void deleteCmmnDetailCode(@ModelAttribute("loginVO") LoginVO loginVO,@RequestBody CmmnDetailCodeVO cmmnDetailCodeVO,
		ModelMap model) throws Exception {
		cmmnDetailCodeManageService.deleteCmmnDetailCode(cmmnDetailCodeVO);
	}

	/**
	 * 공통상세코드 등록을 위한 등록페이지로 이동한다.
	 *
	 * @param cmmnDetailCodeVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/sym/ccm/cde/RegistCcmCmmnDetailCodeViewAPI.do")
	public ResultVO insertCmmnDetailCodeView(@ModelAttribute("loginVO") LoginVO loginVO,
		@ModelAttribute("cmmnCodeVO") CmmnCodeVO cmmnCodeVO,
		@ModelAttribute("cmmnDetailCodeVO") CmmnDetailCodeVO cmmnDetailCodeVO, ModelMap model) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String,Object> result = new HashMap<>();
		CmmnClCodeVO searchClCodeVO = new CmmnClCodeVO();
		searchClCodeVO.setFirstIndex(0);
		List<?> CmmnClCodeList = cmmnClCodeManageService.selectCmmnClCodeList(searchClCodeVO);
		model.addAttribute("clCodeList", CmmnClCodeList);
		result.put("clCodeList", CmmnClCodeList);

		CmmnCodeVO clCode = new CmmnCodeVO();
		clCode.setClCode(cmmnCodeVO.getClCode());

		//if (!cmmnCodeVO.getClCode().equals("")) {

			CmmnCodeVO searchCodeVO = new CmmnCodeVO();
			searchCodeVO.setRecordCountPerPage(999999);
			searchCodeVO.setFirstIndex(0);
			searchCodeVO.setSearchCondition("clCode");
			searchCodeVO.setSearchKeyword(cmmnCodeVO.getClCode());

			List<?> CmmnCodeList = cmmnCodeManageService.selectCmmnCodeList(searchCodeVO);
			model.addAttribute("codeList", CmmnCodeList);
			result.put("codeList",CmmnCodeList);
		//}
		resultVO.setResult(result);

		return resultVO;
	}

	/**
	 * 공통상세코드를 등록한다.
	 *
	 * @param CmmnDetailCodeVO
	 * @param CmmnDetailCodeVO
	 * @param status
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/sym/ccm/cde/RegistCcmCmmnDetailCodeAPI.do")
	public ResultVO insertCmmnDetailCode(@RequestBody CmmnDetailCodeVO cmmnDetailCodeVO,
		BindingResult bindingResult) throws Exception {

		ResultVO resultVO = new ResultVO();
		LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();

		CmmnClCodeVO searchClCodeVO = new CmmnClCodeVO();

		beanValidator.validate(cmmnDetailCodeVO, bindingResult);

		if (bindingResult.hasErrors()) {

			//List<?> CmmnClCodeList = cmmnClCodeManageService.selectCmmnClCodeList(searchClCodeVO);
	        resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		}

		if (cmmnDetailCodeVO.getCodeId() != null) {

			CmmnDetailCode vo = cmmnDetailCodeManageService.selectCmmnDetailCodeDetail(cmmnDetailCodeVO);
			if (vo != null) {
		        resultVO.setResultCode(ResponseCode.SAVE_ERROR.getCode());
		        resultVO.setResultMessage(ResponseCode.SAVE_ERROR.getMessage());
		        
			    return resultVO;
			}
		}

		cmmnDetailCodeVO.setFrstRegisterId((user == null || user.getUniqId() == null) ? "" : user.getUniqId());
		cmmnDetailCodeManageService.insertCmmnDetailCode(cmmnDetailCodeVO);

        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

	/**
	 * 공통상세코드 수정을 위한 수정페이지로 이동한다.
	 *
	 * @param cmmnDetailCodeVO
	 * @param model
	 * @return "egovframework/com/sym/ccm/cde/EgovCcmCmmnDetailCodeUpdt"
	 * @throws Exception
	 */
	@RequestMapping("/sym/ccm/cde/UpdateCcmCmmnDetailCodeViewAPI.do")
	public ResultVO updateCmmnDetailCodeView(@ModelAttribute("loginVO") LoginVO loginVO, CmmnDetailCodeVO cmmnDetailCodeVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String, Object> result = new HashMap<>();
		CmmnDetailCode vo = cmmnDetailCodeManageService.selectCmmnDetailCodeDetail(cmmnDetailCodeVO);
		result.put("result", vo);
		resultVO.setResult(result);

		return resultVO;
	}

	/**
	 * 공통상세코드를 수정한다.
	 *
	 * @param cmmnDetailCodeVO
	 * @param model
	 * @return "egovframework/com/sym/ccm/cde/EgovCcmCmmnDetailCodeUpdt", "/sym/ccm/cde/SelectCcmCmmnDetailCodeList.do"
	 * @throws Exception
	 */
	@RequestMapping("/sym/ccm/cde/UpdateCcmCmmnDetailCodeAPI.do")
	public ResultVO updateCmmnDetailCode(@RequestBody CmmnDetailCodeVO cmmnDetailCodeVO,
		ModelMap model, BindingResult bindingResult)
		throws Exception {
		ResultVO resultVO = new ResultVO();
		LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
		HashMap<String,Object> result = new HashMap<>();

		beanValidator.validate(cmmnDetailCodeVO, bindingResult);

		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		}

		cmmnDetailCodeVO.setLastUpdusrId((user == null || user.getUniqId() == null) ? "" : user.getUniqId());
		cmmnDetailCodeManageService.updateCmmnDetailCode(cmmnDetailCodeVO);
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

}
