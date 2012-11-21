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
import com.concursive.commons.xml.XMLUtils;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

  public static String htmlToWiki(String html, String contextPath, int projectId) throws Exception {

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
    processChildNodes(nodeList, sb, 0, true, true, false, "", contextPath, projectId);
    if (sb.length() > 0) {
      String content = sb.toString().trim();
      if (content.contains("&apos;")) {
        // Determine if this is where the &apos; is being introduced
        content = StringUtils.replace(content, "&apos;", "'");
      }
      if (!content.endsWith(CRLF)) {
        return content + CRLF;
      } else {
        return content;
      }
    } else {
      return "";
    }
  }

  public static void processChildNodes(ArrayList<Node> nodeList, StringBuffer sb, int indentLevel, boolean doText, boolean withFormatting, boolean trim, String appendToCRLF, String contextPath, int projectId) {
    Iterator nodeI = nodeList.iterator();
    while (nodeI.hasNext()) {
      Node n = (Node) nodeI.next();
      if (n != null) {
        if (n.getNodeType() == Node.TEXT_NODE ||
            n.getNodeType() == Node.CDATA_SECTION_NODE) {
          if (doText) {
            String value = n.getNodeValue();
            // Escaped characters
            value = StringUtils.replace(value, "*", "\\*");
            value = StringUtils.replace(value, "#", "\\#");
            value = StringUtils.replace(value, "=", "\\=");
            value = StringUtils.replace(value, "|", "\\|");
            value = StringUtils.replace(value, "[", "\\{");
            value = StringUtils.replace(value, "]", "\\}");
            if (trim && !nodeI.hasNext()) {
              // If within a cell, make sure returns include the cell value
//              String value = (appendToCRLF.length() > 0 ? StringUtils.replace(n.getNodeValue(), CRLF, CRLF + appendToCRLF) : n.getNodeValue());
              LOG.trace(" <text:trim>");
              // Output the value, trim is required
              sb.append(StringUtils.fromHtmlValue(value.trim()));
            } else {
              // If within a cell, make sure returns include the cell value
              if (appendToCRLF.length() > 0 && (hasParentNodeType(n, "th") || hasParentNodeType(n, "td")) && value.trim().length() == 0) {
                // This is an empty value... check to see if the previous line has content or not before appending a new line
              } else {
                LOG.trace(" <text>");
                sb.append(StringUtils.fromHtmlValue(
                    (appendToCRLF.length() > 0 ? StringUtils.replace(value, CRLF, CRLF + appendToCRLF) : value)
                ));
              }
            }
          }
        } else if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          LOG.trace(tag);
          if ("h1".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("= ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" =").append(CRLF + appendToCRLF);
          } else if ("h2".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("== ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" ==").append(CRLF + appendToCRLF);
          } else if ("h3".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("=== ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" ===").append(CRLF + appendToCRLF);
          } else if ("h4".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("==== ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" ====").append(CRLF + appendToCRLF);
          } else if ("h5".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("===== ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" =====").append(CRLF + appendToCRLF);
          } else if ("h6".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("====== ").append(StringUtils.fromHtmlValue(element.getTextContent().trim())).append(" ======").append(CRLF + appendToCRLF);
          } else if ("p".equals(tag) || "div".equals(tag)) {
            if (n.getChildNodes().getLength() > 0 &&
                (hasTextContent(n) || hasImageNodes(n.getChildNodes()))) {
              // If this contains a Table, UL, OL, or object skip everything else to get there
              ArrayList<Node> subNodes = new ArrayList<Node>();
              getNodes(n.getChildNodes(), subNodes, new String[]{"table", "ul", "ol", "object"}, false);
              if (subNodes.size() > 0) {
                LOG.trace("  nonTextNodes - yes");
                processChildNodes(subNodes, sb, indentLevel, true, true, false, appendToCRLF, contextPath, projectId);
              } else {
                LOG.trace("  nonTextNodes - no");
                startOnNewLine(sb, appendToCRLF);
                processChildNodes(getNodeList(n), sb, indentLevel, true, true, false, appendToCRLF, contextPath, projectId);
              }
            }
          } else if ("strong".equals(tag) || "b".equals(tag)) {
            if (n.getChildNodes().getLength() > 0) {
              if ("".equals(StringUtils.fromHtmlValue(n.getTextContent()).trim())) {
                processChildNodes(getNodeList(n), sb, indentLevel, true, false, false, appendToCRLF, contextPath, projectId);
              } else {
                if (hasNonTextNodes(n.getChildNodes())) {
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, false, appendToCRLF, contextPath, projectId);
                } else {
                  if (withFormatting) {
                    sb.append("'''");
                  }
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, false, appendToCRLF, contextPath, projectId);
                  if (withFormatting) {
                    sb.append("'''");
                  }
                }
              }
            }
          } else if ("em".equals(tag) || "i".equals(tag)) {
            if (n.getChildNodes().getLength() > 0) {
              if ("".equals(StringUtils.fromHtmlValue(n.getTextContent()).trim())) {
                processChildNodes(getNodeList(n), sb, indentLevel, true, false, trim, appendToCRLF, contextPath, projectId);
              } else {
                if (hasNonTextNodes(n.getChildNodes())) {
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, trim, appendToCRLF, contextPath, projectId);
                } else {
                  if (withFormatting) {
                    sb.append("''");
                  }
                  processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, trim, appendToCRLF, contextPath, projectId);
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
                processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, trim, appendToCRLF, contextPath, projectId);
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
              } else {
                processChildNodes(getNodeList(n), sb, indentLevel, true, withFormatting, trim, appendToCRLF, contextPath, projectId);
              }
            }
          } else if ("ul".equals(tag) || "ol".equals(tag) || "dl".equals(tag)) {
            ++indentLevel;
            if (indentLevel == 1) {
              if (appendToCRLF.length() == 0) {
                startOnNewLine(sb, appendToCRLF);
              } else {
                // Something\n
                // !
                // !* Item 1
                // !* Item 2
                if (!sb.toString().endsWith("|") && !sb.toString().endsWith(CRLF + appendToCRLF)) {
                  LOG.trace("ul newline CRLF");
                  sb.append(CRLF + appendToCRLF);
                }
              }
            }
            if (indentLevel > 1 && !sb.toString().endsWith(CRLF + appendToCRLF)) {
              LOG.trace("ul indent CRLF");
              sb.append(CRLF + appendToCRLF);
            }
            processChildNodes(getNodeList(n), sb, indentLevel, false, false, trim, appendToCRLF, contextPath, projectId);
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
            processChildNodes(getNodeList(n), sb, indentLevel, true, false, true, appendToCRLF, contextPath, projectId);
            if (!sb.toString().endsWith(CRLF + appendToCRLF)) {
              LOG.trace("li CRLF");
              sb.append(CRLF + appendToCRLF);
            }
          } else if ("dt".equals(tag) || "dd".equals(tag)) {
            processChildNodes(getNodeList(n), sb, indentLevel, true, false, trim, appendToCRLF, contextPath, projectId);
            if (!sb.toString().endsWith(CRLF + appendToCRLF)) {
              LOG.trace("dt CRLF");
              sb.append(CRLF + appendToCRLF);
            }
          } else if ("pre".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("<pre>");
            processChildNodes(getNodeList(n), sb, indentLevel, true, true, trim, appendToCRLF, contextPath, projectId);
            sb.append("</pre>");
            if (nodeI.hasNext()) {
              sb.append(CRLF + appendToCRLF);
              sb.append(CRLF + appendToCRLF);
            }
          } else if ("code".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            sb.append("<code>");
            processChildNodes(getNodeList(n), sb, indentLevel, true, true, trim, appendToCRLF, contextPath, projectId);
            sb.append("</code>");
            if (nodeI.hasNext()) {
              sb.append(CRLF + appendToCRLF);
              sb.append(CRLF + appendToCRLF);
            }
          } else if ("br".equals(tag)) {
            LOG.trace("br CRLF");
            sb.append(CRLF + appendToCRLF);
          } else if ("table".equals(tag)) {
            // Always start a table on a new line
            startOnNewLine(sb, appendToCRLF);
            processTable(n.getChildNodes(), sb, 0, false, false, contextPath, projectId, 0);
            //if (nodeI.hasNext()) {
            //  sb.append(CRLF);
            //}
          } else if ("form".equals(tag)) {
            // Always start a form on a new line
            startOnNewLine(sb, appendToCRLF);
            CustomForm form = processForm(n);
            convertFormToWiki(form, sb);
          } else if ("a".equals(tag)) {
            // Determine if the link is around text or around an image
            if (n.getChildNodes().getLength() > 0 && hasImageNodes(n.getChildNodes())) {
              // The link is around an image
              LOG.debug("Processing link as an image");
              // Get the img tag and pass to processImage...
              ArrayList<Node> subNodes = new ArrayList<Node>();
              getNodes(n.getChildNodes(), subNodes, new String[]{"img"}, false);
              processImage(sb, subNodes.get(0), (Element) subNodes.get(0), appendToCRLF, contextPath, projectId);
            } else {
              // The link is around text
              processLink(sb, element, appendToCRLF, contextPath, projectId);
            }
          } else if ("img".equals(tag)) {
            processImage(sb, n, element, appendToCRLF, contextPath, projectId);
          } else if ("object".equals(tag)) {
            startOnNewLine(sb, appendToCRLF);
            processVideo(sb, n, element, appendToCRLF, contextPath);
          } else {
            processChildNodes(getNodeList(n), sb, indentLevel, false, true, trim, appendToCRLF, contextPath, projectId);
          }
        }
      }
    }
  }

  private static void startOnNewLine(StringBuffer sb, String appendToCRLF) {
    if (sb.length() > 0) {
      if (appendToCRLF.length() == 0) {
        while (!sb.toString().endsWith(CRLF + CRLF)) {
          LOG.trace("startOnNewLine CRLF");
          sb.append(CRLF);
        }
      } else {
        // Something\n
        // !
        // !* Item 1
        // !* Item 2
        if (!sb.toString().endsWith("|") && !sb.toString().endsWith(CRLF + appendToCRLF + CRLF + appendToCRLF)) {
          LOG.trace("startOnNewLine CRLF");
          sb.append(CRLF + appendToCRLF + CRLF + appendToCRLF);
        }
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
          if ("table".equals(tag) || "ul".equals(tag) || "ol".equals(tag) || "p".equals(tag) || "div".equals(tag)) {
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
          if ("img".equals(tag) || "object".equals(tag)) {
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

  private static boolean getNodes(NodeList nodeList, ArrayList<Node> nodes, String[] tags, boolean checkChildren) {
    ArrayList<String> list = new ArrayList<String>(Arrays.asList(tags));
    // Check the immediate node level
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if (list.contains(tag)) {
            nodes.add(n);
          }
        }
      }
    }
    if (nodes.size() > 0) {
      return true;
    }
    // Check the children nodes
    if (checkChildren) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node n = nodeList.item(i);
        getNodes(n.getChildNodes(), nodes, tags, checkChildren);
      }
    }
    return nodes.size() > 0;
  }


  public static void processTable(NodeList nodeList, StringBuffer sb, int rowCount, boolean doText, boolean anyNodeType, String contextPath, int projectId, int pass) {
    LOG.trace("line reset");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (n != null) {
        if (n.getNodeType() == Node.TEXT_NODE ||
            n.getNodeType() == Node.CDATA_SECTION_NODE) {
          if (doText && anyNodeType) {
            if (StringUtils.hasText(n.getNodeValue())) {
              String thisLine = StringUtils.fromHtmlValue(n.getNodeValue());
              LOG.trace("table - text: " + thisLine);
              sb.append(thisLine);
            }
          }
        } else if (n.getNodeType() == Node.ELEMENT_NODE) {
          Element element = ((Element) n);
          String tag = element.getTagName();
          if ("tr".equals(tag)) {
            LOG.trace("table - tr");
            ++rowCount;
            if (rowCount == 1) {
              LOG.debug("Looking for style");
              processTable(n.getChildNodes(), sb, rowCount, false, false, contextPath, projectId, 1);
            }
            processTable(n.getChildNodes(), sb, rowCount, false, false, contextPath, projectId, 2);
            sb.append(CRLF);
          } else if ("td".equals(tag) || "th".equals(tag)) {
            if (LOG.isTraceEnabled()) {
              LOG.trace("table - " + tag + " - " + i + " " + hasNonTextNodes(n.getChildNodes()) + " - pass " + pass);
            }
            String separator = "|";
            if (tag.equals("th")) {
              separator = "||";
            }

            // Determin how many columns are spanned by this column
            int colspan = 1;
            if (element.hasAttribute("colspan")) {
              colspan = Integer.parseInt(element.getAttribute("colspan"));
            }
//            if (sb.toString().endsWith(separator)) {
//              sb.append(" ");
//            }

            // Style pass
            boolean hasStyle = false;
            if (pass == 1) {
              if (element.hasAttribute("style")) {
                String style = element.getAttribute("style");
                if (style.endsWith(";")) {
                  style = style.substring(0, style.length() - 1);
                }
                // Start the wiki markup
                for (int sI = 0; sI < colspan; sI++) {
                  sb.append(separator);
                }
                // Append the style data
                sb.append("{").append(style).append("}");
                hasStyle = true;
              }

              // Close the wiki markup if the last cell
              if (hasStyle) {
                if (i + 1 == nodeList.getLength()) {
                  sb.append(separator);
                  // The style pass needs to add it's own CRLF
                  sb.append(CRLF);
                }
              }
            }

            // Data pass
            if (pass == 2) {

              // Start the wiki markup
              for (int sI = 0; sI < colspan; sI++) {
                sb.append(separator);
              }

              if (n.getChildNodes().getLength() > 0) {
                // Cell data uses a "!" for each return
                if (hasNonTextNodes(n.getChildNodes())) {
                  processChildNodes(getNodeList(n), sb, 0, true, true, false, "!", contextPath, projectId);
                } else {
                  processChildNodes(getNodeList(n), sb, 0, true, true, false, "!", contextPath, projectId);
                }
                // If the cell didn't have any data, then add a space
                if (sb.toString().endsWith(separator)) {
                  sb.append(" ");
                }
              } else {
                sb.append(" ");
              }

              // Close the wiki markup
              if (i + 1 == nodeList.getLength()) {
                sb.append(separator);
              }
            }
          } else {
            LOG.trace("table - text - ** " + tag);
            processTable(n.getChildNodes(), sb, rowCount, true, true, contextPath, projectId, 0);
          }
        }
      }
    }
  }

  public static void processLink(StringBuffer sb, Element element, String appendToCRLF, String contextPath, int projectId) {
    // take the href and rip out the document section
    // output the text associated with the link
    String href = element.getAttribute("href").trim();

    // See if the href has content, it won't if it is an image link
    String value = element.getTextContent();

    // Create the markup
    //"<a class=\"wikiLink\" title=\"UI Configurability\" href=\"/team/ProjectManagement.do?command=ProjectCenter&amp;section=Wiki&amp;pid=177&amp;subject=UI+Configurability\">UI Configurability</a></p>\n" +
    sb.append("[[");
    if (isExternalLink(href, contextPath)) {
      // [[http://www.concursive.com Concursive]]
      sb.append(href);
      if (StringUtils.hasText(value) && !href.equals(value)) {
        // The link has a name
        sb.append(" ").append(StringUtils.fromHtmlValue(value.trim()));
      }
    } else {
      URLControllerBean url = new URLControllerBean(href, contextPath);
      if ("wiki".equals(url.getDomainObject()) &&
          (url.getProjectId() == projectId || url.getProjectId() == -1)) {
        String subject = url.getObjectValue();
        // The incoming link will have a + for a space
        subject = StringUtils.replace(subject, "+", " ");
        // The incoming link will be url encoded
        subject = StringUtils.jsUnEscape(subject);
        sb.append(subject);
        // Base the display on the content of the 'a' tag
        if (StringUtils.hasText(value) && !subject.equals(StringUtils.fromHtmlValue(value))) {
          sb.append("|").append(StringUtils.fromHtmlValue(value));
        }
      } else if (!url.hasDomainObject()) {
        // "[[|9999999:project|project unique id|project title]]"
        sb.append("|");
        sb.append(url.getProjectId());
        sb.append(":profile");
        sb.append("|");
        sb.append(url.getProjectTitle());
        if (StringUtils.hasText(value)) {
          sb.append("|");
          sb.append(StringUtils.fromHtmlValue(value));
        }
      } else {
        // "[[|9999999:issue|200|Some issue]]"
        sb.append("|");
        sb.append(url.getProjectId());
        sb.append(":").append(url.getDomainObject());
        sb.append("|");
        String subject = url.getObjectValue();
        // The incoming link will have a + for a space
        subject = StringUtils.replace(subject, "+", " ");
        // The incoming link will be url encoded
        subject = StringUtils.jsUnEscape(subject);
        sb.append(subject);
        if (StringUtils.hasText(value)) {
          sb.append("|");
          sb.append(StringUtils.fromHtmlValue(value));
        }
      }
    }
    sb.append("]]");
  }

  public static void processImage(StringBuffer sb, Node n, Element element, String appendToCRLF, String contextPath, int projectId) {
    // Images
    // [[Image:Filename.jpg]]
    // [[Image:Filename.jpg|A caption]]
    // [[Image:Filename.jpg|thumb]]
    // [[Image:Filename.jpg|right]]
    // [[Image:Filename.jpg|left]]

    // <img
    // style="float: right;"
    // style="display:block;margin: 0 auto;"
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

    // See if the parent is a link
    String link = null;
    String title = null;
    if (hasParentNodeType(n, "a")) {
      // Get the attributes of the link
      StringBuffer linkSB = new StringBuffer();
      processLink(linkSB, (Element) element.getParentNode(), appendToCRLF, contextPath, projectId);
      link = linkSB.substring(2, linkSB.length() - 2);
    } else {
      // tooltip (will be used as caption), but not for links
      title = element.getAttribute("title");
    }

    // Determine if this is an embedded video link
    if (link != null && link.startsWith("http://www.youtube.com/v/")) {
      processVideoLink(sb, n, element, appendToCRLF, contextPath, link);
    } else {
      processImage(sb, n, element, appendToCRLF, contextPath, projectId, link, title);
    }
  }

  private static void processImage(StringBuffer sb, Node n, Element element, String appendToCRLF, String contextPath, int projectId, String link, String title) {

    // if image ends in -th, then this is in thumbnail mode
    String src = element.getAttribute("src");
    String classAttr = element.getAttribute("class");
    // vertical-align: text-bottom; margin: 3px; border: 3px solid black;
    String style = element.getAttribute("style");
    // displays while loading, or if cannot load (defaults to image name on conversion)
    //String alt = element.getAttribute("alt");

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
      subject = StringUtils.replace(subject, "[", "\\{");
      subject = StringUtils.replace(subject, "]", "\\}");
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
      if (style.contains("float: right") || style.contains("float:right")) {
        sb.append("|right");
      } else if (style.contains("float: left") || style.contains("float:left")) {
        sb.append("|left");
      } else if (style.contains("block")) {
        sb.append("|center");
      }
    }
    // caption (but not for links)
    if (StringUtils.hasText(title)) {
      sb.append("|").append(StringUtils.fromHtmlValue(title));
    }
    // link
    // @note link= must be last!
    if (link != null) {
      sb.append("|link=").append(link);
    }

    // Close the markup
    sb.append("]]");

    // If the image DOES NOT have a title, but it does have a parent <div>, that
    // means the frame was removed in the editor, and two returns need to be added after the image
    // for consistency
    if ((!StringUtils.hasText(title) && hasParentNodeType(n, "div") && !hasParentNodeType(n, "p"))) {
      LOG.trace("CRLF");
      LOG.trace("CRLF");
      sb.append(CRLF + appendToCRLF);
      sb.append(CRLF + appendToCRLF);
    }
  }

  public static void processVideo(StringBuffer sb, Node n, Element element, String appendToCRLF, String contextPath) {
    // Determine if a thumbnail
    boolean thumbnail = false;
    if ("120".equals(element.getAttribute("width"))) {
      thumbnail = true;
    }

    // Object (w/out embed)
    if (element.hasAttribute("value")) {
          LOG.trace("  value");
      //<object width="400" height="300" value="http://vimeo.com/moogaloop.swf?clip_id=11513988&amp;server=vimeo.com&amp;show_title=1&amp;show_byline=1&amp;show_portrait=0&amp;color=&amp;fullscreen=1">
      String video = element.getAttribute("value");
      if (video.contains("http://www.vimeo.com/") || video.contains("http://vimeo.com/")) {
        video = video.substring(video.indexOf("/", video.indexOf("http://") + 7) + 1);
        if (video.contains("#")) {
          video = video.substring(0, video.indexOf("#"));
        }
        if (video.contains("&")) {
          video = video.substring(0, video.indexOf("&"));
        }
        video = "http://www.vimeo.com/" + video.substring(video.indexOf("clip_id") + 8);
      }
      // Write out the markup
      try {
        URL url = new URL(video);
        sb.append("[[Video:" + video + (thumbnail ? "|thumb" : "") + "]]");
      } catch (Exception e) {
        LOG.error("Could not create URL", e);
      }
    } else if (element.hasAttribute("data")) {
      LOG.trace("  data");
      //<object width="425" height="344" type="application/x-shockwave-flash" data="http://www.youtube.com/v/CreiaYbjda4"></object>
      String video = element.getAttribute("data");

      // Justin.tv
      //<object type="application/x-shockwave-flash" height="300" width="400" id="live_embed_player_flash" data="http://www.justin.tv/widgets/live_embed_player.swf?channel=redspades" bgcolor="#000000"><param name="allowFullScreen" value="true" /><param name="allowScriptAccess" value="always" /><param name="allowNetworking" value="all" /><param name="movie" value="http://www.justin.tv/widgets/live_embed_player.swf" /><param name="flashvars" value="channel=redspades&auto_play=false&start_volume=25" /></object><a href="http://www.justin.tv/redspades#r=TRp3S20~&s=em" class="trk" style="padding:2px 0px 4px; display:block; width:345px; font-weight:normal; font-size:10px; text-decoration:underline; text-align:center;">Watch live video from RedSpades Live! Show on Justin.tv</a>
      if (video.contains("http://www.justin.tv/")) {
        video = "http://www.justin.tv/" + video.substring(video.indexOf("channel=") + 8);
        if (video.contains("#")) {
          video = video.substring(0, video.indexOf("#"));
        }
      }
      // otherwise Youtube/Google

      // Write out the markup
      try {
        URL url = new URL(video);
        sb.append("[[Video:" + video + (thumbnail ? "|thumb" : "") + "]]");
      } catch (Exception e) {
        LOG.error("Could not create URL", e);
      }
    } else {
      LOG.trace("  checking nodes");
      // Parse the object parameters

      // <object width=\"425\" height=\"344\">\n
      //     <param name=\"movie\" value=\"" + video + "\"></param>\n" +
      //     <param name=\"wmode\" value=\"transparent\"></param>\n" +
      //     <embed src=\"" + video + "\" type=\"application/x-shockwave-flash\" wmode=\"transparent\" width=\"425\" height=\"350\"></embed>\n" +
      // </object>

      NodeList objectNodes = element.getChildNodes();
      boolean foundQikVideo = false;
      boolean handleException = false;
      for (int i = 0; i < objectNodes.getLength(); i++) {
        // For each object, parse the params
        Node node = objectNodes.item(i);
        LOG.trace("    " + ((Element) node).getTagName());
        if (node.getNodeType() == Node.ELEMENT_NODE && "param".equals(((Element) node).getTagName())) {
          if ("movie".equals(((Element) node).getAttribute("name"))) {
            String video = ((Element) node).getAttribute("value");
            // Ustream
            if (video.contains("ustream.tv/")) {
              handleException = true;
              String ustreamLink = XMLUtils.toString(n);
              ustreamLink = StringUtils.replace(ustreamLink, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
              ustreamLink = StringUtils.replace(ustreamLink, "\r", "");
              ustreamLink = StringUtils.replace(ustreamLink, "\n", "");
              ustreamLink = StringUtils.replace(ustreamLink, "&#13;", "");
              sb.append("[[Video:" + ustreamLink + "]]");
            }
            // Qik.com
            if (video.contains("qik.com")) {
              foundQikVideo = true;
              handleException = true;
            }
            // Livestream.com
            if (video.indexOf("livestream.com") > -1) {
              //"<param name=\"movie\" value=\"http://cdn.livestream.com/grid/LSPlayer.swf?channel=" + channel + "&amp;autoPlay=true\"></param>" +
              String channel = video.substring(video.indexOf("channel=") + 8, video.indexOf("&"));
              video = "http://www.livestream.com/" + channel;
            }
            // otherwise assume Youtube/Google
            if (!handleException) {
              // Write out the markup
              try {
                URL url = new URL(video);
                sb.append("[[Video:" + video + (thumbnail ? "|thumb" : "") + "]]");
              } catch (Exception e) {
                LOG.error("Could not create URL", e);
              }
            }
          } else if ("FlashVars".equals(((Element) node).getAttribute("name"))) {
            String value = ((Element) node).getAttribute("value");
            // Qik.com
            if (foundQikVideo && value.contains("username=")) {
              String username = value.substring(value.indexOf("username=") + 9);
              sb.append("[[Video:" + "http://qik.com/" + username + (thumbnail ? "|thumb" : "") + "]]");
            }
          }
        }
      }
    }
  }

  public static void processVideoLink(StringBuffer sb, Node n, Element element, String appendToCRLF, String contextPath, String link) {
    // Videos
    // [[Video:http://www.youtube.com/watch?v=3LkNlTNHZzE|thumb]]

    // <div class="video-thumbnail">
    // <a class="video-thumbnail" href=\"" + video + "?autoplay=1\" title=\"click to play\" target=\"_blank\"><img src=\"http://img.youtube.com/vi/" + videoKey + "/default.jpg\" /></a>
    // </div>

    String video = link;
    if (video.contains("?")) {
      video = video.substring(0, video.indexOf("?"));
    }
    try {
      URL url = new URL(video);
      sb.append("[[Video:" + video + "|thumb]]");
    } catch (Exception e) {
      LOG.error("Could not create URL", e);
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
    if (index == -1) {
      return null;
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
        !href.startsWith("mailto:") &&
        !href.startsWith("/")) {
      isExternal = false;
    }
    LOG.debug("External link? " + href + " = " + isExternal);
    return isExternal;
  }
}
