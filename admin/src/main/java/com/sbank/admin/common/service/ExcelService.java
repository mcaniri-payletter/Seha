package com.sbank.admin.common.service;

import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.util.CommonUtil;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**--------------------------------------------------------------------
 * ■엑셀 서비스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class ExcelService {
    @Value("${template.path.excel}")
    private String EXCEL_TEMPLATE_PATH;

    public void excelDownload(HttpServletResponse response, Map<String, Object> infos, String[]... arrHiddenField) throws Exception {
        InputStream inputStream     = null;
        OutputStream outputStream   = null;
        Workbook workbook           = null;

        try {
            String            templateFileName  = (String) infos.get(GlobalConstants.EXCELDOWNLOAD_TEMPLATE_FILE);
            ClassPathResource objExcelResource  = new ClassPathResource(EXCEL_TEMPLATE_PATH + templateFileName);
            InputStream       excelIS           = objExcelResource.getInputStream();

            String downloadFileName = String.format(GlobalConstants.EXCEL_DOWNLOAD_FILE_NAME_MAP().get(templateFileName), CommonUtil.convertDate(CommonUtil.DateFormat.YYYYMMDD, new Date()));
            downloadFileName        = URLEncoder.encode(downloadFileName, "UTF-8").replace("+", "%20");

            response.setHeader("Content-Type",         "application/octet-stream");
            response.setHeader("Content-Disposition",  "attachment; filename=" + downloadFileName);
            response.setHeader("Content-Description",  "JSP Generated Data");
            response.setHeader("Accept",               "application/vnd.ms-excel");

            Cookie fileDownload = new Cookie("fileDownload","true");
            fileDownload.setPath("/");
            response.addCookie(fileDownload);

            inputStream  = new BufferedInputStream(excelIS);
            outputStream = response.getOutputStream();

            if(infos.isEmpty()) {
                workbook = new XSSFWorkbook(inputStream);
            } else {
                XLSTransformer xlst = new XLSTransformer();

                // 엑셀 특정 열을 숨김 처리 할 경우
                if(arrHiddenField.length == 1) {
                    xlst.setColumnPropertyNamesToHide(arrHiddenField[0]);
                }

                workbook = xlst.transformXLS(inputStream, infos);
            }

            workbook.write(outputStream);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            outputStream.close();
            workbook.close();
        }
    }
}
