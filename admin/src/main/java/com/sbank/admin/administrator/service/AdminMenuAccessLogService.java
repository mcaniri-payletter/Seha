package com.sbank.admin.administrator.service;

import com.sbank.admin.administrator.domain.AdminMenuAccessLog;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**--------------------------------------------------------------------
 * ■관리자 로그 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class AdminMenuAccessLogService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    /**--------------------------------------------------------------------
     * ■관리자 로그 목록 조회 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<AdminMenuAccessLog> selectAdminMenuAccessLogList(AdminMenuAccessLog adminMenuAccessLog) throws GlobalException {
        adminMenuAccessLog.changeLastYMD();

        // 엑셀 다운로드 여부
        if(adminMenuAccessLog.getExcelFlag()) {
            CommonUtil.setExcelDownload(adminMenuAccessLog);
        } else {
            int intTotalCnt = (int) dao.selectOne("adminMenuAccessLog.selectAdminMenuAccessLogCnt", adminMenuAccessLog);

            adminMenuAccessLog.setRecordsFiltered(intTotalCnt);
            adminMenuAccessLog.setRecordsTotal(intTotalCnt);
        }

        return (List) dao.selectList("adminMenuAccessLog.selectAdminMenuAccessLogList", adminMenuAccessLog);
    }

    /**--------------------------------------------------------------------
     * ■관리자 로그 등록 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public int insertAdminMenuAccessLog(AdminMenuAccessLog adminMenuAccessLog) {
        return dao.insert("adminMenuAccessLog.insertAdminMenuAccessLog", adminMenuAccessLog);
    }
}
