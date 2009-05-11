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

package com.concursive.connect.web.modules.wiki.utils;

import com.concursive.commons.text.StringUtils;
import com.concursive.connect.web.controller.beans.URLControllerBean;
import com.concursive.connect.web.modules.wiki.dao.CustomForm;
import com.concursive.connect.web.modules.wiki.dao.CustomFormField;
import com.concursive.connect.web.modules.wiki.dao.CustomFormGroup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to manipulate wiki objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created February 7, 2006
 */
public class HTMLToWikiUtils {

  private static Log LOG = LogFactory.getLog(HTMLToWikiUtils.class);

  public static final String CRLF = "\n";

  public static String htmlToWiki(String html, String contextPath) throws Exception {

    // Strip the nbsp because it gets converted to unicode
    html = StringUtils.replace(html, "&nbsp;", " ");

    // Take the html create DOM for parsing
    HtmlCleaner cleaner = new HtmlCleaner();
    CleanerProperties props = cleaner.getProperties();
    TagNode node = cleaner.clean(html);
    Document document = new DomSerializer(props, true).createDOM(node);
    if (LOG.isTraceEnabled()) {
      LOG.trace(html);
    }

    // Process each node and output the wiki equivalent
    StringBuffer sb = new StringBuffer();
    ArrayList<Node> nodeList = new ArrayList<Node>();
    for (int i = 0; i < document.getChildNodes().getLength(); i++) {
      Node n = document.getChildNodes().item(i);
      nodeList.add(n);
    }
    processChildNodes(nodeList, sb, 0, true, true, contextPath);
    return sb.toString();
  }

