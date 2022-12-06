package egovframework.com.sym.log.plg.web;

import java.util.HashMap;
import java.util.Map;

import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.sym.log.plg.service.EgovPrivacyLogService;
import egovframework.com.sym.log.plg.service.PrivacyLog;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Class Name : EgovPrivacyLogController.java
 * @Description : 개인정보 조회 이력 관리를 위한 Controller 클래스
 * @Modification Information
 *
 *    수정일         수정자         수정내용
 *    -------        -------     -------------------
 *    2014.09.11	표준프레임워크		최초생성
* @author Vincent Han
 * @since 2014.09.11
 * @version 3.5
 */
@RestController
public class EgovPrivacyLogControllerAPI {

	@Resource(name="egovPrivacyLogService")
	private EgovPrivacyLogService privacyLogService;

	@Resource(name="propertiesService")
	protected EgovPropertyService propertyService;

	/**
	 * 개인정보조회 로그 목록 조회
	 *
	 * @param privacyLog
	 * @return sym/log/plg/EgovPrivacyLogList
	 * @throws Exception
	 */
	@IncludedInfo(name="개인정보조회로그관리", listUrl="/sym/log/plg/SelectPrivacyLogListAPI.do", order = 1085 ,gid = 60)
	@RequestMapping(value="/sym/log/plg/SelectPrivacyLogListAPI.do")
	public ResultVO selectPrivacyLogList(@ModelAttribute("searchVO") PrivacyLog privacyLog
		) throws Exception{
		
		
		ResultVO resultVO = new ResultVO();
        Map<String, Object> resultMap = new HashMap<>();
		
		
		privacyLog.setPageUnit(propertyService.getInt("pageUnit"));
		privacyLog.setPageSize(propertyService.getInt("pageSize"));

		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(privacyLog.getPageIndex());
		paginationInfo.setRecordCountPerPage(privacyLog.getPageUnit());
		paginationInfo.setPageSize(privacyLog.getPageSize());

		privacyLog.setFirstIndex(paginationInfo.getFirstRecordIndex());
		privacyLog.setLastIndex(paginationInfo.getLastRecordIndex());
		privacyLog.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		Map<String, Object> map = privacyLogService.selectPrivacyLogList(privacyLog);
		int totalCount = Integer.parseInt((String)map.get("resultCnt"));

		resultMap.put("resultList", map.get("resultList"));
	    resultMap.put("resultCnt", map.get("resultCnt"));
	    resultMap.put("frm", privacyLog);
		

		paginationInfo.setTotalRecordCount(totalCount);
		resultMap.put("paginationInfo", paginationInfo);
		
		resultVO.setResult(resultMap);
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());

		return resultVO;
	}

	/**
	 * 개인정보조회 로그 상세 조회
	 *
	 * @param privacyLog
	 * @param model
	 * @return sym/log/plg/EgovPrivacyLogInqire
	 * @throws Exception
	 */
	@RequestMapping(value="/sym/log/plg/SelectPrivacyLogDetailAPI.do")
	public ResultVO selectWebLog(@ModelAttribute("searchVO") PrivacyLog privacyLog
			) throws Exception{
		
		 ResultVO resultVO = new ResultVO();
	     Map<String, Object> resultMap = new HashMap<>();

	    PrivacyLog vo = privacyLogService.selectPrivacyLog(privacyLog);
	 	resultMap.put("result", vo);
		resultVO.setResult(resultMap);
		
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	    resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());

		return resultVO;
	}

}
