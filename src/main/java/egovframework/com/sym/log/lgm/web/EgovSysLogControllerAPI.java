package egovframework.com.sym.log.lgm.web;

import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.sym.log.lgm.service.EgovSysLogService;
import egovframework.com.sym.log.lgm.service.SysLog;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class EgovSysLogControllerAPI {

    @Resource(name = "EgovSysLogService")
    private EgovSysLogService sysLogService;

    /**
     * EgovPropertyService
     */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

    /**
     * 시스템 로그 목록 조회
     *
     * @param sysLog
     * @return resultVO
     * @throws Exception
     */
    @IncludedInfo(name = "로그관리", listUrl = "/sym/log/lgm/SelectSysLogList.do", order = 1030, gid = 60)
    @RequestMapping(value = "/sym/log/lgm/SelectSysLogList.do")
    public ResultVO selectSysLogInf(SysLog sysLog) throws Exception {
        ResultVO resultVO = new ResultVO();
        Map<String, Object> resultMap = new HashMap<>();

        /** EgovPropertyService.sample */
        sysLog.setPageUnit(propertiesService.getInt("pageUnit"));
        sysLog.setPageSize(propertiesService.getInt("pageSize"));

        /** pageing */
        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(sysLog.getPageIndex());
        paginationInfo.setRecordCountPerPage(sysLog.getPageUnit());
        paginationInfo.setPageSize(sysLog.getPageSize());

        sysLog.setFirstIndex(paginationInfo.getFirstRecordIndex());
        sysLog.setLastIndex(paginationInfo.getLastRecordIndex());
        sysLog.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        HashMap<?, ?> _map = (HashMap<?, ?>) sysLogService.selectSysLogInf(sysLog);
        int totCnt = Integer.parseInt((String) _map.get("resultCnt"));

        resultMap.put("resultList", _map.get("resultList"));
        resultMap.put("resultCnt", _map.get("resultCnt"));
        resultMap.put("frm", sysLog);

        paginationInfo.setTotalRecordCount(totCnt);
        resultMap.put("paginationInfo", paginationInfo);

        sysLogService.printLog(_map.get("srvcNm"), _map.get("methodNm"));

        resultVO.setResult(resultMap);
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());

        return resultVO;
    }

    /**
     * 시스템 로그 상세 조회
     *
     * @param sysLog
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping("/sym/log/lgm/SelectSysLogDetailAPI.do")
    public ResultVO selectSysLog(@ModelAttribute("searchVO") SysLog sysLog, @RequestParam("requstId") String requstId) throws Exception {
        ResultVO resultVO = new ResultVO();
        Map<String, Object> resultMap = new HashMap<>();

        sysLog.setRequstId(requstId.trim());
        SysLog vo = sysLogService.selectSysLog(sysLog);
        resultMap.put("result", vo);
        resultVO.setResult(resultMap);

        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());

        return resultVO;
    }


}
