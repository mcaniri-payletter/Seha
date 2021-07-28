package com.sbank.admin.common.constants;

import com.sbank.admin.common.database.CodeEnumTypeHandler;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.ObjectUtils;

/**
 * 공통 Enums ■payletter
 */
public class Enums {
    /**
     * 작업 유형 코드 ■payletter
     */
    public enum WorkTypeCode implements CodeEnum {
        SELECT((byte)1, "조회"), INSERT((byte)2, "입력"), UPDATE((byte)3, "수정"), DELETE((byte)4, "삭제"), EXCEL_DOWNLOAD((byte)5, "엑셀다운로드"), ETC((byte)99, "기타");
        private byte value;
        private String codeName;

        WorkTypeCode(byte value, String codeName) {
            this.value = value;
            this.codeName = codeName;
        }

        public byte getValue() {
            return value;
        }
        public String getCodeName() {
            return codeName;
        }

        public static WorkTypeCode getByCode(Byte code) {
            if (ObjectUtils.isEmpty(code)) {
                return null;
            }
            for (WorkTypeCode typeCode : values()) {
                if (typeCode.value == code) {
                    return typeCode;
                }
            }
            return null;
        }

        @MappedTypes(WorkTypeCode.class)
        public static class TypeHandler extends CodeEnumTypeHandler<WorkTypeCode> {
            public TypeHandler() {
                super(WorkTypeCode.class);
            }
        }

        @Override
        public int getCode() {
            return value;
        }
    }
}
