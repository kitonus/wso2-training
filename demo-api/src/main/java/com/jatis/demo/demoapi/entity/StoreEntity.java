package com.jatis.demo.demoapi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.security.core.context.SecurityContextHolder;

@Entity
public class StoreEntity {

	@Id
	private String code;
	private String name;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date establishedDate;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;
	
	private String lastUpdateBy;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEstablishedDate() {
		return establishedDate;
	}

	public void setEstablishedDate(Date establishedDate) {
		this.establishedDate = establishedDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(String lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	
	@PrePersist
	@PreUpdate
	protected void onInsert() {
		lastUpdate = new Date();
		this.lastUpdateBy = SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
