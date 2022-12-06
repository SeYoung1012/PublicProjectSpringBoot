package egovframework.com.sec.ram.web;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.ResponseCode;
import egovframework.com.cmm.SessionVO;
import egovframework.com.cmm.annotation.IncludedInfo;
import egovframework.com.cmm.service.ResultVO;
import egovframework.com.sec.ram.service.AuthorManage;
import egovframework.com.sec.ram.service.AuthorManageVO;
import egovframework.com.sec.ram.service.EgovAuthorManageService;

import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

import java.util.HashMap;

import javax.annotation.Resource;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springmodules.validation.commons.DefaultBeanValidator;

/**
 * 권한관리에 관한 controller 클래스를 정의한다.
 * @author 공통서비스 개발팀 이문준
 * @since 2009.06.01
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2009.03.11  이문준          최초 생성
 *   2011.8.26	정진오			IncludedInfo annotation 추가s
 *
 * </pre>
 */
 

@Controller
@SessionAttributes(types=SessionVO.class)
@RestController
public class EgovAuthorManageControllerAPI {

    @Resource(name="egovMessageSource")
    EgovMessageSource egovMessageSource;
    
    @Resource(name = "egovAuthorManageService")
    private EgovAuthorManageService egovAuthorManageService;
    
    /** EgovPropertyService */
    @Resource(name = "propertiesService")
    protected EgovPropertyService propertiesService;
    
    @Autowired
	private DefaultBeanValidator beanValidator;
    
    /**
	 * 권한 목록화면 이동
	 * @return String
	 * @exception Exception
	 */
    @RequestMapping("/sec/ram/EgovAuthorListView.do")
    public String selectAuthorListView()
            throws Exception {
        return "egovframework/com/sec/ram/EgovAuthorManage";
    }    
    
    /**
	 * 권한 목록을 조회한다
	 * @param authorManageVO AuthorManageVO
	 * @return String
	 * @exception Exception
	 */
    @IncludedInfo(name="권한관리", listUrl="/sec/ram/EgovAuthorList.do", order = 60,gid = 20)
    @RequestMapping(value="/sec/ram/EgovAuthorListAPI.do")
    public ResultVO selectAuthorList(AuthorManageVO authorManageVO) throws Exception {
    	ResultVO resultVO = new ResultVO();
    	HashMap<String,Object> result = new HashMap<>();
    	/** EgovPropertyService.sample */
    	authorManageVO.setPageUnit(propertiesService.getInt("pageUnit"));
    	authorManageVO.setPageSize(propertiesService.getInt("pageSize"));
    	
    	/** paging */
    	PaginationInfo paginationInfo = new PaginationInfo();
		paginationInfo.setCurrentPageNo(authorManageVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(authorManageVO.getPageUnit());
		paginationInfo.setPageSize(authorManageVO.getPageSize());
		
		authorManageVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		authorManageVO.setLastIndex(paginationInfo.getLastRecordIndex());
		authorManageVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());
		
		authorManageVO.setAuthorManageList(egovAuthorManageService.selectAuthorList(authorManageVO));
        result.put("authorList", authorManageVO.getAuthorManageList());
        
        int totCnt = egovAuthorManageService.selectAuthorListTotCnt(authorManageVO);
		paginationInfo.setTotalRecordCount(totCnt);
        result.put("paginationInfo", paginationInfo);
        //model.addAttribute("message", egovMessageSource.getMessage("success.common.select"));
        resultVO.setResult(result);
        return resultVO;
    } 
    
    /**
	 * 권한 세부정보를 조회한다.
	 * @param authorCode String
	 * @param authorManageVO AuthorManageVO
	 * @return String
	 * @exception Exception
	 */   
    @RequestMapping(value="/sec/ram/EgovAuthorAPI.do")
    public ResultVO selectAuthor(String authorCode) throws Exception {
    	ResultVO resultVO = new ResultVO();
    	AuthorManageVO authorManageVO = new AuthorManageVO();
    	authorManageVO.setAuthorCode(authorCode);
    	HashMap<String,Object> result = new HashMap<>();
    	result.put("result", egovAuthorManageService.selectAuthor(authorManageVO));
    	resultVO.setResult(result);
    	return resultVO;
    }     

