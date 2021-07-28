package com.sbank.admin.common.database;

import com.sbank.admin.common.constants.CodeEnum;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class CodeEnumTypeHandler <E extends Enum <E>> implements TypeHandler<CodeEnum> {

    private Class <E> type;

    public CodeEnumTypeHandler(Class <E> type) {
        this.type = type;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, CodeEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public CodeEnum getResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return getCodeEnum(code);
    }

    @Override
    public CodeEnum getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return getCodeEnum(code);
    }

    private CodeEnum getCodeEnum(int code) {
        try {
            CodeEnum[] enumConstants = (CodeEnum[]) type.getEnumConstants();
            for (CodeEnum codeNum: enumConstants) {
                if (codeNum.getCode() == code) {
                    return codeNum;
                }
            }
            return null;
        } catch (Exception e) {
            throw new TypeException("can't make enum '" + type + "'", e);
        }
    }
}

