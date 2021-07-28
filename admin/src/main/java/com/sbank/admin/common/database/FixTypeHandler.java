package com.sbank.admin.common.database;

import com.sbank.admin.common.util.CommonUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//----------------------------------------------------------------------
// ■DB컬럼 자동 암/복호화 적용 (고정) ■payletter ■2019-02-14
//----------------------------------------------------------------------
public class FixTypeHandler implements TypeHandler<String> {
    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        String strRslt = "";

        String strColumnValue = rs.getString(columnName);
        if(strColumnValue != null && !"".equals(strColumnValue)) {
            strRslt = CommonUtil.getDecrypt(rs.getString(columnName));
        }

        return strRslt;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        String strRslt = "";

        String strColumnValue = rs.getString(columnIndex);
        if(strColumnValue != null && !"".equals(strColumnValue)) {
            strRslt = CommonUtil.getDecrypt(rs.getString(columnIndex));
        }

        return strRslt;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String strRslt = "";

        String strColumnValue = cs.getString(columnIndex);
        if(strColumnValue != null && !"".equals(strColumnValue)) {
            strRslt = CommonUtil.getDecrypt(cs.getString(columnIndex));
        }

        return  strRslt;
    }

    @Override
    public void setParameter(PreparedStatement ps, int arg1, String str, JdbcType jdbcType) throws SQLException {
        ps.setString(arg1, CommonUtil.getEncrypt(str, false));
    }
}
