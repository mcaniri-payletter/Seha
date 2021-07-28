package com.sbank.admin.administrator.service;

import com.sbank.admin.administrator.domain.AdminLoginFailHist;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.service.CommonService;
import com.sbank.admin.common.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.List;

/**--------------------------------------------------------------------
 * ■로그인 실패 이력 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@Service
public class AdminLoginFailHistService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    private CommonService commonService;

    /**--------------------------------------------------------------------
     * ■로그인 실패 이력 목록 조회 서비스 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<AdminLoginFailHist> selectAdminLoginFailHistList(AdminLoginFailHist adminLoginFailHist) throws GlobalException {
        adminLoginFailHist.changeLastYMD();

        if(adminLoginFailHist.getExcelFlag()) {
            CommonUtil.setExcelDownload(adminLoginFailHist);
        } else {
            int intTotalCnt = (int)dao.selectOne("adminLoginFailHist.selectAdminLoginFailHistCnt", adminLoginFailHist);

            adminLoginFailHist.setRecordsFiltered(intTotalCnt);
            adminLoginFailHist.setRecordsTotal(intTotalCnt);
        }

        List<AdminLoginFailHist> adminLoginFailHistList = (List) dao.selectList("adminLoginFailHist.selectAdminLoginFailHistList", adminLoginFailHist);

        for (AdminLoginFailHist obj : adminLoginFailHistList) {
            try {
                obj.setErrMsgTrans(commonService.getMessage(obj.getErrMsg()));
            } catch (NoSuchMessageException ignore) {}
        }

        return adminLoginFailHistList;
    }
}
