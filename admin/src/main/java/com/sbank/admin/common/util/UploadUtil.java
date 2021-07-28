package com.sbank.admin.common.util;

import com.sbank.admin.common.constants.GlobalConstants;
import com.sbank.admin.common.domain.AttachFile;
import com.sbank.admin.common.exception.GlobalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

public class UploadUtil {
    private static final Logger logger = LoggerFactory.getLogger(UploadUtil.class);

    private UploadUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**--------------------------------------------------------------------
     * ■파일 업로드 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static AttachFile UploadFile(MultipartFile multipartFile, String strPath) {
        String strFileName = "";
        String strOrgFileName = "";
        AttachFile attachFile = new AttachFile();

        try {
            // 업로드 디렉토리 확인
            File dir = new File(strPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 파일 사이즈 체크

            // 서버에 저장될 파일명 설정
            strOrgFileName = multipartFile.getOriginalFilename();
            strFileName = getConvertedFileName(strOrgFileName);

            // 실제 파일 저장
            File file  = new File(strPath + strFileName);
            byte[] bytFileSize = multipartFile.getBytes();

            try (OutputStream stream = new FileOutputStream(file)) {
                stream.write(bytFileSize);
            }

            // 결과 저장
            attachFile.setIntRetCode(GlobalConstants.COMMON_SUCCEED_CODE);
            attachFile.setStrRetMsg("File upload is complete.");
            attachFile.setFilePath(strPath);
            attachFile.setFileNm(strFileName);
            attachFile.setFileUploadNm(strOrgFileName);
            attachFile.setFileSize(multipartFile.getSize());

        } catch(Exception ex) {
            logger.error("{}", ex);
            attachFile.setIntRetCode(GlobalConstants.COMMON_FAILED_CODE);
            attachFile.setStrRetMsg("There is a problem with the system. Please contact the admin.");
        }

        return attachFile;
    }

    /**--------------------------------------------------------------------
     * ■다운로드 파일 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public static void downloadFile(HttpServletResponse response, String strPath, String strFileUploadName) {
        try {
            File readFile = new File(strPath);

            if (!readFile.exists()) {
                throw new GlobalException("common.msg.internalErr");
            }

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(strFileUploadName,"UTF-8")+"\";");
            response.setHeader("Content-Transfer-Encoding", "binary");

            try(FileInputStream fis = new FileInputStream(readFile)) {
                FileCopyUtils.copy(fis, response.getOutputStream());

                response.getOutputStream().flush();
            }

        } catch (GlobalException ex) {
            logger.error("{}", ex);
            throw new GlobalException(ex);
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
    }

    /**--------------------------------------------------------------------
     * ■실제 저장될 파일 이름 설정 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    private static String getConvertedFileName(String strOrgFileName) {
        int intLastIndexPos = strOrgFileName.lastIndexOf('.');
        String strConvertedFileName = "";

        strConvertedFileName = CommonUtil.convertDate(CommonUtil.DateFormat.YYYYMMDDHH24MISS_NOSPACE) + "_" + CommonUtil.getUuid().substring(0, 5);
        strConvertedFileName += strOrgFileName.substring(intLastIndexPos);

        return strConvertedFileName;
    }

    /**--------------------------------------------------------------------
    * ■임시 파일 삭제 ■payletter ■2019-11-06
    --------------------------------------------------------------------**/
    public static void DeleteFile(String path) {
        File file = new File(path);

        if(file.exists()){
            if(file.delete()) {
                logger.info("임시 파일 삭제 성공 (" + path + ")");
            } else {
                logger.info("임시 파일 삭제 실패 (" + path + ")");
            }
        } else {
            logger.info("해당 임시 파일이 존재하지 않습니다. (" + path + ")");
        }
    }
}
