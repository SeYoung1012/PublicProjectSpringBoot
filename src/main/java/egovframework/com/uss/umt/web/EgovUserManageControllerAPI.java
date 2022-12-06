package egovframework.com.uss.umt.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import egovframework.com.cmm.ComDefaultCodeVO;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovCmmUseService;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.umt.service.EgovUserManageService;
import egovframework.com.uss.umt.service.UserDefaultVO;
import egovframework.com.uss.umt.service.UserManageVO;
import egovframework.com.utl.sim.service.EgovFileScrty;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springmodules.validation.commons.DefaultBeanValidator;

/**
 * 업무사용자관련 요청을  비지니스 클래스로 전달하고 처리된결과를  해당   웹 화면으로 전달하는  Controller를 정의한다
 * @author 공통서비스 개발팀 조재영
 * @since 2009.04.10
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.04.10  조재영          최초 생성
 *   2011.08.26	 정진오			IncludedInfo annotation 추가
 *   2014.12.08	 이기하			암호화방식 변경(EgovFileScrty.encryptPassword)
 *   2015.06.16	 조정국			수정시 유효성체크 후 에러발생 시 목록으로 이동하여 에러메시지 표시
 *   2015.06.19	 조정국			미인증 사용자에 대한 보안처리 기준 수정 (!isAuthenticated)
 *   2017.07.21  장동한 			로그인인증제한 작업
 *
 * </pre>
 */

@RestController
public class EgovUserManageControllerAPI {

	/** userManageService */
	@Resource(name = "userManageService")
	private EgovUserManageService userManageService;

	/** cmmUseService */
	@Resource(name = "EgovCmmUseService")
	private EgovCmmUseService cmmUseService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
	@Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

	/** DefaultBeanValidator beanValidator */
	@Autowired
	private DefaultBeanValidator beanValidator;

	/**
	 * 사용자목록을 조회한다. (pageing)
	 * @param userSearchVO 검색조건정보
	 * @param model 화면모델
	 * @return cmm/uss/umt/EgovUserManage
	 * @throws Exception
	 */
	@IncludedInfo(name = "업무사용자관리api", order = 460, gid = 50)
	@RequestMapping(value = "/uss/umt/EgovUserManageAPI.do")
	public ResultVO selectUserListAPI(UserDefaultVO userSearchVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}
		 
		
		HashMap<String,Object> model2 = new HashMap<String, Object>();
		/** EgovPropertyService */
		userSearchVO.setPageUnit(propertiesService.getInt("pageUnit"));
		userSearchVO.setPageSize(propertiesService.getInt("pageSize"));

		/** pageing */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(userSearchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(userSearchVO.getPageUnit());
		paginationInfo.setPageSize(userSearchVO.getPageSize());

		userSearchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		userSearchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		userSearchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<?> userList = userManageService.selectUserList(userSearchVO);
		model2.put("resultList", userList);

		int totCnt = userManageService.selectUserListTotCnt(userSearchVO);
		paginationInfo.setTotalRecordCount(totCnt);
		model2.put("paginationInfo", paginationInfo);

		//사용자상태코드를 코드정보로부터 조회
		//ComDefaultCodeVO vo = new ComDefaultCodeVO();
		//vo.setCodeId("COM013");
		//List<?> emplyrSttusCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//model.addAttribute("emplyrSttusCode_result", emplyrSttusCode_result);//사용자상태코드목록
		resultVO.setResult(model2);
		return resultVO;
	}

