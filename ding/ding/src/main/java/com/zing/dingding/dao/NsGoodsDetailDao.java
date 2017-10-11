package com.zing.dingding.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zing.dingding.model.NsGoodsDetailBean;

@Mapper
public interface NsGoodsDetailDao {

	int insert(NsGoodsDetailBean goodsDetail);
	
	int checkGoodsRepeat(@Param("internalId") Integer internalId, @Param("goodsName")String goodsName);
	
	List<NsGoodsDetailBean> getGoodsDetailList(Integer internalId);
}
