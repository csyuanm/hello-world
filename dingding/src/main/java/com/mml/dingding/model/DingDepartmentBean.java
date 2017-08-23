package com.mml.dingding.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class DingDepartmentBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
    private String id;

    private String name;

    private String parentid;

    private String order;

    private Boolean createdeptgroup;

    private Boolean autoadduser;

    private Boolean depthiding;

    private String deptpermits;

    private String userpermits;

    private Boolean outerdept;

    private String outerpermitdepts;

    private String outerpermitusers;

    private String orgdeptowner;

    private String deptmanageruseridlist;

   
}