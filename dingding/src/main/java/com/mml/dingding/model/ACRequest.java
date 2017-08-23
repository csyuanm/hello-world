package com.mml.dingding.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ACRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 以下只定义必须字段，需要添加字段请参照文档
     */

    Number[] labelIds;
    String name;
    String followerUserId;
    String stateCode;
    String mobile;
}
