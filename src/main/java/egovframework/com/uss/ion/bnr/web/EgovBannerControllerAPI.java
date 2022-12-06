package egovframework.com.uss.ion.bnr.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.ion.bnr.service.Banner;
import egovframework.com.uss.ion.bnr.service.BannerVO;
import egovframework.com.uss.ion.bnr.service.EgovBannerService;
import egovframework.com.utl.fcc.service.EgovStringUtil;

@RestController
public class EgovBannerControllerAPI {
    @Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;

    @Resource(name = "EgovFileMngService")
    private EgovFileMngService fileMngService;

    @Resource(name = "EgovFileMngUtil")
    private EgovFileMngUtil fileUtil;

    @Resource(name = "egovBannerService")
    private EgovBannerService egovBannerService;

    /** Message ID Generation */
    @Resource(name = "egovBannerIdGnrService")
    private EgovIdGnrService egovBannerIdGnrService;

    @Autowired
    private DefaultBeanValidator beanValidator;

    /**
     * 배너를 관리하기 위해 등록된 배너목록을 조회한다.
     * 
     * @param bannerVO - 배너 VO
     * @return String - 리턴 URL
     * @throws Exception
     */
    @IncludedInfo(name = "배너관리", order = 740, gid = 50)
    @RequestMapping("/uss/ion/bnr/selectBannerListAPI.do")
    public ResultVO selectBannerList(@ModelAttribute("bannerVO") BannerVO bannerVO, ResultVO resultVO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        PaginationInfo paginationInfo = new PaginationInfo();
        paginationInfo.setCurrentPageNo(bannerVO.getPageIndex());
        paginationInfo.setRecordCountPerPage(bannerVO.getPageUnit());
        paginationInfo.setPageSize(bannerVO.getPageSize());
        int totCnt = egovBannerService.selectBannerListTotCnt(bannerVO);
        paginationInfo.setTotalRecordCount(totCnt);

        bannerVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
        bannerVO.setLastIndex(paginationInfo.getLastRecordIndex());
        bannerVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
        bannerVO.setSearchKeyword(bannerVO.getSearchKeyword());
        System.out.println(bannerVO);
        resultMap.put("bannerList", egovBannerService.selectBannerList(bannerVO));

        resultMap.put("paginationInfo", paginationInfo);

        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.select"));
        resultVO.setResult(resultMap);
        return resultVO;
    }

    @RequestMapping(value = "/uss/ion/bnr/getBannerAPI.do")
    public ResultVO selectBanner(@RequestParam("bannerId") String bannerId,
            @ModelAttribute("bannerVO") BannerVO bannerVO,
            ResultVO resultVO, FileVO fileVO) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        System.out.println(fileVO);
        bannerVO.setBannerId(bannerId);

        resultMap.put("banner", egovBannerService.selectBanner(bannerVO));
        resultMap.put("message", egovMessageSource.getMessage("success.common.select"));
        resultVO.setResult(resultMap);
        return resultVO;
    }
    
    @RequestMapping("/uss/ion/bnr/addBannerAPI.do")
    public ResultVO insertBanner(ResultVO resultVO,final MultipartHttpServletRequest multiRequest,
            @ModelAttribute("banner") Banner banner,
            @ModelAttribute("bannerVO") BannerVO bannerVO,
            BindingResult bindingResult,SessionStatus status) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();
        beanValidator.validate(banner, bindingResult); // validation 수행

        if (bindingResult.hasErrors()) {
            resultMap.put("bannerVO", bannerVO);
            resultVO.setResult(resultMap);
            return resultVO;
        } else {
            List<FileVO> result = null;

            String uploadFolder = "";
            String bannerImage = "";
            String bannerImageFile = "";
            String atchFileId = "";

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            System.out.println("files:"+files);
            if (!files.isEmpty()) {
                result = fileUtil.parseFileInf(files, "BNR_", 0, "", uploadFolder);
                atchFileId = fileMngService.insertFileInfs(result);

                FileVO vo = result.get(0);
                Iterator<FileVO> iter = result.iterator();

                while (iter.hasNext()) {
                    vo = iter.next();
                    bannerImage = vo.getOrignlFileNm();
                    bannerImageFile = vo.getStreFileNm();
                }
            }

            LoginVO user = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

            banner.setBannerId(egovBannerIdGnrService.getNextStringId());
            banner.setBannerImage(bannerImage);
            banner.setBannerImageFile(atchFileId);
            banner.setUserId(user == null ? "" : EgovStringUtil.isNullToString(user.getId()));
            bannerVO.setBannerId(banner.getBannerId());
            status.setComplete();
            resultMap.put("message", egovMessageSource.getMessage("success.common.insert"));
            resultMap.put("banner", egovBannerService.insertBanner(banner, bannerVO));
            resultVO.setResult(resultMap);
            // return "egovframework/com/uss/ion/bnr/EgovBannerUpdt";
            return resultVO;

        }
        
    }

    /**
     * 기 등록된 배너정보를 삭제한다.
     * 
     * @param banner Banner
     * @return String
     * @exception Exception
     */
    @RequestMapping("/uss/ion/bnr/removeBannerAPI.do")
    public ResultVO deleteBanner(@RequestParam(value = "bannerId") String bannerId,
            Banner banner, SessionStatus status,ResultVO resultVO) throws Exception {

        banner.setBannerId(bannerId);

        egovBannerService.deleteBanner(banner);
        status.setComplete();
        resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.delete"));

        return resultVO;
    }

    /**
     * 기 등록된 배너정보를 수정한다.
     * 
     * @param banner - 배너 model
     * @return String - 리턴 Url
     */
    @SuppressWarnings("unused")
    @RequestMapping(value = "/uss/ion/bnr/updtBannerAPI.do")
    public ResultVO updateBanner(final MultipartHttpServletRequest multiRequest,
            @ModelAttribute("banner") Banner banner,
            BindingResult bindingResult,
            SessionStatus status,
            ResultVO resultVO) throws Exception {
        beanValidator.validate(banner, bindingResult); // validation 수행
        Map<String, Object> resultMap = new HashMap<>();
        if (bindingResult.hasErrors()) {
            resultMap.put("bannerVO", banner);
            resultVO.setResult(resultMap);
            return resultVO;
        } else {

            List<FileVO> result = null;

            String uploadFolder = "";
            String bannerImage = "";
            String bannerImageFile = "";
            String atchFileId = "";

            final Map<String, MultipartFile> files = multiRequest.getFileMap();

            if (!files.isEmpty()) {
                result = fileUtil.parseFileInf(files, "BNR_", 0, "", uploadFolder);
                atchFileId = fileMngService.insertFileInfs(result);

                FileVO vo = null;
                Iterator<FileVO> iter = result.iterator();

                while (iter.hasNext()) {
                    vo = iter.next();
                    bannerImage = vo.getOrignlFileNm();
                    bannerImageFile = vo.getStreFileNm();
                }

                if (vo == null) {
                    banner.setAtchFile(false);
                } else {
                    banner.setBannerImage(bannerImage);
                    banner.setBannerImageFile(atchFileId);
                    banner.setAtchFile(true);

                }
            } else {
                banner.setAtchFile(false);
            }

            LoginVO user = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
            banner.setUserId(user == null ? "" : EgovStringUtil.isNullToString(user.getId()));

            egovBannerService.updateBanner(banner);
            // return "forward:/uss/ion/bnr/getBanner.do";
            resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
            return resultVO;

        }
    }
}
