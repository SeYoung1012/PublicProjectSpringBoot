package egovframework.com.uss.olh.dat.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.egovframe.rte.fdl.idgnr.EgovIdGnrService;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.EgovProperties;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.uss.olh.dat.service.DataVO;
import egovframework.com.uss.olh.dat.service.EgovDataService;
import egovframework.com.utl.fcc.service.EgovStringUtil;

/**
*
* DATA내용을 처리하는 비즈니스 구현 클래스
* @author 공통서비스 개발팀 박정규
* @since 2009.04.01
* @version 1.0
* @see
*
* <pre>
* << 개정이력(Modification Information) >>
*
*  수정일                수정자           수정내용
*  ----------   --------   ---------------------------
*  2009.04.01   박정규            최초 생성
*  2011.08.26   정진오            IncludedInfo annotation 추가
*  2016.08.03   김연호            표준프레임워크 3.6 개선
*  2020.10.27   신용호            파일 업로드 수정 (multiRequest.getFiles)
*  2021.07.29   정진호            경로 오류 수정
*
* </pre>
*/

@RestController
public class EgovDataController {
	
	@Resource(name = "EgovDataService")
	private EgovDataService egovDataService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;

	// 첨부파일 관련
	@Resource(name = "EgovFileMngService")
	private EgovFileMngService fileMngService;

	@Resource(name = "EgovFileMngUtil")
	private EgovFileMngUtil fileUtil;

	/** EgovMessageSource */
	@Resource(name = "egovMessageSource")
    EgovMessageSource egovMessageSource;
    
    /** Message ID Generation */
    @Resource(name = "egovDataManageIdGnrService")
    private EgovIdGnrService egovDataIdGnrService;

	// Validation 관련
	@Autowired
    private DefaultBeanValidator beanValidator;
    
    
    /**
     * DATA 목록을 조회한
     * @param searchVO
     * @param model
     * @return	"/uss/olh/data/EgovDataList"
	 * @throws Exception
	 */
	@IncludedInfo(name = "DATA관리", order = 540, gid = 50)
	@RequestMapping(value = "/uss/olh/dat/selectDataListAPI.do")
    public ResultVO selectDataList(@ModelAttribute("searchVO") DataVO searchVO, ResultVO resultVO) throws Exception {

        Map<String, Object> resultMap = new HashMap<>();

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

        List<?> DataList = egovDataService.selectDataList(searchVO);
        resultMap.put("resultList", DataList);
        int totCnt = egovDataService.selectDataListCnt(searchVO);
        paginationInfo.setTotalRecordCount(totCnt);
        resultMap.put("paginationInfo", paginationInfo);
        resultVO.setResult(resultMap);
        return resultVO;
    }
	
