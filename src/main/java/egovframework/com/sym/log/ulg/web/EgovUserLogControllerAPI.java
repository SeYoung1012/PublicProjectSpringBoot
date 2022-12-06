package egovframework.com.sym.log.ulg.web;

import java.util.HashMap;
import java.util.Map;

import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.sym.log.ulg.service.EgovUserLogService;
import egovframework.com.sym.log.ulg.service.UserLog;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;
import javax.xml.registry.infomodel.User;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Class Name : EgovUserLogController.java
 * @Description : 사용로그정보를 관리하기 위한 컨트롤러 클래스
 * @Modification Information
 *
 *    수정일         수정자         수정내용
 *    -------        -------     -------------------
 *    2009. 3. 11.   이삼섭         최초생성
 *    2011. 7. 01.   이기하         패키지 분리(sym.log -> sym.log.ulg)
 *    2011.8.26	정진오			IncludedInfo annotation 추가
 *    2017.09.14	이정은			표준프레임워크 v3.7 개선
 *
 * @author 공통 서비스 개발팀 이삼섭
 * @since 2009. 3. 11.
 * @version
 * @see
 *
 */

@RestController
public class EgovUserLogControllerAPI {

	@Resource(name="EgovUserLogService")
	private EgovUserLogService userLogService;

	@Resource(name="propertiesService")
	protected EgovPropertyService propertyService;

	/**
	 * 사용자 로그 목록 조회
	 *
	 * @param UserLog
	* @return resultVO
     * @throws Exception
	 */
	@IncludedInfo(name="사용로그관리", listUrl= "/sym/log/ulg/SelectUserLogListAPI.do", order = 1040 ,gid = 60)
	@RequestMapping(value="/sym/log/ulg/SelectUserLogListAPI.do")
	public ResultVO selectUserLogInf(@ModelAttribute("searchVO") UserLog userLog
			) throws Exception{
		
		
		ResultVO resultVO = new ResultVO();
        Map<String, Object> resultMap = new HashMap<>();
		
		/** EgovPropertyService.sample */
		userLog.setPageUnit(propertyService.getInt("pageUnit"));
		userLog.setPageSize(propertyService.getInt("pageSize"));

		/** pageing */
		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(userLog.getPageIndex());
		paginationInfo.setRecordCountPerPage(userLog.getPageUnit());
		paginationInfo.setPageSize(userLog.getPageSize());

		userLog.setFirstIndex(paginationInfo.getFirstRecordIndex());
		userLog.setLastIndex(paginationInfo.getLastRecordIndex());
		userLog.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		HashMap<?, ?> _map = (HashMap<?, ?>)userLogService.selectUserLogInf(userLog);
		int totCnt = Integer.parseInt((String)_map.get("resultCnt"));
		
		resultMap.put("resultList", _map.get("resultList"));
	    resultMap.put("resultCnt", _map.get("resultCnt"));
	    resultMap.put("frm", userLog);
		

		paginationInfo.setTotalRecordCount(totCnt);
		resultMap.put("paginationInfo", paginationInfo);
			
		resultVO.setResult(resultMap);
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		
		return resultVO;
	}

	/**
	 * 사용자 로그 상세 조회
	 *
	 * @param userLog
	 * @return resultVO
     * @throws Exception
	 */
	@RequestMapping(value="/sym/log/ulg/SelectUserLogDetailAPI.do")
	public ResultVO selectUserLog(UserLog userLog) throws Exception{
		
		
		 ResultVO resultVO = new ResultVO();
	     Map<String, Object> resultMap = new HashMap<>();

//		userLog.setOccrrncDe(occrrncDe.trim());
//		userLog.setRqesterId(rqesterId.trim());
//		userLog.setSrvcNm(srvcNm.trim());
//		userLog.setMethodNm(methodNm.trim());

		UserLog vo = userLogService.selectUserLog(userLog);
		//System.out.println("==================================userLog vo=========" + vo);
		resultMap.put("result", vo);
		resultVO.setResult(resultMap);
		
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	    resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		
	
		return resultVO;
	}

}
