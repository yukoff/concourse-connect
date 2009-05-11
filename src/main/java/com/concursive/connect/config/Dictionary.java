/*
 * ConcourseConnect
 * Copyright 2009 Concursive Corporation
 * http://www.concursive.com
 *
 * This file is part of ConcourseConnect, an open source social business
 * software and community platform.
 *
 * Concursive ConcourseConnect is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, version 3 of the License.
 *
 * Under the terms of the GNU Affero General Public License you must release the
 * complete source code for any application that uses any part of ConcourseConnect
 * (system header files and libraries used by the operating system are excluded).
 * These terms must be included in any work that has ConcourseConnect components.
 * If you are developing and distributing open source applications under the
 * GNU Affero General Public License, then you are free to use ConcourseConnect
 * under the GNU Affero General Public License.
 *
 * If you are deploying a web site in which users interact with any portion of
 * ConcourseConnect over a network, the complete source code changes must be made
 * available.  For example, include a link to the source archive directly from
 * your web site.
 *
 * For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
 * products, and do not license and distribute their source code under the GNU
 * Affero General Public License, Concursive provides a flexible commercial
 * license.
 *
 * To anyone in doubt, we recommend the commercial license. Our commercial license
 * is competitively priced and will eliminate any confusion about how
 * ConcourseConnect can be used and distributed.
 *
 * ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Attribution Notice: ConcourseConnect is an Original Work of software created
 * by Concursive Corporation
 */
package com.concursive.connect.config;

import com.concursive.commons.xml.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maintains resource bundle content for a specific language
 *
 * @author Ananth
 * @created February 3, 2005
 */
public class Dictionary {

  private static Log LOG = LogFactory.getLog(Dictionary.class);

  private String language = null;
  private String defaultLanguage = "en_US";  //default language
  private Map localizationPrefs = new LinkedHashMap();


  /**
   * Gets the defaultLanguage attribute of the Dictionary object
   *
   * @return The defaultLanguage value
   */
  public String getDefaultLanguage() {
    return defaultLanguage;
  }


  /**
   * Sets the defaultLanguage attribute of the Dictionary object
   *
   * @param tmp The new defaultLanguage value
   */
  public void setDefaultLanguage(String tmp) {
    this.defaultLanguage = tmp;
  }


  /**
   * Gets the language attribute of the Dictionary object
   *
   * @return The language value
   */
  public String getLanguage() {
    return language;
  }


  /**
   * Sets the language attribute of the Dictionary object
   *
   * @param tmp The new language value
   */
  public void setLanguage(String tmp) {
    this.language = tmp;
  }


  /**
   * Gets the localizationPrefs attribute of the Dictionary object
   *
   * @return The localizationPrefs value
   */
  public Map getLocalizationPrefs() {
    return localizationPrefs;
  }


  /**
   * Sets the localizationPrefs attribute of the Dictionary object
   *
   * @param tmp The new localizationPrefs value
   */
  public void setLocalizationPrefs(Map tmp) {
    this.localizationPrefs = tmp;
  }

  public String getTerm(String section, String parameter, String tagName) {
    Map prefGroup = (Map) localizationPrefs.get(section);
    if (prefGroup != null) {
      Node param = (Node) prefGroup.get(parameter);
      if (param != null) {
        return XMLUtils.getNodeText(
            XMLUtils.getFirstChild((Element) param, tagName));
      }
    }
    return null;
  }


  /**
   * Constructor for the Dictionary object
   */
  public Dictionary() {
  }

  public Dictionary(URL languageURL, String arg1, String arg2) throws Exception {
    defaultLanguage = arg1;
    language = arg2;
    URL thisURL = new URL(languageURL.toString() + "dictionary_" + defaultLanguage + ".xml");
    load(thisURL);
    try {
      if (language != null && !defaultLanguage.equals(language)) {
        thisURL = new URL(languageURL.toString() + "dictionary_" + language + ".xml");
        load(thisURL);
      }
    } catch (Exception e) {
      // The language is not yet supported, so skip the dictionary
    }
  }


  public Dictionary(ServletContext context, String languageFilePath, String defaultLanguage) throws Exception {
    this.defaultLanguage = defaultLanguage;
    // Load the default language
    load(context, languageFilePath, defaultLanguage);
  }


  public Dictionary(String languageFilePath, String defaultLanguage) throws Exception {
    this.defaultLanguage = defaultLanguage;
    // Load the default language
    load(null, languageFilePath, defaultLanguage);
  }


  /**
   * Description of the Method
   *
   * @param context          Description of the Parameter
   * @param languageFilePath Description of the Parameter
   * @param language         Description of the Parameter
   * @throws Exception Description of the Exception
   */
  public void load(ServletContext context, String languageFilePath, String language) throws Exception {
    if (languageFilePath == null) {
      throw new Exception("Dictionary file path not provided");
    }
    if (!languageFilePath.endsWith("/") && !languageFilePath.endsWith(System.getProperty("file.separator"))) {
      languageFilePath += System.getProperty("file.separator");
    }
    String languageFilename = "dictionary_" + language + ".xml";
    LOG.debug("Loading dictionary preferences: " + languageFilePath + languageFilename);
    if (context != null) {
      load(context.getResource(languageFilePath + languageFilename));
    } else {
      load(new File(languageFilePath + languageFilename).toURL());
    }
  }

  public void load(URL languageURL) throws Exception {
    XMLUtils xml = new XMLUtils(languageURL);
    if (xml.getDocument() != null) {
      //Traverse the prefs and add the config nodes to the LinkedHashMap,
      //then for each config, add the param nodes into a child LinkedHashMap.
      //This will provide quick access to the values, and will allow an
      //editor to display the fields as ordered in the XML file
      NodeList configNodes = xml.getDocumentElement().getElementsByTagName(
          "config");
      for (int i = 0; i < configNodes.getLength(); i++) {
        Node configNode = configNodes.item(i);
        if (configNode != null &&
            configNode.getNodeType() == Node.ELEMENT_NODE &&
            "config".equals(((Element) configNode).getTagName()) &&
            (((Element) configNode).getAttribute("enabled") == null ||
                "".equals(((Element) configNode).getAttribute("enabled")) ||
                "true".equals(((Element) configNode).getAttribute("enabled")))) {
          //For each config name, create a map for each of the params
          String configName = ((Element) configNode).getAttribute("name");
          Map preferenceGroup = null;
          if (configName != null) {
            if (localizationPrefs.containsKey(configName)) {
              preferenceGroup = (LinkedHashMap) localizationPrefs.get(
                  configName);
            } else {
              preferenceGroup = new LinkedHashMap();
              localizationPrefs.put(configName, preferenceGroup);
            }
            //Process the params for this config
            NodeList paramNodes = ((Element) configNode).getElementsByTagName(
                "param");
            for (int j = 0; j < paramNodes.getLength(); j++) {
              Node paramNode = paramNodes.item(j);
              if (paramNode != null &&
                  paramNode.getNodeType() == Node.ELEMENT_NODE &&
                  "param".equals(((Element) paramNode).getTagName())) {
                String paramName = ((Element) paramNode).getAttribute("name");
                if (paramName != null) {
                  preferenceGroup.put(paramName, paramNode);
                }
              }
            }
          }
        }
      }
    }
  }
}

