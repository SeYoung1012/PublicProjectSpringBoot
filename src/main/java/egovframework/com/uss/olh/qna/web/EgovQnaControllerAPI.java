package egovframework.com.uss.olh.qna.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.cmm.util.EgovXssChecker;
import egovframework.com.uss.olh.qna.service.EgovQnaService;
import egovframework.com.uss.olh.qna.service.QnaDefaultVO;
import egovframework.com.uss.olh.qna.service.QnaVO;
import egovframework.com.utl.fcc.service.EgovStringUtil;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
/**
*
* Q&A를 처리하는 Controller 클래스
* @author 디지털정부서비스 개방 플랫폼 개발 김세영
* @since 2022.10.28
* @version 1.0
* @see
*
* <pre>
* << 개정이력(Modification Information) >>
*
*   수정일     	수정자           			수정내용
*  ------------   --------    ---------------------------------------------
*   2020-10-28   김세영			최초생성
*
* </pre>
*/
@RestController
public class EgovQnaControllerAPI {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EgovQnaControllerAPI.class);
	
	@Resource(name = "EgovQnaService")
	private EgovQnaService egovQnaService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	@Resource(name = "EgovCmmUseService")
	private EgovCmmUseService cmmUseService;

	/** EgovMessageSource */
	@Resource(name = "egovMessageSource")
	EgovMessageSource egovMessageSource;

	// Validation 관련
	@Autowired
	private DefaultBeanValidator beanValidator;
	
	/**
	 * Q&A정보 목록을 조회한다. (pageing)
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/qna/EgovQnaListInqire"
	 * @throws Exception
	 */
	@IncludedInfo(name = "Q&A관리",listUrl ="/uss/olh/qna/selectQnaListAPI.do", order = 550, gid = 50)
	@RequestMapping(value = "/uss/olh/qna/selectQnaListAPI.do")
	public ResultVO selectQnaList(@ModelAttribute("searchVO")  QnaVO searchVO, ModelMap model ) throws Exception {
		
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
		
		
		
		List<?> QnaList = egovQnaService.selectQnaList(searchVO);
		//model.addAttribute("resultList", QnaList);
		Map<String, Object> resultMap = new HashMap<>();
        System.out.println("searchKeyword:" + searchVO.getSearchWrd());
        System.out.println("searchCondition:" + searchVO.getSearchCnd());
		
		resultMap.put("resultList", QnaList);
			
		// 인증여부 체크
		Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();

		if (!isAuthenticated) {
			resultMap.put("certificationAt", "N");
		} else {
			resultMap.put("certificationAt", "Y");
		}
		int totCnt = egovQnaService.selectQnaListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		resultMap.put("paginationInfo", paginationInfo);
		
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	    resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		resultVO.setResult(resultMap);
		
		return resultVO;
	}
	
	/**
	 * Q&A정보 목록에 대한 상세정보를 조회한다.
	 * @param passwordConfirmAt
	 * @param qnaVO
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/qna/EgovQnaDetail"
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/uss/olh/qna/selectQnaDetailAPI.do")
	public ResultVO selectQnaDetail(@ModelAttribute("searchVO") QnaDefaultVO searchVO, 
			 QnaVO qnaVO) throws Exception {
		//qnaVO.setQaId(qaId);
		
		ResultVO resultVO = new ResultVO();
		
		Map<String, Object> resultMap = new HashMap<>();		
		//qnaVO.setQaId(qaId);
		
		//조회수 수정처리
		egovQnaService.updateQnaInqireCo(qnaVO);		
		QnaVO vo = egovQnaService.selectQnaDetail(qnaVO);

		// 작성 비밀번호를 얻는다.
//		String writngPassword = vo.getWritngPassword();

		// EgovFileScrty Util에 있는 암호화 모듈을 적용해서 복호화한다.
//		vo.setWritngPassword(EgovFileScrty.decode(writngPassword));
		resultMap.put("result", vo);
		resultVO.setResult(resultMap);
		return resultVO;
	}
	
	
	/**
	 * Q&A정보를 등록한다.
	 * @param searchVO
	 * @param qnaVO
	 * @param bindingResult
	 * @return	"forward:/uss/olh/qna/selectQnaList.do"
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/uss/olh/qna/insertQnaAPI.do")
	public ResultVO insertQna(@ModelAttribute("searchVO") QnaVO searchVO, 
			@ModelAttribute("qnaVO") QnaVO qnaVO, BindingResult bindingResult,
			 ResultVO resultVO) throws Exception {
	
		
		beanValidator.validate(qnaVO, bindingResult);
		
	
//		if (bindingResult.hasErrors()) {
//			resultVO.setResultCode(0);
//			return resultVO;
//		}

		// 로그인VO에서  사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

		String frstRegisterId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());

		qnaVO.setFrstRegisterId(frstRegisterId); // 최초등록자ID
		qnaVO.setLastUpdusrId(frstRegisterId); // 최종수정자ID

		// 작성비밀번호를 암호화 하기 위해서 Get
//		String writngPassword = qnaVO.getWritngPassword();

		// EgovFileScrty Util에 있는 암호화 모듈을 적용해서 암호화 한다.
//		qnaVO.setWritngPassword(EgovFileScrty.encode(writngPassword));
		
		egovQnaService.insertQna(qnaVO);
		
		System.out.println("--------------------egovQnaService.insertQna(qnaVO);-----------" + qnaVO);
		resultVO.setResultMessage(egovMessageSource.getMessage("success.common.insert"));
        resultVO.setResultCode(200);
		return resultVO;
	}
	
	
	/**
	 * Q&A정보를 수정처리한다.
	 * @param searchVO
	 * @param qnaVO
	 * @param bindingResult
	 * @return	"forward:/uss/olh/qna/selectQnaList.do"
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping(value="/uss/olh/qna/updateQnaAPI.do")
	public ResultVO updateQna(HttpServletRequest request,
    		 @RequestParam("qaId") String qaId,
			@ModelAttribute("searchVO") QnaVO searchVO,
			@ModelAttribute("qnaVO") QnaVO qnaVO, 
			BindingResult bindingResult,ResultVO resultVO) throws Exception {
		
	
		
		 Map<String, Object> resultMap = new HashMap<>();
		// Validation
		beanValidator.validate(qnaVO, bindingResult);

		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(0);
			return resultVO;
		}
		
    	//--------------------------------------------------------------------------------------------
    	// @ XSS 사용자권한체크 START
    	// param1 : 사용자고유ID(uniqId,esntlId)
    	//--------------------------------------------------------
    	LOGGER.debug("@ XSS 권한체크 START ----------------------------------------------");
    	//step1 DB에서 해당 게시물의 uniqId 조회
    		
    	qnaVO.setQaId(qaId);
    	
    	// 변수명은 CoC 에 따라 JSTL사용을 위해
        resultMap.put("qnaVO", egovQnaService.selectQnaDetail(qnaVO));
        
    	QnaVO vo = egovQnaService.selectQnaDetail(qnaVO);
    	
    	//step2 EgovXssChecker 공통모듈을 이용한 권한체크
    	EgovXssChecker.checkerUserXss(request, vo.getFrstRegisterId()); 
      	LOGGER.debug("@ XSS 권한체크 END ------------------------------------------------");
    	//--------------------------------------------------------
    	// @ XSS 사용자권한체크 END
    	//--------------------------------------------------------------------------------------------

		// 로그인VO에서  사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
		String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());

		qnaVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID

		// 작성비밀번호를 암호화 하기 위해서 Get
//		String writngPassword = qnaManageVO.getWritngPassword();

		// EgovFileScrty Util에 있는 암호화 모듈을 적용해서 암호화 한다.
//		qnaManageVO.setWritngPassword(EgovFileScrty.encode(writngPassword));
		
		egovQnaService.updateQna(qnaVO);
		resultVO.setResult(resultMap);
        resultVO.setResultCode(200);
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
		return resultVO;

	}
	
	/**
	 * Q&A정보를 삭제처리한다.
	 * @param qnaVO
	 * @param searchVO
	 * @return	"forward:/uss/olh/qna/selectQnaList.do"
	 * @throws Exception
	 */
	@RequestMapping(value="/uss/olh/qna/deleteQnaAPI.do")
	public ResultVO deleteQna(
			ResultVO resultVO,
    		HttpServletRequest request,
    		QnaVO qnaVO, 
			@ModelAttribute("searchVO") QnaVO searchVO) throws Exception {
		
	
    	//--------------------------------------------------------------------------------------------
    	// @ XSS 사용자권한체크 START
    	// param1 : 사용자고유ID(uniqId,esntlId)
    	//--------------------------------------------------------
    	LOGGER.debug("@ XSS 권한체크 START ----------------------------------------------");
    	
    	//step1 DB에서 해당 게시물의 uniqId 조회
    	QnaVO vo = egovQnaService.selectQnaDetail(qnaVO);;
    	
    	//step2 EgovXssChecker 공통모듈을 이용한 권한체크
    	EgovXssChecker.checkerUserXss(request, vo.getFrstRegisterId()); 
      	LOGGER.debug("@ XSS 권한체크 END ------------------------------------------------");
    	//--------------------------------------------------------
    	// @ XSS 사용자권한체크 END
    	//--------------------------------------------------------------------------------------------
    
		egovQnaService.deleteQna(qnaVO);
		resultVO.setResultMessage(egovMessageSource.getMessage("success.common.delete"));
		return resultVO;
	}
	
	/**
	 * Q&A답변정보 목록을 조회한다. (pageing)
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/qna/EgovQnaAnswerList"
	 * @throws Exception
	 */
	@IncludedInfo(name = "Q&A답변관리",listUrl="/uss/olh/qna/selectQnaAnswerListAPI.do" ,order = 551, gid = 50)
	@RequestMapping(value = "/uss/olh/qna/selectQnaAnswerListAPI.do")
	public ResultVO selectQnaAnswerList(@ModelAttribute("searchVO") QnaVO searchVO, ModelMap model) throws Exception {
		
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

		List<?> QnaAnswerList = egovQnaService.selectQnaAnswerList(searchVO);
		
		Map<String, Object> resultMap = new HashMap<>();
        System.out.println("searchKeyword:" + searchVO.getSearchWrd());
        System.out.println("searchCondition:" + searchVO.getSearchCnd());
        
        resultMap.put("resultList", QnaAnswerList);

		int totCnt = egovQnaService.selectQnaAnswerListCnt(searchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		resultMap.put("paginationInfo", paginationInfo);
		
		 resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	     resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
	     resultVO.setResult(resultMap);
	        

		return resultVO;
	}
	
	/**
	 * Q&A답변정보 목록에 대한 상세정보를 조회한다.
	 * @param qnaVO
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/qna/EgovQnaAnswerDetail"
	 * @throws Exception
	 */
	@RequestMapping(value="/uss/olh/qna/selectQnaAnswerDetailAPI.do")
	public ResultVO selectQnaAnswerDetail(QnaVO qnaVO, @ModelAttribute("searchVO") QnaVO searchVO, ModelMap model) throws Exception {
		
		ResultVO resultVO = new ResultVO();
		QnaVO vo = egovQnaService.selectQnaDetail(qnaVO);		
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
	 * Q&A답변정보를 수정처리한다.
	 * @param qnaVO
	 * @param searchVO
	 * @return	"forward:/uss/olh/qnm/selectQnaAnswerList.do"
	 * @throws Exception
	 */
	@RequestMapping(value="/uss/olh/qna/updateQnaAnswerAPI.do")
	public ResultVO updateQnaAnswer(QnaVO qnaVO, @ModelAttribute("searchVO") QnaVO searchVO,ResultVO resultVO) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<>();
		// 로그인VO에서  사용자 정보 가져오기
		LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
		String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
		qnaVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID

		egovQnaService.updateQnaAnswer(qnaVO);
		resultVO.setResult(resultMap);
        resultVO.setResultCode(200);
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
		return resultVO;

	}
	
}
