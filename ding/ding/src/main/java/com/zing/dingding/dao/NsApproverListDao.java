package com.zing.dingding.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.zing.dingding.model.NsApproverListBean;

@Mapper
public interface NsApproverListDao {

    int insert(NsApproverListBean record);

    NsApproverListBean selectByName(String name);
    
    List<NsApproverListBean> selectListByName(String name);

}