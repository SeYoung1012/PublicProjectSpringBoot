package egovframework.com.uss.ion.pwm.web;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.ion.pwm.service.EgovPopupManageService;
import egovframework.com.uss.ion.pwm.service.PopupManageVO;
import egovframework.com.utl.fcc.service.EgovStringUtil;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springmodules.validation.commons.DefaultBeanValidator;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EgovPopupManageControllerAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(EgovPopupManageController.class);

    @Autowired
    private DefaultBeanValidator beanValidator;

    /**
     * EgovMessageSource
     */
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    /**
     * EgovPropertyService
     */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;

    /**
     * EgovPopupManageService
     */
    @Resource(name = "egovPopupManageService")
    private EgovPopupManageService egovPopupManageService;


    /**
     * 통합 링크 관리를 등록한다.
     *
     * @param popupManageVO
     * @param bindingResult
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping(value = "/uss/ion/pwm/registPopupAPI.do")
    public ResultVO registerPopup(@ModelAttribute PopupManageVO popupManageVO, BindingResult bindingResult) throws Exception {
        ResultVO resultVO = new ResultVO();
        // spring security 사용자 권한 처리
        Map<String, Object> resultMap = new HashMap<>();

        // 서버 validate 체크
        beanValidator.validate(popupManageVO, bindingResult);
        if (bindingResult.hasErrors()) {
            resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
            return resultVO;
        }

        LoginVO user = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

//        if (!EgovUserDetailsHelper.isAuthenticated()) {
//            return handleAuthError(resultVO); // server-side 권한 확인
//        }

        // 아이디 설정
        popupManageVO.setFrstRegisterId(user == null ? "" : EgovStringUtil.isNullToString(user.getUniqId()));
        popupManageVO.setLastUpdusrId(user == null ? "" : EgovStringUtil.isNullToString(user.getUniqId()));

        // 저장
        egovPopupManageService.insertPopup(popupManageVO);

        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());


        return resultVO;
    }

    /**
     * 팝업창 관리 목록을 조회한다.
     *
     * @param resultVO
     * @return
     */
    @RequestMapping(value = "/uss/ion/pwm/listPopupAPI.do")
    public ResultVO retrievePopupList(@ModelAttribute PopupManageVO popupManageVO) throws Exception {

        ResultVO resultVO = new ResultVO();

        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(popupManageVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(propertiesService.getInt("pageUnit"));
        paginationInfo.setPageSize(propertiesService.getInt("pageSize"));

        popupManageVO.setSearchCondition(popupManageVO.getSearchCondition());

        popupManageVO.setSearchKeyword(popupManageVO.getSearchKeyword());
        popupManageVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        popupManageVO.setLastIndex(paginationInfo.getLastRecordIndex());
        popupManageVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

        List<?> resultList = egovPopupManageService.selectPopupList(popupManageVO);

        Map<String, Object> resultMap = new HashMap<>();
        System.out.println("searchKeyword:" + popupManageVO.getSearchKeyword());
        System.out.println("searchCondition:" + popupManageVO.getSearchCondition());

        int totCnt = egovPopupManageService.selectPopupListCount(popupManageVO);
        System.out.println(totCnt);
        paginationInfo.setTotalRecordCount(totCnt);
        resultMap.put("resultList", resultList);
        resultMap.put("paginationInfo", paginationInfo);

        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
        resultVO.setResult(resultMap);

        return resultVO;
    }

    /**
     * 팝업창 정보조회
     *
     * @param resultVO
     * @return
     */
    @RequestMapping(value = "/uss/ion/pwm/ajaxPopupManageInfoAPI.do")
    public ResultVO retrievePopup(HttpServletResponse response, PopupManageVO popupManageVO) throws Exception {
        ResultVO resultVO = new ResultVO();

        Map<String, Object> resultMap = new HashMap<>();

        response.setHeader("Content-Type", "text/html;charset=utf-8");
        //PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));

        PopupManageVO popupManageVOs = egovPopupManageService.selectPopup(popupManageVO);

        String sPrint = "";
        sPrint = popupManageVOs.getFileUrl();
        sPrint = sPrint + "||" + popupManageVOs.getPopupWSize();
        sPrint = sPrint + "||" + popupManageVOs.getPopupHSize();
        sPrint = sPrint + "||" + popupManageVOs.getPopupHlc();
        sPrint = sPrint + "||" + popupManageVOs.getPopupWlc();
        sPrint = sPrint + "||" + popupManageVOs.getStopVewAt();

        resultMap.put("result", sPrint);
        resultVO.setResult(resultMap);
        //out.print(EgovWebUtil.clearXSSMinimum(sPrint));//2022.01 Potential XSS in Servlet
        //out.flush();

        return resultVO;
    }

    /**
     * 통합링크관리를 수정한다.
     *
     * @param popupManageVO
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping(value = "/uss/ion/pwm/updtPopupAPI.do")
    public ResultVO egovPopupManageUpdt(PopupManageVO popupManageVO, BindingResult bindingResult) throws Exception {
        ResultVO resultVO = new ResultVO();
        Map<String, Object> resultMap = new HashMap<>();

        egovPopupManageService.updatePopup(popupManageVO);

        return resultVO;
    }

    /**
     * 수정 위한 상세정보 조회
     *
     * @param popupId
     * @return resultVO
     * @throws Exception
     */
    @RequestMapping("/uss/ion/pwm/detailPopupAPI.do")
    public ResultVO egovPopupManageDetail(PopupManageVO popupManageVO) throws Exception {
        ResultVO resultVO = new ResultVO();

        PopupManageVO vo = egovPopupManageService.selectPopup(popupManageVO);
        System.out.println(vo.getPopupId());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("popupManageVO", vo);

        resultVO.setResult(resultMap);
        return resultVO;

    }

    /**
     * 팝업삭제
     * @param popupManageVO
     * @return
     */
    @RequestMapping("uss/ion/pwm/deletePopupAPI.do")
    public ResultVO egoPopupManageDelete(PopupManageVO popupManageVO) throws Exception{
        ResultVO resultVO = new ResultVO();
        egovPopupManageService.deletePopup(popupManageVO);
        return resultVO;
    }


    private ResultVO handleAuthError(ResultVO resultVO) {
        resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
        return resultVO;
    }


}