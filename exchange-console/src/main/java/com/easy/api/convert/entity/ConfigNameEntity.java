package com.easy.api.convert.entity;

import java.io.Serializable;

public class ConfigNameEntity implements Serializable{
	
	private int p_indx;
	private int seq;
	private int SourceFileFloor;
	private String SourceNoteName;
	private int TargetFileFloor;
	private String TargetColName;
	private String IsSubList;
	private String IsSubListName;
	private String CN_REMAKE;
	public int getP_indx() {
		return p_indx;
	}
	public void setP_indx(int p_indx) {
		this.p_indx = p_indx;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getSourceFileFloor() {
		return SourceFileFloor;
	}
	public void setSourceFileFloor(int sourceFileFloor) {
		SourceFileFloor = sourceFileFloor;
	}
	public String getSourceNoteName() {
		return SourceNoteName;
	}
	public void setSourceNoteName(String sourceNoteName) {
		SourceNoteName = sourceNoteName;
	}
	public int getTargetFileFloor() {
		return TargetFileFloor;
	}
	public void setTargetFileFloor(int targetFileFloor) {
		TargetFileFloor = targetFileFloor;
	}
	public String getTargetColName() {
		return TargetColName;
	}
	public void setTargetColName(String targetColName) {
		TargetColName = targetColName;
	}
	public String getIsSubList() {
		return IsSubList;
	}
	public void setIsSubList(String isSubList) {
		IsSubList = isSubList;
	}
	
	public String getCN_REMAKE() {
		return CN_REMAKE;
	}
	public void setCN_REMAKE(String cN_REMAKE) {
		CN_REMAKE = cN_REMAKE;
	}
	
	public String getIsSubListName() {
		return IsSubListName;
	}
	public void setIsSubListName(String isSubListName) {
		IsSubListName = isSubListName;
	}
	
	@Override
	public String toString() {
		return "{ISSUBLISTNAME:"+IsSubListName+",CN_REMAKE:" + CN_REMAKE + ",P_INDX:" + p_indx + ",ISSUBLIST:"+IsSubList+", SEQ:" + seq + ", SOURCEFILEFLOOR:" + SourceFileFloor
				+ ", SOURCENOTENAME:" + SourceNoteName + ", TARGETFILEFLOOR:" + TargetFileFloor + ", TARGETCOLNAME:"
				+ TargetColName + "}";
	}
	
	
	
	
	

}
