package com.sbank.admin.common.domain;

import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

/**--------------------------------------------------------------------
* ■RestTemplate API 모델 ■payletter ■2019-11-07
--------------------------------------------------------------------**/
@Data
public class RestTemplateModel {
    public HttpHeaders                          httpHeaders;         // 헤더
    public String                               strApiUrl;           // 호출 uri
    public LinkedMultiValueMap<String, Object>  multiValueMap;       // 데이터 변수
    public HttpEntity<?>                        requestData;         // 요청 데이터

    public RestTemplateModel() {
        httpHeaders     = new HttpHeaders();
        multiValueMap   = new LinkedMultiValueMap<>();
    }
}
