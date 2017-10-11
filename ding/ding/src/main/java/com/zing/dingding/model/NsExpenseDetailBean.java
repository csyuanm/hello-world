package com.zing.dingding.model;

import java.io.Serializable;
import java.util.Date;

public class NsExpenseDetailBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer id;

    private Integer internalId;

    private String category;

    private Double amount;

    private String remark;

    private String imgUrl;

    private Boolean flag;

    private String startTime;

    private String endTime;

    private Date lastUdpateTime;

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

	public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }

    public Date getLastUdpateTime() {
        return lastUdpateTime;
    }

    public void setLastUdpateTime(Date lastUdpateTime) {
        this.lastUdpateTime = lastUdpateTime;
    }
}