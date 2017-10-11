package com.zing.dingding.model;

import java.io.Serializable;
import java.util.Date;

public class NsGoodsDetailBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer id;

    private Integer internalId;

    private String poNumber;

    private String goodsName;

    private String goodsCode;

    private Double rate;

    private Integer quantity;

    private Double total;

    private Boolean flag;

    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getInternalId() {
		return internalId;
	}

	public void setInternalId(Integer internalId) {
		this.internalId = internalId;
	}

	public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber == null ? null : poNumber.trim();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode == null ? null : goodsCode.trim();
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}