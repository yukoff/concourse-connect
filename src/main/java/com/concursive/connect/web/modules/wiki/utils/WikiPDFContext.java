package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.dao.Wiki;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Encapsulates objects used by the PDF Export
 *
 * @author matt rajkowski
 * @created Apr 13, 2010 4:40:39 PM
 */
public class WikiPDFContext {

  // properties used for parsing a wiki
  private WikiExportBean exportBean;
  private Project project;
  private Wiki wiki;
  private File file;
  private HashMap<String, ImageInfo> imageList;
  private String fileLibrary;
  private LinkedHashMap<String, String> headerAnchors = new LinkedHashMap<String, String>();
  private int userId;
  private int sectionIdCount = 0;
  private boolean canAppend = true;
  private int currentHeaderLevel = 1;
  private String serverUrl;

  public WikiPDFContext(Project project, Wiki wiki, File file, HashMap<String, ImageInfo> imageList, String fileLibrary, WikiExportBean exportBean) {
    this.exportBean = exportBean;
    this.project = project;
    this.wiki = wiki;
    this.file = file;
    this.imageList = imageList;
    this.fileLibrary = fileLibrary;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public Wiki getWiki() {
    return wiki;
  }

  public void setWiki(Wiki wiki) {
    this.wiki = wiki;
  }

  public HashMap<String, ImageInfo> getImageList() {
    return imageList;
  }

  public void setImageList(HashMap<String, ImageInfo> imageList) {
    this.imageList = imageList;
  }

  public LinkedHashMap<String, String> getHeaderAnchors() {
    return headerAnchors;
  }

  public void setHeaderAnchors(LinkedHashMap<String, String> headerAnchors) {
    this.headerAnchors = headerAnchors;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getSectionIdCount() {
    return sectionIdCount;
  }

  public void setSectionIdCount(int sectionIdCount) {
    this.sectionIdCount = sectionIdCount;
  }

  public boolean isCanAppend() {
    return canAppend;
  }

  public void setCanAppend(boolean canAppend) {
    this.canAppend = canAppend;
  }

  public int getCurrentHeaderLevel() {
    return currentHeaderLevel;
  }

  public void setCurrentHeaderLevel(int currentHeaderLevel) {
    this.currentHeaderLevel = currentHeaderLevel;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public WikiExportBean getExportBean() {
    return exportBean;
  }

  public void setExportBean(WikiExportBean exportBean) {
    this.exportBean = exportBean;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public String getFileLibrary() {
    return fileLibrary;
  }

  public void setFileLibrary(String fileLibrary) {
    this.fileLibrary = fileLibrary;
  }

  public int foundHeader(int headerLevel) {
    sectionIdCount += 1;
    currentHeaderLevel = headerLevel;
    return sectionIdCount;
  }
}
