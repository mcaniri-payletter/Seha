package com.sbank.admin.common.security;

import com.sbank.admin.common.util.CommonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■XSSRequestWrapper ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
public class XSSRequestWrapper extends HttpServletRequestWrapper {
	private static final Logger logger = LoggerFactory.getLogger("FILTER");

	private byte[] bytRetRequestBody;

	private static final String ENCODING = "UTF-8";

    /**--------------------------------------------------------------------
     * ■생성자(RequestBody로 파라메터를 처리하는 경우 생성자에서 XSS 처리) ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
	@SuppressWarnings("unchecked")
	public XSSRequestWrapper(HttpServletRequest httpServletRequest) throws IOException {
        super(httpServletRequest);

        InputStream         inputStream     = null;
        ObjectMapper        objectMapper     = null;
        Map<String, Object> retMap = null;

        JSONObject before = null;
        JSONObject after  = null;

        String strBody = "";

		try {
			inputStream = super.getInputStream();
			objectMapper = new ObjectMapper();

			bytRetRequestBody = IOUtils.toByteArray(inputStream);

			strBody  = new String(bytRetRequestBody, ENCODING);
			after = new JSONObject();

			//Json 값이 없는 경우 또는 Json 값이 아닌 경우
			if(!CommonUtil.isJson(strBody)){
				bytRetRequestBody = strBody.getBytes(ENCODING);
				return;
			}

			//Json 값 파싱
			before = CommonUtil.getJson(strBody);
			for(Iterator<Object> objIterator = before.keySet().iterator(); objIterator.hasNext();){
				String strKey = (String) objIterator.next();

				if(before.get(strKey) instanceof String) {
					after.put(strKey, escapeXSS((String)before.get(strKey)));
				} else {
					after.put(strKey, before.get(strKey));
				}
			}

			strBody = objectMapper.writeValueAsString(retMap);
			strBody = after.toJSONString();
			bytRetRequestBody = strBody.getBytes(ENCODING);
		} catch (IOException ex) {
            CommonUtil.globalExceptionHandle(ex, logger);
		} finally {
			retMap = null;
			objectMapper     = null;
			inputStream     = null;
		}
    }

	@Override
    public ServletInputStream getInputStream() throws IOException {
    	return new ServletInputStreamImpl(new ByteArrayInputStream(bytRetRequestBody));
    }

	@Override
    public String[] getParameterValues(String strParameter) {
        String[] arrValues        = super.getParameterValues(strParameter);
        String[] arrEncodedValues = null;

        if (StringUtils.isAnyEmpty(arrValues)) {
            return arrEncodedValues;
        }

        if (arrValues != null) {
			int intCount = arrValues.length;
			arrEncodedValues = new String[intCount];

			for (int i = 0; i < intCount; i++) {
				arrEncodedValues[i] = escapeXSS(arrValues[i]);
			}
		}
        return arrEncodedValues;
    }

	@Override
    public String getParameter(String strParameter) {
        String strValue = super.getParameter(strParameter);

        return escapeXSS(strValue);
    }

    @Override
    public String getHeader(String strName) {
        String strValue = super.getHeader(strName);

        return escapeXSS(strValue);
    }

    /**--------------------------------------------------------------------
     * ■Escape Xss ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private String escapeXSS(String strValue) {
        return strValue == null ? strValue : StringEscapeUtils.escapeHtml4(strValue);
    }
}

/**--------------------------------------------------------------------
 * ■Request Body 데이터 Stream Read 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
class ServletInputStreamImpl extends ServletInputStream {
	private InputStream inputStream;

	public ServletInputStreamImpl(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public int read() throws IOException {
		return inputStream.read();
	}

	@Override
	public int read(byte[] bytValue) throws IOException {
		return inputStream.read(bytValue);
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setReadListener(ReadListener listener) {
		throw new UnsupportedOperationException();
	}
}