    /**
	 * 권한 등록화면 이동
	 * @return String
	 * @exception Exception
	 */     
    @RequestMapping("/sec/ram/EgovAuthorInsertView.do")
    public String insertAuthorView(@ModelAttribute("authorManage") AuthorManage authorManage)
            throws Exception {
        return "egovframework/com/sec/ram/EgovAuthorInsert";
    }
    
    /**
	 * 권한 세부정보를 등록한다.
	 * @param authorManage AuthorManage
	 * @param bindingResult BindingResult
	 * @return String
	 * @exception Exception
	 */ 
    @RequestMapping(value="/sec/ram/EgovAuthorInsertAPI.do")
    public ResultVO insertAuthor(@RequestBody AuthorManage authorManage, BindingResult bindingResult) throws Exception {
    	ResultVO resultVO = new ResultVO();
    	beanValidator.validate(authorManage, bindingResult); //validation 수행
    	
		if (bindingResult.hasErrors()) { 
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		} else {
	    	egovAuthorManageService.insertAuthor(authorManage);
	    	resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
			return resultVO;
		}
    }
    
    /**
	 * 권한 세부정보를 수정한다.
	 * @param authorManage AuthorManage
	 * @param bindingResult BindingResult
	 * @return String
	 * @exception Exception
	 */   
    @RequestMapping(value="/sec/ram/EgovAuthorUpdateAPI.do")
    public ResultVO updateAuthor(@RequestBody AuthorManage authorManage, BindingResult bindingResult) throws Exception {
    	ResultVO resultVO = new ResultVO();
    	beanValidator.validate(authorManage, bindingResult); //validation 수행
    	
		if (bindingResult.hasErrors()) {
			resultVO.setResultCode(ResponseCode.AUTH_ERROR.getCode());
	        resultVO.setResultMessage(ResponseCode.AUTH_ERROR.getMessage());
	        
		    return resultVO;
		} else {
	    	egovAuthorManageService.updateAuthor(authorManage);
	    	resultVO.setResultCode(ResponseCode.SUCCESS.getCode());
	        resultVO.setResultMessage(ResponseCode.SUCCESS.getMessage());
			return resultVO;
		}
    }    

    /**
	 * 권한 세부정보를 삭제한다.
	 * @param authorManage AuthorManage
	 * @return String
	 * @exception Exception
	 */  
    @RequestMapping(value="/sec/ram/EgovAuthorDeleteAPI.do")
    public void deleteAuthor(AuthorManage authorManage) throws Exception {
    	System.out.println(authorManage.getAuthorCode());
    	egovAuthorManageService.deleteAuthor(authorManage);
    }   
    
    /**
	 * 권한목록을 삭제한다.
	 * @param authorCodes String
	 * @param authorManage AuthorManage
	 * @return String
	 * @exception Exception
	 */  
    @RequestMapping(value="/sec/ram/EgovAuthorListDeleteAPI.do")
    public void deleteAuthorList(String authorCodes) throws Exception {
    	String [] strAuthorCodes = authorCodes.split(",");
    	System.out.println(strAuthorCodes[0]);
    	System.out.println(strAuthorCodes[1]);
    	AuthorManage authorManage = new AuthorManage();
    	for(int i=0; i<strAuthorCodes.length;i++) {
    		authorManage.setAuthorCode(strAuthorCodes[i]);
    		egovAuthorManageService.deleteAuthor(authorManage);
    	}
    }    
    
    /**
	 * 권한제한 화면 이동
	 * @return String
	 * @exception Exception
	 */
    @RequestMapping("/sec/ram/accessDenied.do")
    public String accessDenied()
            throws Exception {
        return "egovframework/com/sec/accessDenied";
    } 
}
