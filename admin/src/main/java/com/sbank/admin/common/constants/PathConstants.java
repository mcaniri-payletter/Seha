package com.sbank.admin.common.constants;

/**--------------------------------------------------------------------
 * ■Path 관련 상수 설정 ■payletter ■2020-02-04
 --------------------------------------------------------------------**/
public class PathConstants {
    private PathConstants() {}

    // URI PATH
    public static final String PATH_VIEW_ERROR                       = "error";
    public static final String PATH_CONTROLLER_ERROR                 = "/" + PATH_VIEW_ERROR;
    public static final String PATH_VIEW_COMMON                      = "common/common";
    public static final String PATH_CONTROLLER_COMMON                = "/" + PATH_VIEW_COMMON;

    public static final String PATH_VIEW_LOGIN                       = "login/login";
    public static final String PATH_CONTROLLER_LOGIN                 = "/" + PATH_VIEW_LOGIN;

    // DASH BOARD
    public static final String PATH_VIEW_DASHBOARD                   = "dashboard/dashboard";
    public static final String PATH_CONTROLLER_DASHBOARD             = "/" + PATH_VIEW_DASHBOARD;

    // MENU
    public static final String PATH_VIEW_MENU                        = "menuAuthority/menu";
    public static final String PATH_CONTROLLER_MENU                  = "/" + PATH_VIEW_MENU;
    public static final String PATH_VIEW_MENU_GROUP                  = "menuAuthority/menuGroup";
    public static final String PATH_CONTROLLER_MENU_GROUP            = "/" + PATH_VIEW_MENU_GROUP;
    public static final String PATH_VIEW_MENU_ROLE                   = "menuAuthority/menuRole";
    public static final String PATH_CONTROLLER_MENU_ROLE             = "/" + PATH_VIEW_MENU_ROLE;

    // ADMIN INFO
    public static final String PATH_VIEW_ADMINISTRATOR               = "administrator/administrator";
    public static final String PATH_CONTROLLER_ADMINISTRATOR         = "/" + PATH_VIEW_ADMINISTRATOR;
    public static final String PATH_VIEW_ADMIN_MENU_ACCESS_LOG       = "administrator/adminMenuAccessLog";
    public static final String PATH_CONTROLLER_ADMIN_MENU_ACCESS_LOG = "/" + PATH_VIEW_ADMIN_MENU_ACCESS_LOG;
    public static final String PATH_VIEW_ADMIN_LOGIN_FAIL_HIST       = "administrator/adminLoginFailHist";
    public static final String PATH_CONTROLLER_ADMIN_LOGIN_FAIL_HIST = "/" + PATH_VIEW_ADMIN_LOGIN_FAIL_HIST;

    // SAMPLE
    public static final String PATH_VIEW_SAMPLE                      = "sample/sample";
    public static final String PATH_VIEW_SAMPLE_DETAIL               = "sample/sampleDetail";
    public static final String PATH_VIEW_SAMPLE_EDIT                 = "sample/sampleEdit";
    public static final String PATH_CONTROLLER_SAMPLE                = "/" + PATH_VIEW_SAMPLE;

    // CUSTOMER
    public static final String PATH_VIEW_CUSTOMER                    = "customer/customer";
    public static final String PATH_VIEW_CUSTOMER_EDIT               = "customer/customerEdit";
    public static final String PATH_CONTROLLER_CUSTOMER              = "/" + PATH_VIEW_CUSTOMER;
}