package net.juniper.titan.model;

import java.sql.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author rtejasvi
 */
public class ProblemReport {
    
    private int pr_id;
    private String description;
    private String confidential;
    private String synopsis;
    private String platform; 
    private String product;
    private String prClass;
    private String category; 
    private String functionalArea; 
    private Date lastModified;
    private String problemLevel;
    private String keywords;
    private String fix;
    private String workaround; 
    private String submitterId;
    private String jtacCaseId;
    private String externalId;
    private String legacyCustomer;
    private String environment;
    private String releaseNote;
    private String externalDescription;
    private String externalTitle;
    private String supportNotes;
    private String resolvedIn;
    private String devOwnerBgBu;
    private String submitterBgBu;
    private String state;
    private Date arrivalDate;

    /**
     * @return the pr_id
     */
    public int getPr_id() {
        return pr_id;
    }

    /**
     * @param pr_id the pr_id to set
     */
    public void setPr_id(int pr_id) {
        this.pr_id = pr_id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the confidential
     */
    public String getConfidential() {
        return confidential;
    }

    /**
     * @param confidential the confidential to set
     */
    public void setConfidential(String confidential) {
        this.confidential = confidential;
    }

    /**
     * @return the synopsis
     */
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * @param synopsis the synopsis to set
     */
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the prClass
     */
    public String getPrClass() {
        return prClass;
    }

    /**
     * @param prClass the prClass to set
     */
    public void setPrClass(String prClass) {
        this.prClass = prClass;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the functionalArea
     */
    public String getFunctionalArea() {
        return functionalArea;
    }

    /**
     * @param functionalArea the functionalArea to set
     */
    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the problemLevel
     */
    public String getProblemLevel() {
        return problemLevel;
    }

    /**
     * @param problemLevel the problemLevel to set
     */
    public void setProblemLevel(String problemLevel) {
        this.problemLevel = problemLevel;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @return the fix
     */
    public String getFix() {
        return fix;
    }

    /**
     * @param fix the fix to set
     */
    public void setFix(String fix) {
        this.fix = fix;
    }

    /**
     * @return the workaround
     */
    public String getWorkaround() {
        return workaround;
    }

    /**
     * @param workaround the workaround to set
     */
    public void setWorkaround(String workaround) {
        this.workaround = workaround;
    }

    /**
     * @return the submitterId
     */
    public String getSubmitterId() {
        return submitterId;
    }

    /**
     * @param submitterId the submitterId to set
     */
    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    /**
     * @return the jtacCaseId
     */
    public String getJtacCaseId() {
        return jtacCaseId;
    }

    /**
     * @param jtacCaseId the jtacCaseId to set
     */
    public void setJtacCaseId(String jtacCaseId) {
        this.jtacCaseId = jtacCaseId;
    }

    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @return the legacyCustomer
     */
    public String getLegacyCustomer() {
        return legacyCustomer;
    }

    /**
     * @param legacyCustomer the legacyCustomer to set
     */
    public void setLegacyCustomer(String legacyCustomer) {
        this.legacyCustomer = legacyCustomer;
    }

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @return the releaseNote
     */
    public String getReleaseNote() {
        return releaseNote;
    }

    /**
     * @param releaseNote the releaseNote to set
     */
    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    /**
     * @return the externalDescription
     */
    public String getExternalDescription() {
        return externalDescription;
    }

    /**
     * @param externalDescription the externalDescription to set
     */
    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    /**
     * @return the externalTitle
     */
    public String getExternalTitle() {
        return externalTitle;
    }

    /**
     * @param externalTitle the externalTitle to set
     */
    public void setExternalTitle(String externalTitle) {
        this.externalTitle = externalTitle;
    }

    /**
     * @return the supportNotes
     */
    public String getSupportNotes() {
        return supportNotes;
    }

    /**
     * @param supportNotes the supportNotes to set
     */
    public void setSupportNotes(String supportNotes) {
        this.supportNotes = supportNotes;
    }

    /**
     * @return the resolvedIn
     */
    public String getResolvedIn() {
        return resolvedIn;
    }

    /**
     * @param resolvedIn the resolvedIn to set
     */
    public void setResolvedIn(String resolvedIn) {
        this.resolvedIn = resolvedIn;
    }

    /**
     * @return the devOwnerBgBu
     */
    public String getDevOwnerBgBu() {
        return devOwnerBgBu;
    }

    /**
     * @param devOwnerBgBu the devOwnerBgBu to set
     */
    public void setDevOwnerBgBu(String devOwnerBgBu) {
        this.devOwnerBgBu = devOwnerBgBu;
    }

    /**
     * @return the submitterBgBu
     */
    public String getSubmitterBgBu() {
        return submitterBgBu;
    }

    /**
     * @param submitterBgBu the submitterBgBu to set
     */
    public void setSubmitterBgBu(String submitterBgBu) {
        this.submitterBgBu = submitterBgBu;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the arrivalDate
     */
    public Date getArrivalDate() {
        return arrivalDate;
    }

    /**
     * @param arrivalDate the arrivalDate to set
     */
    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

}