	/**
	 * 사용자등록화면으로 이동한다.
	 * @param userSearchVO 검색조건정보
	 * @param userManageVO 사용자초기화정보
	 * @param model 화면모델
	 * @return cmm/uss/umt/EgovUserInsert
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserInsertOptionAPI.do")
	public ResultVO insertUserOption(@ModelAttribute("userSearchVO") UserDefaultVO userSearchVO, @ModelAttribute("userManageVO") UserManageVO userManageVO)
			throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO; 
	//	}
		

		ComDefaultCodeVO vo = new ComDefaultCodeVO();
		HashMap<String, Object> result = new HashMap<String, Object>();
		//패스워드힌트목록을 코드정보로부터 조회
		vo.setCodeId("COM022");
		List<?> passwordHint_result = cmmUseService.selectCmmCodeDetail(vo);
		//성별구분코드를 코드정보로부터 조회
		vo.setCodeId("COM014");
		List<?> sexdstnCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//사용자상태코드를 코드정보로부터 조회
		vo.setCodeId("COM013");
		List<?> emplyrSttusCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//소속기관코드를 코드정보로부터 조회 - COM025
		vo.setCodeId("COM025");
		List<?> insttCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//조직정보를 조회 - ORGNZT_ID정보
		vo.setTableNm("COMTNORGNZTINFO");
		List<?> orgnztId_result = cmmUseService.selectOgrnztIdDetail(vo);
		//그룹정보를 조회 - GROUP_ID정보
		vo.setTableNm("COMTNORGNZTINFO");
		List<?> groupId_result = cmmUseService.selectGroupIdDetail(vo);
		
		result.put("passwordHint_result", passwordHint_result); //패스워트힌트목록
		result.put("sexdstnCode_result", sexdstnCode_result); //성별구분코드목록
		result.put("emplyrSttusCode_result", emplyrSttusCode_result);//사용자상태코드목록
		result.put("insttCode_result", insttCode_result); //소속기관코드목록
		result.put("orgnztId_result", orgnztId_result); //조직정보 목록
		result.put("groupId_result", groupId_result); //그룹정보 목록
		resultVO.setResult(result);
		return resultVO;
	}

	/**
	 * 사용자등록처리후 목록화면으로 이동한다.
	 * @param userManageVO 사용자등록정보
	 * @param bindingResult 입력값검증용 bindingResult
	 * @param model 화면모델
	 * @return forward:/uss/umt/EgovUserManage.do
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserInsertAPI.do")
	public ResultVO insertUser(@RequestBody UserManageVO userManageVO, BindingResult bindingResult) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	 //       resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}

		beanValidator.validate(userManageVO, bindingResult);
		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		} else {
			if ("".equals(userManageVO.getOrgnztId())) {//KISA 보안약점 조치 (2018-10-29, 윤창원)
				userManageVO.setOrgnztId(null);
			}
			if ("".equals(userManageVO.getGroupId())) {//KISA 보안약점 조치 (2018-10-29, 윤창원)
				userManageVO.setGroupId(null);
			}
			userManageService.insertUser(userManageVO);
			//Exception 없이 진행시 등록성공메시지
			resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
			return resultVO;
		}
	}

	/**
	 * 사용자정보 수정을 위해 사용자정보를 상세조회한다.
	 * @param uniqId 상세조회대상 사용자아이디
	 * @param userSearchVO 검색조건
	 * @param model 화면모델
	 * @return uss/umt/EgovUserSelectUpdt
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserUpdtViewAPI.do")
	public ResultVO updateUserView(String uniqId, @ModelAttribute("searchVO") UserDefaultVO userSearchVO, Model model) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		uniqId = uniqId.replace("\"", "");
		ComDefaultCodeVO vo = new ComDefaultCodeVO();

		//패스워드힌트목록을 코드정보로부터 조회
		vo.setCodeId("COM022");
		List<?> passwordHint_result = cmmUseService.selectCmmCodeDetail(vo);
		//성별구분코드를 코드정보로부터 조회
		vo.setCodeId("COM014");
		List<?> sexdstnCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//사용자상태코드를 코드정보로부터 조회
		vo.setCodeId("COM013");
		List<?> emplyrSttusCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//소속기관코드를 코드정보로부터 조회 - COM025
		vo.setCodeId("COM025");
		List<?> insttCode_result = cmmUseService.selectCmmCodeDetail(vo);
		//조직정보를 조회 - ORGNZT_ID정보
		vo.setTableNm("COMTNORGNZTINFO");
		List<?> orgnztId_result = cmmUseService.selectOgrnztIdDetail(vo);
		//그룹정보를 조회 - GROUP_ID정보
		vo.setTableNm("COMTNORGNZTINFO");
		List<?> groupId_result = cmmUseService.selectGroupIdDetail(vo);

		result.put("passwordHint_result", passwordHint_result); //패스워트힌트목록
		result.put("sexdstnCode_result", sexdstnCode_result); //성별구분코드목록
		result.put("emplyrSttusCode_result", emplyrSttusCode_result);//사용자상태코드목록
		result.put("insttCode_result", insttCode_result); //소속기관코드목록
		result.put("orgnztId_result", orgnztId_result); //조직정보 목록
		result.put("groupId_result", groupId_result); //그룹정보 목록

		UserManageVO userManageVO = new UserManageVO();
		userManageVO = userManageService.selectUser(uniqId);
		System.out.println(uniqId);
		System.out.println(userManageVO);
		result.put("userSearchVO", userSearchVO);
		result.put("userManageVO", userManageVO);
		resultVO.setResult(result);

		return resultVO;
	}
	
	/**
	 * 로그인인증제한 해제 
	 * @param userManageVO 사용자정보
	 * @param model 화면모델
	 * @return uss/umt/EgovUserSelectUpdtView.do
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserLockIncorrectAPI.do")
	public ResultVO updateLockIncorrect(@RequestBody UserManageVO userManageVO)
			throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}
		userManageService.updateLockIncorrect(userManageVO);
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

	/**
	 * 사용자정보 수정후 목록조회 화면으로 이동한다.
	 * @param userManageVO 사용자수정정보
	 * @param bindingResult 입력값검증용 bindingResult
	 * @param model 화면모델
	 * @return forward:/uss/umt/EgovUserManage.do
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserSelectUpdtAPI.do")
	public ResultVO updateUser(@RequestBody UserManageVO userManageVO, BindingResult bindingResult) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
	//	if (!isAuthenticated) {
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	 //       resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}

		HashMap<String, Object> result = new HashMap<String, Object>();
		System.out.println(userManageVO);
		beanValidator.validate(userManageVO, bindingResult);
		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		} else {
			//업무사용자 수정시 히스토리 정보를 등록한다.
			userManageService.insertUserHistory(userManageVO);
			if ("".equals(userManageVO.getOrgnztId())) {//KISA 보안약점 조치 (2018-10-29, 윤창원)
				userManageVO.setOrgnztId(null);
			}
			if ("".equals(userManageVO.getGroupId())) {//KISA 보안약점 조치 (2018-10-29, 윤창원)
				userManageVO.setGroupId(null);
			}
			userManageService.updateUser(userManageVO);
			//Exception 없이 진행시 수정성공메시지
			resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
			return resultVO;
		}
	}

	/**
	 * 사용자정보삭제후 목록조회 화면으로 이동한다.
	 * @param checkedIdForDel 삭제대상아이디 정보
	 * @param userSearchVO 검색조건
	 * @param model 화면모델
	 * @return forward:/uss/umt/EgovUserManage.do
	 * @throws Exception
	 */
	@RequestMapping("/uss/umt/EgovUserDeleteAPI.do")
	public ResultVO deleteUser(String checkedIdForDel) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
	//	if (!isAuthenticated) {
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
	//	    return resultVO;
	//	}

