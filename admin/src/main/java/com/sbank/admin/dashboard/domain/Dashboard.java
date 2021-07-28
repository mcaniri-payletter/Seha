package com.sbank.admin.dashboard.domain;

import com.sbank.admin.common.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**--------------------------------------------------------------------
 * ■대시 보드 모델 ■payletter ■2020-04-13
 --------------------------------------------------------------------**/
@Data
@EqualsAndHashCode(callSuper=false)
public class Dashboard extends BaseModel {
    private String     todayPaymentAmount;      // 당일 결제 금액
    private String     todayPurchaseAmount;     // 당일 구매 금액
    private String     paymentTime;             // 시간
    private String     cashAttrCode;            // 캐시 속성
    private String     itemName;                // 아이템 이름

    private BigDecimal totalCashAmt;            // 총 충전 금액
    private BigDecimal totalPurchaseCnt;        // 총 구매 갯수

    private List<Dashboard> timezoneCashChargeList;
    private List<Dashboard> timezonePurchaseItemList;
}
