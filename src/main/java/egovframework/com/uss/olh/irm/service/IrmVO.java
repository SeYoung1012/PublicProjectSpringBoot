package egovframework.com.uss.olh.irm.service;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * 서비스 개선요청을 처리하는 VO 클래스
 * @author 디지털정부서비스 개방 플랫폼 김세영
 * @since 2022.11.18
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2022.11.18  김세영          최초 생성
 *
 * </pre>
 */




public class IrmVO extends IrmDefaultVO {

	private static final long serialVersionUID = 1L;

	/** 서비스 변경요청 ID */
	private String srvcId;

	/** 서비스 변경요청 제목 */
	private String srvcTtl;

	/** 서비스 변경요청 내용 */
	private String srvcCn;
	
	/** 작성자 명 */
	private String wrtNm;
	
	/** 작성일자 */
	private String wrtYmd;

		
	/** 작성비밀번호 */
	private String wrtPswd;


	/** 답변내용 */
	private String ansCn;

	
	/** 답변자명 */
	private String answrNm;

	
	/** 답변일자 */
	private String ansYmd;
	
	
	/** 요청처리 상태코드 */
	private String dmndPrcsSttsCd;
	
	
	/** 사용여부 */
	private String useYn;
	
	/** 첨부파일ID */
	private String atchFileId;
	
	
	/** 최초등록자ID */
	private String frstRgtrId;
	
	
	/** 최초등록시점 */
	private String frstRegDt;

	
	/** 최종수정자ID */
	private String lastChnrgId;
	
	
	/** 최종수정시점 */
	private String lastChgDt;


	public String getSrvcId() {
		return srvcId;
	}


	public void setSrvcId(String srvcId) {
		this.srvcId = srvcId;
	}


	public String getSrvcTtl() {
		return srvcTtl;
	}


	public void setSrvcTtl(String srvcTtl) {
		this.srvcTtl = srvcTtl;
	}


	public String getSrvcCn() {
		return srvcCn;
	}


	public void setSrvcCn(String srvcCn) {
		this.srvcCn = srvcCn;
	}


	public String getWrtNm() {
		return wrtNm;
	}


	public void setWrtNm(String wrtNm) {
		this.wrtNm = wrtNm;
	}


	public String getWrtYmd() {
		return wrtYmd;
	}


	public void setWrtYmd(String wrtYmd) {
		this.wrtYmd = wrtYmd;
	}


	public String getWrtPswd() {
		return wrtPswd;
	}


	public void setWrtPswd(String wrtPswd) {
		this.wrtPswd = wrtPswd;
	}


	public String getAnsCn() {
		return ansCn;
	}


	public void setAnsCn(String ansCn) {
		this.ansCn = ansCn;
	}


	public String getAnswrNm() {
		return answrNm;
	}


	public void setAnswrNm(String answrNm) {
		this.answrNm = answrNm;
	}


	public String getAnsYmd() {
		return ansYmd;
	}


	public void setAnsYmd(String ansYmd) {
		this.ansYmd = ansYmd;
	}


	public String getDmndPrcsSttsCd() {
		return dmndPrcsSttsCd;
	}


	public void setDmndPrcsSttsCd(String dmndPrcsSttsCd) {
		this.dmndPrcsSttsCd = dmndPrcsSttsCd;
	}


	public String getUseYn() {
		return useYn;
	}


	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}


	public String getAtchFileId() {
		return atchFileId;
	}


	public void setAtchFileId(String atchFileId) {
		this.atchFileId = atchFileId;
	}


	public String getFrstRgtrId() {
		return frstRgtrId;
	}


	public void setFrstRgtrId(String frstRgtrId) {
		this.frstRgtrId = frstRgtrId;
	}


	public String getFrstRegDt() {
		return frstRegDt;
	}


	public void setFrstRegDt(String frstRegDt) {
		this.frstRegDt = frstRegDt;
	}


	public String getLastChnrgId() {
		return lastChnrgId;
	}


	public void setLastChnrgId(String lastChnrgId) {
		this.lastChnrgId = lastChnrgId;
	}


	public String getLastChgDt() {
		return lastChgDt;
	}


	public void setLastChgDt(String lastChgDt) {
		this.lastChgDt = lastChgDt;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	/**
	 * toString 메소드를 대치한다.
	 */
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	
	
}
