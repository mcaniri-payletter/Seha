package com.sbank.admin.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**--------------------------------------------------------------------
 * ■DDLB 모델 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class TypeCode extends BaseModel {

    private String  codeID;
    private String  codeName;
    private String  messageCode;
    private Boolean useFlag;
    private Boolean displayFlag;
    private String  filter;

    public TypeCode() {}

    public TypeCode(Boolean useFlag) {
        this(useFlag, null);
    }

    public TypeCode(String filter) {
        this(null, filter);
    }

    public TypeCode(Boolean useFlag, String filter) {
        this.useFlag = useFlag;
        this.filter = filter;
    }

    public TypeCode(String filter, Boolean displayFlag) {
        this.filter = filter;
        this.displayFlag = displayFlag;
    }

    @Override
    public String toString() {
        return "{\"strCodeID\":\"" + codeID + "\", \"strCodeName\":\"\" + codeName + \"\", \"strMsgCode\":\"" + messageCode + "\"}";
    }
}