		checkedIdForDel = checkedIdForDel.replace("\"", "");
		System.out.println(checkedIdForDel);
		userManageService.deleteUser(checkedIdForDel);
		//Exception 없이 진행시 등록성공메시지
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

	/**
	 * 입력한 사용자아이디의 중복확인화면 이동
	 * @param model 화면모델
	 * @return uss/umt/EgovIdDplctCnfirm
	 * @throws Exception
	 */
	//@RequestMapping(value = "/uss/umt/EgovIdDplctCnfirmView.do")
	public String checkIdDplct(ModelMap model) throws Exception {

		// 미인증 사용자에 대한 보안처리
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
	//	if (!isAuthenticated) {
	//		return "index";
	//	}

		model.addAttribute("checkId", "");
		model.addAttribute("usedCnt", "-1");
		return "egovframework/com/uss/umt/EgovIdDplctCnfirm";
	}

	/**
	 * 입력한 사용자아이디의 중복여부를 체크하여 사용가능여부를 확인
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return uss/umt/EgovIdDplctCnfirm
	 * @throws Exception
	 */
	//@RequestMapping(value = "/uss/umt/EgovIdDplctCnfirm.do")
	public String checkIdDplct(@RequestParam Map<String, Object> commandMap, ModelMap model) throws Exception {

		// 미인증 사용자에 대한 보안처리
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
	//	if (!isAuthenticated) {
	//		return "index";
	//	}

		String checkId = (String) commandMap.get("checkId");
		checkId = new String(checkId.getBytes("ISO-8859-1"), "UTF-8");

		if (checkId == null || checkId.equals(""))
			return "forward:/uss/umt/EgovIdDplctCnfirmView.do";

		int usedCnt = userManageService.checkIdDplct(checkId);
		model.addAttribute("usedCnt", usedCnt);
		model.addAttribute("checkId", checkId);

		return "egovframework/com/uss/umt/EgovIdDplctCnfirm";
	}
	
	
	/**
	 * 입력한 사용자아이디의 중복여부를 체크하여 사용가능여부를 확인
	 * @param commandMap 파라메터전달용 commandMap
	 * @param model 화면모델
	 * @return uss/umt/EgovIdDplctCnfirm
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/umt/EgovIdDplctCnfirmAPI.do")
	public ResultVO checkIdDplctAjax(String id) throws Exception {
		ResultVO resultVO = new ResultVO();
    	HashMap<String,Object> result = new HashMap<>(); 

		//String checkId = (String) commandMap.get("checkId");
    	System.out.println(id);
    	String checkId = id;
		//checkId = new String(checkId.getBytes("ISO-8859-1"), "UTF-8");

		int usedCnt = userManageService.checkIdDplct(checkId);
		System.out.println(usedCnt);
		result.put("checkId", checkId);
		result.put("usedCnt", usedCnt);
		resultVO.setResult(result);

		return resultVO;
	}

	/**
	 * 업무사용자 암호 수정처리 후 화면 이동
	 * @param model 화면모델
	 * @param commandMap 파라메터전달용 commandMap
	 * @param userSearchVO 검색조 건
	 * @param userManageVO 사용자수정정보(비밀번호)
	 * @return uss/umt/EgovUserPasswordUpdt
	 * @throws Exception
	 */
	@RequestMapping(value = "/uss/umt/EgovUserPasswordUpdtAPI.do")
	public ResultVO updatePassword(@RequestBody Map<String, Object> commandMap, @ModelAttribute("searchVO") UserDefaultVO userSearchVO,
			@ModelAttribute("userManageVO") UserManageVO userManageVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		// 미인증 사용자에 대한 보안처리
		
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated(); 
	//	if(!isAuthenticated) { 
	//		resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	//        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	       
	//	    return resultVO;
	//	}
		
		HashMap<String,Object> result = new HashMap<>();
		String oldPassword = (String) commandMap.get("oldPassword");
		System.out.println(oldPassword);
		String newPassword = (String) commandMap.get("newPassword");
		String newPassword2 = (String) commandMap.get("newPassword2");
		String uniqId = (String) commandMap.get("uniqId");
		System.out.println(uniqId);
		String emplyrId = (String) commandMap.get("emplyrId");

		boolean isCorrectPassword = false;
		UserManageVO selectVO = new UserManageVO();
		userManageVO.setPassword(newPassword);
		userManageVO.setOldPassword(oldPassword);
		userManageVO.setUniqId(uniqId);
		userManageVO.setEmplyrId(emplyrId);
		
		selectVO = userManageService.selectPassword(userManageVO);
		//패스워드 암호화
		String encryptPass = EgovFileScrty.encryptPassword(oldPassword, userManageVO.getEmplyrId());
		if (encryptPass.equals(selectVO.getPassword())) {
			if (newPassword.equals(newPassword2)) {
				isCorrectPassword = true;
			} else {
				isCorrectPassword = false;
			}
		} else {
			isCorrectPassword = false;
		}

		if (isCorrectPassword) {
			userManageVO.setPassword(EgovFileScrty.encryptPassword(newPassword, userManageVO.getEmplyrId()));
			userManageService.updatePassword(userManageVO);
		} else {
			resultVO.setResultCode(ResponseCode.INPUT_CHECK_ERROR.getCode());
	        resultVO.setResultMessage(egovMessageSource.getMessage("fail.user.passwordUpdate1"));
			return resultVO;
		}
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

	/**
	 * 업무사용자 암호 수정  화면 이동
	 * @param model 화면모델
	 * @param commandMap 파라메터전달용 commandMap
	 * @param userSearchVO 검색조건
	 * @param userManageVO 사용자수정정보(비밀번호)
	 * @return uss/umt/EgovUserPasswordUpdt
	 * @throws Exception
	 */
	//@RequestMapping(value = "/uss/umt/EgovUserPasswordUpdtView.do")
	public String updatePasswordView(ModelMap model, @RequestParam Map<String, Object> commandMap, @ModelAttribute("searchVO") UserDefaultVO userSearchVO,
			@ModelAttribute("userManageVO") UserManageVO userManageVO) throws Exception {

		// 미인증 사용자에 대한 보안처리
	//	Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
	//	if (!isAuthenticated) {
	//		return "index";
	//	}

		String userTyForPassword = (String) commandMap.get("userTyForPassword");
		userManageVO.setUserTy(userTyForPassword);

		model.addAttribute("userManageVO", userManageVO);
		model.addAttribute("userSearchVO", userSearchVO);
		return "egovframework/com/uss/umt/EgovUserPasswordUpdt";
	}

}
