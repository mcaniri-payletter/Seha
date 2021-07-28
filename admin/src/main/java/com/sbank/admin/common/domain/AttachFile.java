package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■첨부 파일 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class AttachFile extends BaseRes{
    private int fileNo;
    private long fileSize;

    private String filePath;
    private String fileNm;
    private String fileUploadNm;
    private String regDate;
    private String regId;

    private Boolean useFg;
}