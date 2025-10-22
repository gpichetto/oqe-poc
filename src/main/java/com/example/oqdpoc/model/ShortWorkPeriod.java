package com.example.oqdpoc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ShortWorkPeriod {
    @JsonProperty("_id")
    private String id;
    
    @JsonProperty("_translangcode")
    private String transLangCode;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime actfinish;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime actstart;
    
    private String assetnum;
    private String description;
    private String description_longdescription;
    private String href;
    private String jpnum;
    private String owner;
    private String parent;
    private String pcphys;
    private String plusareference;
    private String plusareferenceid;
    private String plusmworkperf;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime schedfinish;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime schedstart;
    
    private String siteid;
    private String status;
    private String status_description;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime targcompdate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime targstartdate;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime thacalldate;
    
    private String thacfto;
    private String thadesc;
    private String thamtcpln;
    private String than1n9;
    private String thand;
    private Boolean thansc;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime thantfdate;
    
    private String thantffrom;
    private String thantfto;
    private String thaopdef;
    private String vendor;
    private String wonum;
    private String worktype;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransLangCode() {
        return transLangCode;
    }

    public void setTransLangCode(String transLangCode) {
        this.transLangCode = transLangCode;
    }

    public LocalDateTime getActfinish() {
        return actfinish;
    }

    public void setActfinish(LocalDateTime actfinish) {
        this.actfinish = actfinish;
    }

    public LocalDateTime getActstart() {
        return actstart;
    }

    public void setActstart(LocalDateTime actstart) {
        this.actstart = actstart;
    }

    public String getAssetnum() {
        return assetnum;
    }

    public void setAssetnum(String assetnum) {
        this.assetnum = assetnum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription_longdescription() {
        return description_longdescription;
    }

    public void setDescription_longdescription(String description_longdescription) {
        this.description_longdescription = description_longdescription;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getJpnum() {
        return jpnum;
    }

    public void setJpnum(String jpnum) {
        this.jpnum = jpnum;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getPcphys() {
        return pcphys;
    }

    public void setPcphys(String pcphys) {
        this.pcphys = pcphys;
    }

    public String getPlusareference() {
        return plusareference;
    }

    public void setPlusareference(String plusareference) {
        this.plusareference = plusareference;
    }

    public String getPlusareferenceid() {
        return plusareferenceid;
    }

    public void setPlusareferenceid(String plusareferenceid) {
        this.plusareferenceid = plusareferenceid;
    }

    public String getPlusmworkperf() {
        return plusmworkperf;
    }

    public void setPlusmworkperf(String plusmworkperf) {
        this.plusmworkperf = plusmworkperf;
    }

    public LocalDateTime getSchedfinish() {
        return schedfinish;
    }

    public void setSchedfinish(LocalDateTime schedfinish) {
        this.schedfinish = schedfinish;
    }

    public LocalDateTime getSchedstart() {
        return schedstart;
    }

    public void setSchedstart(LocalDateTime schedstart) {
        this.schedstart = schedstart;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_description() {
        return status_description;
    }

    public void setStatus_description(String status_description) {
        this.status_description = status_description;
    }

    public LocalDateTime getTargcompdate() {
        return targcompdate;
    }

    public void setTargcompdate(LocalDateTime targcompdate) {
        this.targcompdate = targcompdate;
    }

    public LocalDateTime getTargstartdate() {
        return targstartdate;
    }

    public void setTargstartdate(LocalDateTime targstartdate) {
        this.targstartdate = targstartdate;
    }

    public LocalDateTime getThacalldate() {
        return thacalldate;
    }

    public void setThacalldate(LocalDateTime thacalldate) {
        this.thacalldate = thacalldate;
    }

    public String getThacfto() {
        return thacfto;
    }

    public void setThacfto(String thacfto) {
        this.thacfto = thacfto;
    }

    public String getThadesc() {
        return thadesc;
    }

    public void setThadesc(String thadesc) {
        this.thadesc = thadesc;
    }

    public String getThamtcpln() {
        return thamtcpln;
    }

    public void setThamtcpln(String thamtcpln) {
        this.thamtcpln = thamtcpln;
    }

    public String getThan1n9() {
        return than1n9;
    }

    public void setThan1n9(String than1n9) {
        this.than1n9 = than1n9;
    }

    public String getThand() {
        return thand;
    }

    public void setThand(String thand) {
        this.thand = thand;
    }

    public Boolean getThansc() {
        return thansc;
    }

    public void setThansc(Boolean thansc) {
        this.thansc = thansc;
    }

    public LocalDateTime getThantfdate() {
        return thantfdate;
    }

    public void setThantfdate(LocalDateTime thantfdate) {
        this.thantfdate = thantfdate;
    }

    public String getThantffrom() {
        return thantffrom;
    }

    public void setThantffrom(String thantffrom) {
        this.thantffrom = thantffrom;
    }

    public String getThantfto() {
        return thantfto;
    }

    public void setThantfto(String thantfto) {
        this.thantfto = thantfto;
    }

    public String getThaopdef() {
        return thaopdef;
    }

    public void setThaopdef(String thaopdef) {
        this.thaopdef = thaopdef;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWonum() {
        return wonum;
    }

    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    public String getWorktype() {
        return worktype;
    }

    public void setWorktype(String worktype) {
        this.worktype = worktype;
    }
}
