package egovframework.com.sym.bat.web;

import java.util.HashMap;
import java.util.List;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.sym.bat.service.BatchResult;
import egovframework.com.sym.bat.service.EgovBatchResultService;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배치결과관리에 대한 controller 클래스
 *
 * @author 김진만
 * @since 2010.06.17
 * @version 1.0
 * @updated 17-6-2010 오전 10:27:13
 * @see
 * <pre>
 * == 개정이력(Modification Information) ==
 *
 *   수정일       수정자           수정내용
 *  -------     --------    ---------------------------
 *  2010.06.17   김진만     최초 생성
 *  2011.8.26	정진오			IncludedInfo annotation 추가
 * </pre>
 */

@RestController
public class EgovBatchResultControllerAPI {

	/** egovBatchResultService */
	@Resource(name = "egovBatchResultService")
	private EgovBatchResultService egovBatchResultService;

	/* Property 서비스 */
	@Resource(name = "propertiesService")
	private EgovPropertyService propertyService;

	/*  메세지 서비스 */
	@Resource(name = "egovMessageSource")
	private EgovMessageSource egovMessageSource;

	/** logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(EgovBatchResultControllerAPI.class);

	/**
	 * 배치결과을 삭제한다.
	 * @return 리턴URL
	 *
	 * @param batchResult 삭제대상 배치결과model
	 * @param model		ModelMap
	 * @exception Exception Exception
	 */
	@RequestMapping("/sym/bat/deleteBatchResultAPI.do")
	public ResultVO deleteBatchResult(@RequestBody BatchResult batchResult) throws Exception {
		ResultVO resultVO = new ResultVO();
		Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();
		if (!isAuthenticated) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		}
		egovBatchResultService.deleteBatchResult(batchResult);
		resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
		return resultVO;
	}

	/**
	 * 배치결과정보을 상세조회한다.
	 * @return 리턴URL
	 *
	 * @param batchResult 조회대상 배치결과model
	 * @param model		ModelMap
	 * @exception Exception Exception
	 */
	@RequestMapping("/sym/bat/getBatchResultAPI.do")
	public ResultVO selectBatchResult(BatchResult batchResult) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String,Object> resultMap = new HashMap<>();
		LOGGER.debug(" 조회조건 : {}", batchResult);
		BatchResult result = egovBatchResultService.selectBatchResult(batchResult);
		resultMap.put("result",result);
		LOGGER.debug(" 결과값 : {}", result);
		resultVO.setResult(resultMap);

		return resultVO;
	}

	/**
	 * 배치결과 목록을 조회한다.
	 * @return 리턴URL
	 *
	 * @param searchVO 목록조회조건VO
	 * @param model		ModelMap
	 * @exception Exception Exception
	 */
	//@SuppressWarnings("unchecked")
	@IncludedInfo(name = "배치결과관리", listUrl = "/sym/bat/getBatchResultList.do", order = 1130, gid = 60)
	@RequestMapping("/sym/bat/getBatchResultListAPI.do")
	public ResultVO selectBatchResultList(BatchResult searchVO) throws Exception {
		ResultVO resultVO = new ResultVO();
		HashMap<String,Object> result = new HashMap<>();
		searchVO.setPageUnit(propertyService.getInt("pageUnit"));
		searchVO.setPageSize(propertyService.getInt("pageSize"));

		PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(searchVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(searchVO.getPageUnit());
		paginationInfo.setPageSize(searchVO.getPageSize());

		searchVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		searchVO.setLastIndex(paginationInfo.getLastRecordIndex());
		searchVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		List<BatchResult> resultList = (List<BatchResult>) egovBatchResultService.selectBatchResultList(searchVO);
		int totCnt = egovBatchResultService.selectBatchResultListCnt(searchVO);

		paginationInfo.setTotalRecordCount(totCnt);

		result.put("resultList", resultList);
		result.put("resultcnt", totCnt);
		result.put("paginationInfo", paginationInfo);
		resultVO.setResult(result);

		return resultVO;
	}
	
	@RequestMapping("/sym/bat/getBatchDashboard.do")
	public HashMap<String,Object> selectBatchDashboard() throws Exception {
		HashMap<String,Object> result = new HashMap<>();
		
		List<BatchResult> allList = (List<BatchResult>) egovBatchResultService.selectAllBatchDashBoard();
		List<BatchResult> successList = (List<BatchResult>) egovBatchResultService.selectSuccessBatchDashBoard();
		List<BatchResult> failList = (List<BatchResult>) egovBatchResultService.selectFailBatchDashBoard();

		result.put("allList", allList);
		result.put("successList", successList);
		result.put("failList", failList);

		return result;
	}

}