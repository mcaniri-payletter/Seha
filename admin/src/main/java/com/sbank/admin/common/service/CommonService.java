package com.sbank.admin.common.service;

import com.sbank.admin.common.domain.AccessMenu;
import com.sbank.admin.common.domain.AdminMenu;
import com.sbank.admin.common.domain.RestTemplateModel;
import com.sbank.admin.common.domain.TypeCode;
import com.sbank.admin.common.exception.GlobalException;
import com.sbank.admin.common.repository.GenericDAO;
import com.sbank.admin.common.util.CommonUtil;
import com.sbank.admin.login.domain.Login;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;

/**--------------------------------------------------------------------
 * ■공통 인터페이스 구현부 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
@SuppressWarnings("unchecked")
@Service
public class CommonService {
    @Autowired
    @Qualifier("mainDB")
    private GenericDAO<Object, Object> dao;

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);

    /**--------------------------------------------------------------------
     * ■관리자 로그인 시 초기 페이지 셋팅 (접근 권한을 가지고 있는 메뉴 1개) ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public AdminMenu selectMenuLinkByLoginAction(Login login) {
        return (AdminMenu) dao.selectOne("common.selectMenuLinkByLoginAction", login);
    }

    public LinkedHashMap<Integer, AdminMenu> selectMenuList(Login login) {
        List<AdminMenu> menuList = (List) dao.selectList("common.selectMenuList", login);
        LinkedHashMap<Integer, AdminMenu> menuTree = convertMenuTree(menuList);
        return menuTree;
    }

    public AccessMenu selectAccessMenu(AccessMenu accessMenu) {
        return (AccessMenu) dao.selectOne("common.selectAccessMenu", accessMenu);
    }

    private LinkedHashMap<Integer, AdminMenu> convertMenuTree(List<AdminMenu> menuList) {
        LinkedHashMap<Integer, AdminMenu> menuTree = new LinkedHashMap<>();

        for(AdminMenu adminMenu : menuList) {
            int menuGroupNo = adminMenu.getMenuGroupNo();
            int menuNo = adminMenu.getMenuNo();

            AdminMenu parentMenu = menuTree.get(menuGroupNo);

            if(parentMenu == null) {
                menuTree.put(menuGroupNo, adminMenu);
            } else {
                parentMenu.getSubMenu().put(menuNo, adminMenu);
            }
        }
        return menuTree;
    }

    /**--------------------------------------------------------------------
     * ■DDLB 조회 서비스(조회 조건이 없는 경우) ■payletter ■2019-06-28
     --------------------------------------------------------------------**/
    public List<TypeCode> selectCodeList(String queryName) {
        List<TypeCode> lstTypeCode = (List) dao.selectList(queryName);

        for(TypeCode objTypeCode : lstTypeCode) {
            String messageCode = objTypeCode.getMessageCode();
            if(!CommonUtil.isNullOrEmpty(messageCode)) {
                String message = getMessage(messageCode);
                objTypeCode.setCodeName(message);
            }
        }
        return lstTypeCode;
    }

    /**--------------------------------------------------------------------
    * ■DDLB 조회 서비스(조회 조건이 있는 경우) ■payletter ■2019-01-11
    --------------------------------------------------------------------**/
    public List<TypeCode> selectCodeList(String queryName, TypeCode typeCode) {
        List<TypeCode> lstTypeCode = (List) dao.selectList(queryName, typeCode);

        for(TypeCode objTypeCode : lstTypeCode) {
            String messageCode = objTypeCode.getMessageCode();
            if(!CommonUtil.isNullOrEmpty(messageCode)) {
                objTypeCode.setCodeName(getMessage(messageCode));
            }
        }
        return lstTypeCode;
    }

    /**--------------------------------------------------------------------
     * ■다국어 메시지 조회 ■payletter ■2019-07-30
     --------------------------------------------------------------------**/
    public String getMessage(String messageCode) {
        String message = String.format("?%s?", messageCode);
        try {
            message = messageSourceAccessor.getMessage(messageCode);
        } catch (NoSuchMessageException ignore) {}
        return message;
    }

    /**--------------------------------------------------------------------
     * ■API 호출 서비스 ■payletter ■2019-11-07
     * ------------------------------------------------------------------**/
    public <T> ResponseEntity<T> callAPIService(RestTemplateModel restTemplateModel, HttpMethod httpMethodType) throws GlobalException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> result;
        try {
            result = restTemplate.exchange(new URI(restTemplateModel.strApiUrl), httpMethodType, restTemplateModel.requestData, new ParameterizedTypeReference<T>(){});
        } catch(URISyntaxException e) {
            throw new GlobalException(e.getMessage());
        } catch(HttpClientErrorException e) {
            logger.warn(e.getResponseBodyAsString());
            throw new GlobalException(e.getMessage());
        }
        return result;
    }
}