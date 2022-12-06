package egovframework.com.uss.olh.ntc.service;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * NOTICE를 처리하는 VO 클래스
 * 
 * @author 디지털정부서비스 개방 플랫폼 구축 윤명석
 * @since 2022.10.26
 * @version 1.0
 * @see
 *
 *      <pre>
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2022-10-26  윤명석          최초 생성
 *
 *      </pre>
 */
public class NoticeVO extends NoticeDefaultVO {

    private static final long serialVersionUID = 1L;

    /** NOTICE ID */
    private String ntcId;

    /** 제목 */
    private String ntcSj;

    /** 내용 */
    private String ntcCn;

    /** 조회횟수 */
    private String inqireCo;

    /** 첨부파일ID */
    private String atchFileId;

    /** 최초등록시점 */
    private String frstRegisterPnttm;

    /** 최초등록자ID */
    private String frstRegisterId;

    /** 최종수정시점 */
    private String lastUpdusrPnttm;

    /** 최종수정자ID */
    private String lastUpdusrId;

    /** 작성자 이름 */
    private String ntcNm;

    /** 상위노출여부 */
    private String topAt;

    public String getNtcId() {
        return this.ntcId;
    }

    public void setNtcId(String ntcId) {
        this.ntcId = ntcId;
    }

    public String getNtcSj() {
        return this.ntcSj;
    }

    public void setNtcSj(String ntcSj) {
        this.ntcSj = ntcSj;
    }

    public String getNtcCn() {
        return this.ntcCn;
    }

    public void setNtcCn(String ntcCn) {
        this.ntcCn = ntcCn;
    }

    public String getInqireCo() {
        return this.inqireCo;
    }

    public void setInqireCo(String inqireCo) {
        this.inqireCo = inqireCo;
    }

    public String getAtchFileId() {
        return this.atchFileId;
    }

    public void setAtchFileId(String atchFileId) {
        this.atchFileId = atchFileId;
    }

    public String getFrstRegisterPnttm() {
        return this.frstRegisterPnttm;
    }

    public void setFrstRegisterPnttm(String frstRegisterPnttm) {
        this.frstRegisterPnttm = frstRegisterPnttm;
    }

    public String getFrstRegisterId() {
        return this.frstRegisterId;
    }

    public void setFrstRegisterId(String frstRegisterId) {
        this.frstRegisterId = frstRegisterId;
    }

    public String getLastUpdusrPnttm() {
        return this.lastUpdusrPnttm;
    }

    public void setLastUpdusrPnttm(String lastUpdusrPnttm) {
        this.lastUpdusrPnttm = lastUpdusrPnttm;
    }

    public String getLastUpdusrId() {
        return this.lastUpdusrId;
    }

    public void setLastUpdusrId(String lastUpdusrId) {
        this.lastUpdusrId = lastUpdusrId;
    }

    public String getTopAt() {
        return this.topAt;
    }

    public void setTopAt(String topAt) {
        this.topAt = topAt;
    }

    public String getNtcNm() {
        return this.ntcNm;
    }

    public void setNtcNm(String ntcNm) {
        this.ntcNm = ntcNm;
    }

    /**
     * toString 메소드를 대치한다.
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