  public static void processChildNodes(ArrayList<Node> nodeList, StringBuffer sb, int indentLevel, boolean doText, boolean withFormatting, String contextPath) {
    Iterator nodeI = nodeList.iterator();
    while (nodeI.hasNext()) {
      Node n = (Node) nodeI.next();
      if (n != null) {
        if (n.getNodeType() == Node.TEXT_NODE ||
            n.getNodeType() == Node.CDATA_SECTION_NODE) {
          if (doText) {
            sb.append(StringUtils.fromHtmlValue(n.getNodeValue()));
          }
        } else if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("h1".equals(tag)) {
            startOnNewLine(sb);
            sb.append("= ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" =").append(CRLF);
          } else if ("h2".equals(tag)) {
            startOnNewLine(sb);
            sb.append("== ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" ==").append(CRLF);
          } else if ("h3".equals(tag)) {
            startOnNewLine(sb);
            sb.append("=== ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" ===").append(CRLF);
          } else if ("h4".equals(tag)) {
            startOnNewLine(sb);
            sb.append("==== ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" ====").append(CRLF);
          } else if ("h5".equals(tag)) {
            startOnNewLine(sb);
            sb.append("===== ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" =====").append(CRLF);
          } else if ("h6".equals(tag)) {
            startOnNewLine(sb);
            sb.append("====== ").append(StringUtils.fromHtmlValue(element.getTextContent())).append(" ======").append(CRLF);
          } else if ("p".equals(tag)) {
            if (n.getChildNodes().getLength() > 0 &&
                (hasTextContent(n) || hasImageNodes(n.getChildNodes()))) {
              // If the paragraph contains a Table, UL or OL, skip everything else to get there
              // because there is html junk added by the editor
              if (hasNonTextNodes(n.getChildNodes())) {
                ArrayList<Node> nonTextNodes = new ArrayList<Node>();
                getNonTextNodes(n.getChildNodes(), nonTextNodes);
                if (nonTextNodes.size() > 0) {
                  processChildNodes(nonTextNodes, sb, indentLevel, true, true, contextPath);
                }
              } else {
                // Normal
                startOnNewLine(sb);
                processChildNodes(getNodeList(n), sb, indentLevel, true, true, contextPath);
              }
            }
          } else if ("strong".equals(tag) || "b".equals(tag)) {
            if (n.getChildNodes().getLength() > 0) {
              if ("".equals(StringUtils.fromHtmlValue(n.getTextContent()).trim())) {
                processChildNodes(getNodeList(n), sb, indentLevel, true, false, contextPath);
              } else {
                if (hasNonTextNodes(n.getChildNodes())) {
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, contextPath);
                } else {
                  if (withFormatting) {
                    sb.append("'''");
                  }
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, contextPath);
                  if (withFormatting) {
                    sb.append("'''");
                  }
                }
              }
            }
          } else if ("em".equals(tag) || "i".equals(tag)) {
            if (n.getChildNodes().getLength() > 0) {
              if ("".equals(StringUtils.fromHtmlValue(n.getTextContent()).trim())) {
                processChildNodes(getNodeList(n), sb, indentLevel, true, false, contextPath);
              } else {
                if (hasNonTextNodes(n.getChildNodes())) {
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, contextPath);
                } else {
                  if (withFormatting) {
                    sb.append("''");
                  }
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, contextPath);
                  if (withFormatting) {
                    sb.append("''");
                  }
                }
              }
            }
          } else if ("span".equals(tag)) {
            if (n.getChildNodes().getLength() > 0 && !"".equals(StringUtils.fromHtmlValue(n.getTextContent()).trim())) {
              if (element.hasAttribute("style")) {
                String value = element.getAttribute("style");
                if (withFormatting) {
                  if (value.contains("underline")) {
                    sb.append("__");
                  }
                  if (value.contains("line-through")) {
                    sb.append("<s>");
                  }
                  if (value.contains("bold")) {
                    sb.append("'''");
                  }
                  if (value.contains("italic")) {
                    sb.append("''");
                  }
                }
                processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, contextPath);
                if (withFormatting) {
                  if (value.contains("italic")) {
                    sb.append("''");
                  }
                  if (value.contains("bold")) {
                    sb.append("'''");
                  }
                  if (value.contains("line-through")) {
                    sb.append("</s>");
                  }
                  if (value.contains("underline")) {
                    sb.append("__");
                  }
                }
              }
            }
          } else if ("ul".equals(tag) || "ol".equals(tag)) {
            ++indentLevel;
            if (indentLevel == 1) {
              startOnNewLine(sb);
            }
            if (indentLevel > 1 && !sb.toString().endsWith(CRLF)) {
              sb.append(CRLF);
            }
            processChildNodes(getNodeList(n), sb, indentLevel, false, false, contextPath);
            --indentLevel;
          } else if ("li".equals(tag)) {
            String parentTag = ((Element) element.getParentNode()).getTagName();
            for (int counter = 0; counter < indentLevel; counter++) {
              if ("ul".equals(parentTag)) {
                sb.append("*");
              } else if ("ol".equals(parentTag)) {
                sb.append("#");
              }
            }
            sb.append(" ");
            processChildNodes(getNodeList(n), sb, indentLevel, true, false, contextPath);
            if (!sb.toString().endsWith(CRLF)) {
              sb.append(CRLF);
            }
          } else if ("pre".equals(tag)) {
            startOnNewLine(sb);
            sb.append("<pre>");
            processChildNodes(getNodeList(n), sb, indentLevel, true, true, contextPath);
            sb.append("</pre>");
            if (nodeI.hasNext()) {
              sb.append(CRLF);
              sb.append(CRLF);
            }
          } else if ("code".equals(tag)) {
            startOnNewLine(sb);
            sb.append("<code>");
            processChildNodes(getNodeList(n), sb, indentLevel, true, true, contextPath);
            sb.append("</code>");
            if (nodeI.hasNext()) {
              sb.append(CRLF);
              sb.append(CRLF);
            }
          } else if ("br".equals(tag)) {
            sb.append(CRLF);
          } else if ("table".equals(tag)) {
            // Always start a table on a new line
            startOnNewLine(sb);
            processTable(n.getChildNodes(), sb, 0, false, false);
            //if (nodeI.hasNext()) {
            //  sb.append(CRLF);
            //}
          } else if ("form".equals(tag)) {
            // Always start a form on a new line
            startOnNewLine(sb);
            CustomForm form = processForm(n);
            convertFormToWiki(form, sb);
          } else if ("a".equals(tag)) {
            processLink(sb, element, contextPath);
          } else if ("img".equals(tag)) {
            processImage(sb, n, element, contextPath);
          } else {
            processChildNodes(getNodeList(n), sb, indentLevel, false, true, contextPath);
          }
        }
      }
    }
  }

  private static void startOnNewLine(StringBuffer sb) {
    if (sb.length() > 0) {
      while (!sb.toString().endsWith(CRLF + CRLF)) {
        sb.append(CRLF);
      }
    }
  }

  private static ArrayList<Node> getNodeList(Node n) {
    ArrayList<Node> nodeList = new ArrayList<Node>();
    NodeList nodes = n.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node child = nodes.item(i);
      nodeList.add(child);
    }
    return nodeList;
  }

  private static boolean hasParentNodeType(Node n, String tagToMatch) {
    Node parent = n.getParentNode();
    while (parent != null) {
      if (parent.getNodeType() == Node.ELEMENT_NODE) {
        Element element = ((Element) parent);
        String tag = element.getTagName();
        if (tagToMatch.equals(tag)) {
          return true;
        }
      }
      parent = parent.getParentNode();
    }
    return false;
  }

  private static boolean hasNonTextNodes(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("table".equals(tag) || "ul".equals(tag) || "ol".equals(tag) || "p".equals(tag)) {
            return true;
          }
        }
        boolean found = hasNonTextNodes(n.getChildNodes());
        if (found) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean hasTextContent(Node n) {
    return (!"".equals(n.getTextContent().trim()) &&
        !"&nbsp;".equals(n.getTextContent().trim()));
  }

  private static boolean hasImageNodes(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("img".equals(tag)) {
            return true;
          }
        }
        boolean found = hasImageNodes(n.getChildNodes());
        if (found) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean getNonTextNodes(NodeList nodeList, ArrayList<Node> nodes) {
    // Check the immediate node level
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("table".equals(tag) || "ul".equals(tag) || "ol".equals(tag)) {
            nodes.add(n);
          }
        }
      }
    }
    if (nodes.size() > 0) {
      return true;
    }
    // Check the children nodes
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      getNonTextNodes(n.getChildNodes(), nodes);
    }
    return nodes.size() > 0;
  }


  public static void processTable(NodeList nodeList, StringBuffer sb, int rowCount, boolean doText, boolean anyNodeType) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.TEXT_NODE ||
            n.getNodeType() == Node.CDATA_SECTION_NODE) {
          if (doText && anyNodeType) {
            String thisLine = StringUtils.fromHtmlValue(n.getNodeValue());
            sb.append(thisLine);
          }
        } else if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("tr".equals(tag)) {
            ++rowCount;
            processTable(n.getChildNodes(), sb, rowCount, false, false);
            sb.append(CRLF);
          } else if ("td".equals(tag) || "th".equals(tag)) {
            String separator = "|";
            if (rowCount == 1) {
              separator = "||";
            }
            int colspan = 1;
            if (element.hasAttribute("colspan")) {
              colspan = Integer.parseInt(element.getAttribute("colspan"));
            }
            for (int sI = 0; sI < colspan; sI++) {
              sb.append(separator);
            }
            if (n.getChildNodes().getLength() > 0) {
              if (hasNonTextNodes(n.getChildNodes())) {
                processTable(n.getChildNodes(), sb, rowCount, true, false);
              } else {
                processTable(n.getChildNodes(), sb, rowCount, true, true);
              }
            } else {
              sb.append(" ");
            }
            if (i + 1 == nodeList.getLength()) {
              sb.append(separator);
            }
          } else if ("p".equals(tag)) {
            if (i > 1) {
              sb.append("\n");
              sb.append("!");
            }
            processTable(n.getChildNodes(), sb, rowCount, true, true);
          } else if ("br".equals(tag)) {
            //if (i > 1) {
            sb.append("\n");
            sb.append("!");
            //}
            processTable(n.getChildNodes(), sb, rowCount, true, true);
          } else if ("strong".equals(tag) || "b".equals(tag)) {
            sb.append("'''");
            processTable(n.getChildNodes(), sb, rowCount, true, true);
            sb.append("'''");
          } else if ("em".equals(tag) || "i".equals(tag)) {
            sb.append("''");
            processTable(n.getChildNodes(), sb, rowCount, true, true);
            sb.append("''");
          } else if ("span".equals(tag)) {
            if (element.hasAttribute("style")) {
              String value = element.getAttribute("style");
              // apply the style
              if (value.contains("underline")) {
                sb.append("__");
              }
              if (value.contains("line-through")) {
                sb.append("<s>");
              }
              if (value.contains("bold")) {
                sb.append("'''");
              }
              if (value.contains("italic")) {
                sb.append("''");
              }
              processTable(n.getChildNodes(), sb, rowCount, true, true);
              // apply in reverse
              if (value.contains("italic")) {
                sb.append("''");
              }
              if (value.contains("bold")) {
                sb.append("'''");
              }
              if (value.contains("line-through")) {
                sb.append("</s>");
              }
              if (value.contains("underline")) {
                sb.append("__");
              }
            }
          } else if ("a".equals(tag)) {
            // @todo not supported because parser does a split on | which
            // is allowed in links...
            //processLink(sb, element);
          } else if ("img".equals(tag)) {
            // @todo not supported because parser does a split on | which
            // is allowed in images...
            // processImage(sb, n, element);
          } else {
            processTable(n.getChildNodes(), sb, rowCount, true, true);
          }
        }
      }
    }
  }

  public static void processLink(StringBuffer sb, Element element, String contextPath) {
    // take the href and rip out the document section
    // output the text associated with the link
    String href = element.getAttribute("href");
    String value = element.getTextContent();
    if (StringUtils.hasText(value)) {
      //"<a class=\"wikiLink\" title=\"UI Configurability\" href=\"/team/ProjectManagement.do?command=ProjectCenter&amp;section=Wiki&amp;pid=177&amp;subject=UI+Configurability\">UI Configurability</a></p>\n" +
      sb.append("[[");
      if (isExternalLink(href, contextPath)) {
        // [[http://www.concursive.com Concursive]]
        sb.append(href);
        if (!href.equals(value)) {
          sb.append(" ").append(StringUtils.fromHtmlValue(value));
        }
      } else {
        URLControllerBean url = new URLControllerBean(href, contextPath);
        if ("wiki".equals(url.getDomainObject())) {
          String subject = url.getObjectValue();
          subject = StringUtils.replace(subject, "+", " ");
          sb.append(subject);
          // Base the display on the content of the 'a' tag
          if (!subject.equals(StringUtils.fromHtmlValue(value))) {
            sb.append("|").append(StringUtils.fromHtmlValue(value));
          }
        } else if (!url.hasDomainObject()) {
          // "[[|9999999:project|project unique id|project title]]"
          sb.append("|");
          sb.append(url.getProjectId());
          sb.append(":profile");
          sb.append("|");
          sb.append(url.getProjectTitle());
          sb.append("|");
          sb.append(StringUtils.fromHtmlValue(value));
        } else {
          // "[[|9999999:issue|200|Some issue]]"
          sb.append("|");
          sb.append(url.getProjectId());
          sb.append(":").append(url.getDomainObject());
          sb.append("|");
          sb.append(url.getObjectValue());
          sb.append("|");
          sb.append(StringUtils.fromHtmlValue(value));
        }
      }
      sb.append("]]");
    }
  }

  public static void processImage(StringBuffer sb, Node n, Element element, String contextPath) {
    // Images
    // [[Image:Filename.jpg]]
    // [[Image:Filename.jpg|A caption]]
    // [[Image:Filename.jpg|thumb]]
    // [[Image:Filename.jpg|right]]
    // [[Image:Filename.jpg|left]]

    // <img
    // style="float: right;"
    // title="This is the title of the image"
    // longdesc="http://i.l.cnn.net/cnn/2008/LIVING/personal/04/08/fees/art.fees.jpg?1"
    // src="http://i.l.cnn.net/cnn/2008/LIVING/personal/04/08/fees/art.fees.jpg"
    // alt="This is an image description"
    // width="292"
    // height="219" />

    // image right: float: right; margin: 3px; border: 3px solid black;
    // image left: float: left; margin: 3px; border: 3px solid black;

    // <img
    // src="${ctx}/show/some-company/wiki-image/Workflow+-+Ticket+Example.png"
    // alt="Workflow - Ticket Example.png"
    // width="315"
    // height="362" />

    // if image ends in -th, then this is in thumbnail mode
    String src = element.getAttribute("src");
    String classAttr = element.getAttribute("class");
    // vertical-align: text-bottom; margin: 3px; border: 3px solid black;
    String style = element.getAttribute("style");
    // displays while loading, or if cannot load (defaults to image name on conversion)
    //String alt = element.getAttribute("alt");
    // tooltip (will be used as caption)
    String title = element.getAttribute("title");

    // Construct the wiki markup
    sb.append("[[Image:");

    // Determine if local or remote
    if (!src.contains(".do?command=Img") &&
        !src.contains("/wiki-image/") &&
        (src.startsWith("http://") || src.startsWith("https://"))) {
      // External images url
      sb.append(StringUtils.fromHtmlValue(src));
    } else {
      // Local images link
      URLControllerBean url = new URLControllerBean(src, contextPath);
      String subject = StringUtils.jsUnEscape(url.getObjectValue());
      subject = StringUtils.replace(subject, "+", " ");
      sb.append(subject);
    }

    // frame
    if (classAttr != null && classAttr.contains("wikiFrame") || StringUtils.hasText(title)) {
      sb.append("|frame");
    }
    // thumbnail
    if ("true".equals(extract("th", src))) {
      sb.append("|thumb");
    }
    // position
    if (style != null) {
      if (style.contains("right")) {
        sb.append("|right");
      } else if (style.contains("left")) {
        sb.append("|left");
      }
    }
    // caption
    if (StringUtils.hasText(title)) {
      sb.append("|").append(StringUtils.fromHtmlValue(title));
    }

    // Close the markup
    sb.append("]]");

    // If the image DOES NOT have a title, but it does have a parent <div>, that
    // means the frame was removed in the editor, and two returns need to be added after the image
    // for consistency
    if ((!StringUtils.hasText(title) && hasParentNodeType(n, "div") && !hasParentNodeType(n, "p"))) {
      sb.append(CRLF);
      sb.append(CRLF);
    }
  }

  public static CustomForm processForm(Node formNode) {
    // Each table in the form node is a "Group"
    Element formElement = (Element) formNode;
    // Populate the form object
    CustomForm form = new CustomForm();
    form.setName(formElement.getAttribute("name"));
    // Parse the tables which represent a group of fields
    NodeList tableNodes = formNode.getChildNodes();
    for (int tableI = 0; tableI < tableNodes.getLength(); tableI++) {
      // For each table, parse the rows
      Node tableNode = tableNodes.item(tableI);
      // There will be 1 group per table
      CustomFormGroup group = null;
      // Split the rows into groups and fields...
      NodeList rowNodes = tableNode.getChildNodes();
      for (int rowI = 0; rowI < rowNodes.getLength(); rowI++) {
        // For each row, parse the groups and fields
        Node rowNode = rowNodes.item(rowI);
        NodeList cellNodes = rowNode.getChildNodes();
        // Any number of fields should be found...
        CustomFormField field = null;
        for (int cellI = 0; cellI < cellNodes.getLength(); cellI++) {
          Node cellNode = cellNodes.item(cellI);
          Element cellElement = (Element) cellNode;
          String tag = cellElement.getTagName();
          // Check to see if there is a group row, represented by th
          if ("th".equals(tag)) {
            group = new CustomFormGroup();
            group.setName(cellNode.getTextContent());
          }
          // Construct a field object from each row's tds
          if ("td".equals(tag)) {
            if (field == null) {
              field = new CustomFormField();
              field.setLabel(cellNode.getTextContent());
            } else {
              // TODO: parse the td
              /*
              field.setName();
              field.setType();
              field.setRequired();
              field.setParameters();
              field.setAdditionalText();
              */
            }
          }
        }
        if (field != null) {
          if (group == null) {
            group = new CustomFormGroup();
          }
          group.add(field);
        }
      }
      if (group != null) {
        form.add(group);
      }
    }
    return form;
  }

  public static void convertFormToWiki(CustomForm form, StringBuffer sb) {
    if (form != null) {
      if (StringUtils.hasText(form.getName())) {
        sb.append("[{form name=\"" + form.getName() + "\"}]").append(CRLF);
      } else {
        sb.append("[{form name=\"wikiForm\"}]").append(CRLF);
      }
      // process the groups
      for (CustomFormGroup group : form) {
        sb.append("---").append(CRLF);
        sb.append("[{group value=\"").append(group.getName()).append("\"");
        if (!group.getDisplay()) {
          sb.append(" display=\"false\"");
        }
        sb.append("}]").append(CRLF);
        // process the fields
        for (CustomFormField field : group) {
          sb.append("---").append(CRLF);
          // Field label
          sb.append("[{label value=\"").append(field.getLabel()).append("\"");
          if (!field.getLabelDisplay()) {
            sb.append(" display=\"false\"");
          }
          sb.append("}]").append(CRLF);
          // Field type definition
          sb.append("[{field");
          sb.append(" type=\"").append(field.getTypeAsString()).append("\"");
          sb.append(" name=\"").append(field.getName()).append("\"");
          if (field.getMaxLength() > 0) {
            sb.append(" maxlength=\"").append(field.getMaxLength()).append("\"");
          }
          if (field.getSize() > 0) {
            sb.append(" size=\"").append(field.getSize()).append("\"");
          }
          if (StringUtils.hasText(field.getDefaultValue())) {
            // Handle multi-line values
            String thisValue = StringUtils.replaceReturns(field.getDefaultValue(), "^");
            sb.append(" value=\"").append(thisValue).append("\"");
          }
          if (field.getRequired()) {
            sb.append(" required=\"").append("true").append("\"");
          }
          if (field.getColumns() > -1) {
            sb.append(" cols=\"").append(field.getColumns()).append("\"");
          }
          if (field.getRows() > -1) {
            sb.append(" rows=\"").append(field.getRows()).append("\"");
          }
          if (StringUtils.hasText(field.getOptions())) {
            // TODO: Handle quoted options
            sb.append(" options=\"").append(field.getOptions()).append("\"");
          }
          sb.append("}]").append(CRLF);
          // Field additional text description
          if (StringUtils.hasText(field.getAdditionalText())) {
            sb.append("[{description value=\"").append(field.getAdditionalText()).append("\"}]").append(CRLF);
          }
          // Entered value from user
          if (StringUtils.hasText(field.getValue())) {
            // Handle multi-line values
            String thisValue = StringUtils.replaceReturns(field.getValue(), "^");
            sb.append("[{entry");
            sb.append(" value=\"").append(thisValue).append("\"");
            if (StringUtils.hasText(field.getValueCurrency())) {
              sb.append(" currency=\"").append(field.getValueCurrency()).append("\"");
            }
            sb.append("}]").append(CRLF);
          }
        }
      }
      sb.append("+++");
    }
  }

  /**
   * Extracts a parameter from a given url
   *
   * @param param the url parameter to get the value for
   * @param href  the url to parse
   * @return the found value
   * @deprecated
   */
  public static String extract(String param, String href) {
    int index = href.indexOf("?" + param + "=");
    if (index == -1) {
      index = href.indexOf("&" + param + "=");
    }
    int attrLength = 2;
    if (index == -1) {
      index = href.indexOf("&amp;" + param + "=");
      attrLength = 6;
    }
    int end = href.indexOf("&", index + 1);
    if (end == -1) {
      end = href.length();
    }
    return href.substring(index + param.length() + attrLength, end);
  }

  public static boolean isExternalLink(String href, String contextPath) {
    boolean isExternal = true;
    if (href.startsWith(contextPath + "/show/") ||
        href.startsWith(contextPath + "/modify/")) {
      isExternal = false;
    } else if (!href.startsWith("http://") &&
        !href.startsWith("https://") &&
        !href.startsWith("ftp://") &&
        !href.startsWith("/")) {
      isExternal = false;
    }
    LOG.debug("External link? " + href + " = " + isExternal);
    return isExternal;
  }
}