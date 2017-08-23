package com.mml.dingding.model;

import java.io.Serializable;

import lombok.Data;


@Data
public class DingUserBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String userid;

    private String name;

    private String tel;

    private String workpalce;

    private String remark;

    private String mobile;

    private String email;

    private String orgemail;

    private Boolean active;

    private String orderindepts;

    private Boolean isadmin;

    private Boolean isboss;

    private String dingid;

    private String unionid;

    private Boolean isleaderindepts;

    private Boolean ishide;

    private String department;

    private String position;

    private String avatar;

    private String jobnumber;

    private String extatt;

    private String roles;

    private String rolesId;

    private String rolesName;

    private String rolesGroupname;

    private Boolean issenior;
    
    private String openId;
    
    private String extattr;
    
    private int order;

}