	/**
	 * DATA 목록에 대한 상세정보를 조회한다.
	 * @param dataVO
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/dat/EgovDataDetail"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/selectDataDetailAPI.do")
	public ResultVO selectDataDetail(DataVO dataVO, @ModelAttribute("searchVO") DataVO searchVO, ResultVO resultVO) throws Exception {

		DataVO vo = egovDataService.selectDataDetail(searchVO);
        Map<String, Object> resultMap = new HashMap<>();
        // 첨부파일 확인
		if (vo != null && vo.getAtchFileId() != null && !vo.getAtchFileId().isEmpty()) {
			FileVO fileVO = new FileVO();
			fileVO.setAtchFileId(vo.getAtchFileId());
			List<FileVO> resultFiles = fileMngService.selectFileInfs(fileVO);
			resultMap.put("resultFiles", resultFiles);
		}
		resultMap.put("result", vo);
        resultVO.setResult(resultMap);
		return resultVO;
	}
	
	/**
	 * DATA를 등록하기 위한 전 처리
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/dat/EgovDataRegist"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/insertDataView.do")
	public String insertDataView(@ModelAttribute("searchVO") DataVO searchVO, Model model) throws Exception {

		model.addAttribute("dataVO", new DataVO());
		
    	// 파일업로드 제한
    	String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
    	String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

        model.addAttribute("fileUploadExtensions", whiteListFileUploadExtensions);
        model.addAttribute("fileUploadMaxSize", fileUploadMaxSize);

		return "egovframework/com/uss/olh/faq/EgovFaqRegist";

	}
	
	/**
	 * DATA를 등록한다.
	 * @param multiRequest
	 * @param searchVO
	 * @param dataVO
	 * @param bindingResult
	 * @return	"forward:/uss/olh/dat/selectDataList.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/insertData.do")
	public ResultVO insertDataCn(final MultipartHttpServletRequest multiRequest, // 첨부파일을 위한...
            @ModelAttribute("searchVO") DataVO searchVO, @ModelAttribute("dataManageVO") DataVO dataVO,
            BindingResult bindingResult, ResultVO resultVO) throws Exception {
		beanValidator.validate(dataVO, bindingResult);

        if (bindingResult.hasErrors()) {
            resultVO.setResultCode(0);
			return resultVO;
        } else {
            // 첨부파일 관련 첨부파일ID 생성
            List<FileVO> result = null;
            String atchFileId = null;

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            // final List<MultipartFile> files = multiRequest.getFiles("atchFileId");
            if (!files.isEmpty()) {
                result = fileUtil.parseFileInf(files, "DAT_", 0, "", "");
                atchFileId = fileMngService.insertFileInfs(result); // 파일이 생성되고나면 생성된 첨부파일 ID를 리턴한다.
            }

            // 리턴받은 첨부파일ID를 셋팅한다..
            dataVO.setAtchFileId(atchFileId); // 첨부파일 ID
            // 로그인VO에서 사용자 정보 가져오기
            LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();

            String frstRegisterId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());

            dataVO.setFrstRegisterId(frstRegisterId); // 최초등록자ID
            dataVO.setLastUpdusrId(frstRegisterId); // 최종수정자ID
            egovDataService.insertData(dataVO);
            resultVO.setResultMessage(egovMessageSource.getMessage("success.common.insert"));
            resultVO.setResultCode(200);

            return resultVO;
        }
	}
	
	/**
	 * DATA를 수정하기 위한 전 처리
	 * @param dataId
	 * @param searchVO
	 * @param model
	 * @return	"/uss/olh/dat/EgovDataUpdt"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/updateDataView.do")
	public String updateDataView(@RequestParam("dataId") String dataId, @ModelAttribute("searchVO") DataVO searchVO, ModelMap model) throws Exception {

		DataVO dataVO = new DataVO();

		// Primary Key 값 세팅
		dataVO.setDataId(dataId);

		// 변수명은 CoC 에 따라 JSTL사용을 위해
		model.addAttribute("dataVO", egovDataService.selectDataDetail(dataVO));

    	// 파일업로드 제한
    	String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
    	String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

        model.addAttribute("fileUploadExtensions", whiteListFileUploadExtensions);
        model.addAttribute("fileUploadMaxSize", fileUploadMaxSize);
		
		return "egovframework/com/uss/olh/faq/EgovFaqUpdt";
	}

	/**
	 * DATA를 수정처리한다.
	 * @param atchFileAt
	 * @param multiRequest
	 * @param searchVO
	 * @param dataVO
	 * @param bindingResult
	 * @param model
	 * @return	"forward:/uss/olh/dat/selectDataList.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/updateDataAPI.do")
	public ResultVO updateDataCn(final MultipartHttpServletRequest multiRequest,@RequestParam("dataId") String dataId,
            @ModelAttribute("searchVO") DataVO searchVO, @ModelAttribute("dataVO") DataVO dataVO,
            BindingResult bindingResult, ResultVO resultVO)
			throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
		// Validation
		beanValidator.validate(dataVO, bindingResult);
        if (bindingResult.hasErrors()) {
            resultVO.setResultCode(0);
            return resultVO;
        } else {
            // Primary Key 값 세팅
            dataVO.setDataId(dataId);

            // 변수명은 CoC 에 따라 JSTL사용을 위해
            resultMap.put("dataVO", egovDataService.selectDataDetail(dataVO));

            // 파일업로드 제한
            String whiteListFileUploadExtensions = EgovProperties.getProperty("Globals.fileUpload.Extensions");
            String fileUploadMaxSize = EgovProperties.getProperty("Globals.fileUpload.maxSize");

            resultMap.put("fileUploadExtensions", whiteListFileUploadExtensions);
            resultMap.put("fileUploadMaxSize", fileUploadMaxSize);

            // 첨부파일 관련 ID 생성 start....
            String atchFileId = dataVO.getAtchFileId();

            final Map<String, MultipartFile> files = multiRequest.getFileMap();
            // final List<MultipartFile> files = multiRequest.getFiles("file_1");
            if (!files.isEmpty()) {
                if (atchFileId == null || "".equals(atchFileId)) {
                    List<FileVO> result = fileUtil.parseFileInf(files, "DAT_", 0, atchFileId, "");
                    atchFileId = fileMngService.insertFileInfs(result);
                    dataVO.setAtchFileId(atchFileId);
                } else {
                    FileVO fvo = new FileVO();
                    fvo.setAtchFileId(atchFileId);
                    int cnt = fileMngService.getMaxFileSN(fvo);
                    List<FileVO> _result = fileUtil.parseFileInf(files, "DAT_", cnt, atchFileId, "");
                    fileMngService.updateFileInfs(_result);
                }
            }
            // 첨부파일 관련 ID 생성 end...

            // 로그인VO에서 사용자 정보 가져오기
            LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
            String lastUpdusrId = loginVO == null ? "" : EgovStringUtil.isNullToString(loginVO.getUniqId());
            dataVO.setLastUpdusrId(lastUpdusrId); // 최종수정자ID

            egovDataService.updateData(dataVO);
            resultVO.setResult(resultMap);
            resultVO.setResultCode(200);
            resultVO.setResultMessage(egovMessageSource.getMessage("success.common.update"));
            return resultVO;
        }
	}

	/**
	 * DATA를 삭제처리한다.
	 * @param dataVO
	 * @param searchVO
	 * @return	"forward:/uss/olh/dat/selectDataList.do"
	 * @throws Exception
	 */
	@RequestMapping("/uss/olh/dat/deleteDataAPI.do")
	public ResultVO deleteData(ResultVO resultVO,DataVO dataVO, @ModelAttribute("searchVO") DataVO searchVO) throws Exception {

		// 첨부파일 삭제를 위한 ID 생성 start....
		String _atchFileId = dataVO.getAtchFileId();

		egovDataService.deleteData(dataVO);

		// 첨부파일을 삭제하기 위한  Vo
		FileVO fvo = new FileVO();
		fvo.setAtchFileId(_atchFileId);

		fileMngService.deleteAllFileInf(fvo);
        // 첨부파일 삭제 End.............
        resultVO.setResultMessage(egovMessageSource.getMessage("success.common.delete"));

		return resultVO;
	}
	
}
