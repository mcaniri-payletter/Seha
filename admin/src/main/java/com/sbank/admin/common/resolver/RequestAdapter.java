package com.sbank.admin.common.resolver;

import com.sbank.admin.common.exception.GlobalException;
import net.sf.json.util.JSONUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■Request를 Map으로 변환하기 위한 클래스 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
public class RequestAdapter {
	private static final Logger logger = LoggerFactory.getLogger(RequestAdapter.class);

    /**--------------------------------------------------------------------
     * ■Request 객체에서 파라미터를 추출한 후 Map으로 전환하여 리턴해 준다 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
	@SuppressWarnings("unchecked")
	public Map<String, Object> convert(HttpServletRequest nativeRequest) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String[]> objMap = nativeRequest.getParameterMap();
		Map<String, Object> objTmpMap = new HashMap<String, Object>();

		String contentType = nativeRequest.getHeader("content-type");

		// Header content-type이 application/json이 아닌 경우 변환
		if (contentType == null || contentType.indexOf("application/json") < 0 ) {
			objMap.forEach((strKey, objValues)-> {
				if (strKey != null && !"".equals(strKey)) {
					try {
						String strValue = StringUtils.arrayToCommaDelimitedString(objValues);
						if(JSONUtils.mayBeJSON(strValue)) {
							if (strValue == null || "".equals(strValue)) {
								objTmpMap.put("data", mapper.readValue(strKey, Map.class));
							} else {
								objTmpMap.put("data", mapper.readValue(strValue, Map.class));
							}
						} else {
							objTmpMap.put(strKey,  strValue);
						}
					} catch(IOException ex) {
						logger.error("{}", ex);
						throw new GlobalException(ex);
					}
				}
			});
		}

		return (objTmpMap.containsKey("data"))? (Map<String, Object>)objTmpMap.get("data") : objTmpMap;
	}
}
