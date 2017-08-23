package com.mml.dingding.common;

public enum ErrorCode {

    ERROR_CODE_1(60200, "该用户是同企业同事, 无法加入外部通讯录"),
    ERROR_CODE_2(60103, "手机号码不合法"),
    ERROR_CODE_3(60201, "该外部联系人已存在"),
    ERROR_CODE_4(60203, "外部联系人数量达到上限"),
    ERROR_CODE_5(60204, "必须至少设置一个标签");

    int code;
    String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
