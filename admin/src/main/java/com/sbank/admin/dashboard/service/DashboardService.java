package com.sbank.admin.dashboard.service;

import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.dashboard.domain.Dashboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**--------------------------------------------------------------------
 * ■대시보드 인터페이스 구현부 ■payletter ■2020-04-13
 --------------------------------------------------------------------**/
@Service
public class DashboardService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    /**--------------------------------------------------------------------
     * ■당일 결제 금액 조회 서비스 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    public Dashboard selectDashboard() {
        Dashboard dashboard = new Dashboard();
        DecimalFormat decimalFormat = new DecimalFormat("###,###.#########");

        dashboard.setTodayPaymentAmount(decimalFormat.format(selectTodayPaymentAmount()));
        dashboard.setTodayPurchaseAmount(decimalFormat.format(selectTodayPurchaseAmount()));
        dashboard.setTimezoneCashChargeList(selectTimezoneCashCharge());
        dashboard.setTimezonePurchaseItemList(selectTimezonePurchaseItem());

        return dashboard;
    }

    /**--------------------------------------------------------------------
     * ■당일 결제 금액 조회 서비스 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    public BigDecimal selectTodayPaymentAmount() {
        return (BigDecimal) dao.selectOne("dashboard.selectTodayPaymentAmount");
    }

    /**--------------------------------------------------------------------
     * ■당일 결제 금액 조회 서비스 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    public BigDecimal selectTodayPurchaseAmount() {
        return (BigDecimal) dao.selectOne("dashboard.selectTodayPurchaseAmount");
    }

    /**--------------------------------------------------------------------
     * ■시간대별 캐시 충전 조회 서비스 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Dashboard> selectTimezoneCashCharge() {
        return (List) dao.selectList("dashboard.selectTimezoneCashCharge");
    }

    /**--------------------------------------------------------------------
     * ■시간대별 아이템 구매 조회 서비스 ■payletter ■2020-04-13
     --------------------------------------------------------------------**/
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Dashboard> selectTimezonePurchaseItem() {
        return (List) dao.selectList("dashboard.selectTimezonePurchaseItem");
    }
}
