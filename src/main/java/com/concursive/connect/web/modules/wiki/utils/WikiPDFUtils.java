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

import com.concursive.commons.date.DateUtils;
import com.concursive.commons.db.DatabaseUtils;
import com.concursive.commons.phone.PhoneNumberBean;
import com.concursive.commons.phone.PhoneNumberUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.dao.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class to manipulate wiki objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 26, 2006
 */
public class WikiPDFUtils {

  private static final Log LOG = LogFactory.getLog(WikiPDFUtils.class);

  public static String CRLF = "\n";
  public final static String fs = System.getProperty("file.separator");
  // Define styles
  public final static Font titleFont = FontFactory.getFont(FontFactory.TIMES, 24, Font.NORMAL, new Color(0, 0, 0));
  public final static Font titleSmallFont = FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL, new Color(0, 0, 0));
  public final static Font wikiFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.ITALIC, new Color(0, 0, 0));
  public final static Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA, 20, Font.NORMAL, new Color(0, 0, 0));
  public final static Font section2Font = FontFactory.getFont(FontFactory.HELVETICA, 18, Font.NORMAL, new Color(0, 0, 0));
  public final static Font section3Font = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.NORMAL, new Color(0, 0, 0));
  public final static Font codeFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL, new Color(0, 0, 0));


  public static boolean functionsToUse(Project project, Wiki wiki, File file, HashMap imageList) throws Exception {
    // remove this.
    Document document = new Document();

    // Parse each line to see if paragraph, table, section heading, list
    // Parse within the line to see if bold, italic, links, code fragment, images

    // Define styles
    Font exampleFont = FontFactory.getFont(FontFactory.HELVETICA, 24, Font.NORMAL, new Color(255, 0, 0));

    // Add title and paragraphs

    // TODO: Construct a title page... with logo
    Paragraph title = new Paragraph(wiki.getSubject(), exampleFont);

    //Chunk title = new Chunk(wiki.getSubject());
    //title.setBackground(new Color(0xFF, 0xDE, 0xAD));
    ////title.setUnderline(new Color(0xFF, 0x00, 0x00), 3.0f, 0.0f, -4.0f, 0.0f, PdfContentByte.LINE_CAP_ROUND);
    //title.setUnderline(0.2f, -2f);
    LOG.debug("document.add(title)");
    document.add(title);

    // Link from
    // a paragraph with a local goto
    //Paragraph p1 = new Paragraph("We will do something special with this paragraph. If you click on ", FontFactory.getFont(FontFactory.HELVETICA, 12));
    //p1.add(new Chunk("this word", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))).setLocalGoto("test"));
    //p1.add(" you will automatically jump to another location in this document.");

    // a paragraph with a local destination
    //Paragraph p3 = new Paragraph("This paragraph contains a ");
    //p3.add(new Chunk("local destination", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 255, 0))).setLocalDestination("test"));

    // Ext A Href
    //Paragraph paragraph = new Paragraph("Please visit my ");
    //Anchor anchor1 = new Anchor("website (external reference)", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255)));
    //anchor1.setReference("http://www.lowagie.com/iText/");
    //anchor1.setName("top");
    //paragraph.add(anchor1);

    //Image jpg = Image.getInstance("otsoe.jpg");
    //document.add(jpg);

    return false;
  }

  public static boolean exportToFile(WikiPDFContext context, Connection db) throws Exception {

    LOG.debug("exportToFile-> begin");

    // Context Objects
    Wiki wiki = context.getWiki();
    Project project = context.getProject();
    File file = context.getFile();
    WikiExportBean exportBean = context.getExportBean();

    // Determine the content to parse
    String content = wiki.getContent();
    if (content == null) {
      return false;
    }

    // Create a pdf
    Document document = new Document(PageSize.LETTER);
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));

    // Meta data
    document.addTitle(project.getTitle());
    document.addSubject(wiki.getSubject());
    document.addCreator("Concursive ConcourseConnect");
    document.addAuthor("Wiki Contributor");
    //writer.setPageEvent(new PageNumbersWatermark());

    if (!exportBean.getIncludeTitle()) {
      boolean hasTitle = StringUtils.hasText(wiki.getSubject());
      HeaderFooter pageFooter = new HeaderFooter(new Phrase(project.getTitle() + (hasTitle ? ": " + wiki.getSubject() : "") + " - page "), new Phrase(""));
      pageFooter.setAlignment(Element.ALIGN_CENTER);
      document.setFooter(pageFooter);
    }

    document.open();

    if (exportBean.getIncludeTitle()) {
      //HeaderFooter pageHeader = new HeaderFooter(new Phrase(project.getTitle()), false);
      //document.setHeader(pageHeader);
      boolean hasTitle = (StringUtils.hasText(wiki.getSubject()));
      HeaderFooter pageFooter = new HeaderFooter(new Phrase(project.getTitle() + (hasTitle ? ": " + wiki.getSubject() : "") + " - page "), new Phrase(""));
      pageFooter.setAlignment(Element.ALIGN_CENTER);
      document.setFooter(pageFooter);

      // Draw a title page
      Rectangle rectangle = new Rectangle(600, 30);
      rectangle.setBackgroundColor(new Color(100, 100, 100));
      LOG.debug("document.add(rectangle)");
      document.add(rectangle);

      document.add(new Paragraph(project.getTitle(), titleFont));
      if (!"".equals(wiki.getSubject())) {
        document.add(new Paragraph(wiki.getSubject(), titleFont));
      }
      document.add(Chunk.NEWLINE);
      document.add(new Paragraph("Last Modified: " + wiki.getModified(), titleSmallFont));
      document.newPage();
    }

    ArrayList<Integer> wikiListDone = new ArrayList<Integer>();

    appendWiki(context, context.getWiki(), document, db, wikiListDone);
    // Close everything
    document.close();
    writer.close();
    LOG.debug("exportToFile-> finished");
    return true;
  }

  private static void appendWiki(WikiPDFContext context, Wiki currentWiki, Document document, Connection db, ArrayList<Integer> wikiListDone) throws SQLException {

    LOG.debug("appendWiki-> " + currentWiki.getSubject());

    // Context Objects
    Project project = context.getProject();
    WikiExportBean exportBean = context.getExportBean();

    // Track the wikis to get appended to the output
    ArrayList<Integer> wikiListTodo = new ArrayList<Integer>();

    try {
      // Output the name of the Wiki
      boolean hasTitle = StringUtils.hasText(currentWiki.getSubject());
      if (hasTitle) {
        Anchor wikiAnchor = new Anchor(currentWiki.getSubject(), wikiFont);
        wikiAnchor.setName(currentWiki.getSubject().toLowerCase());
        LOG.debug("Add anchor: " + currentWiki.getSubject().toLowerCase());
        document.add(wikiAnchor);
        LOG.debug("document.add(wikiAnchor)");
      }

      // Output the wiki content
      parseContent(context, currentWiki, currentWiki.getContent(), document, null, db, wikiListTodo, wikiListDone, 0f);
      wikiListDone.add(currentWiki.getId());

      // See if any linked wikis should be appended
      if (exportBean.getFollowLinks() && wikiListTodo.size() > 0) {
        Iterator i = wikiListTodo.iterator();
        while (i.hasNext()) {
          Integer id = (Integer) i.next();
          if (id > -1 && !wikiListDone.contains(id)) {
            Wiki subwiki = new Wiki(db, id);
            document.add(Chunk.NEXTPAGE);
            appendWiki(context, subwiki, document, db, wikiListDone);
          }
          //i.remove();
        }
      }
    } catch (Exception e) {
      LOG.error("appendWiki", e);
    }
  }


  private static boolean parseContent(WikiPDFContext context, Wiki wiki, String content, Document document, PdfPCell cell, Connection db, ArrayList<Integer> wikiListTodo, ArrayList<Integer> wikiListDone, float cellWidth) throws Exception {

    LOG.debug("PARSING CONTENT: " + content);

    // Parse the wiki page
    int lastIndent = 0;
    boolean preTag = false;
    boolean pre = false;
    boolean code = false;
    boolean header = true;

    try {

      BufferedReader in = new BufferedReader(new StringReader(content));
      String line = null;

      ArrayList unorderedParents = null;
      List thisList = null;
      Paragraph codeParagraph = null;

      while ((line = in.readLine()) != null) {
        // Tables
        if (line.startsWith("|")) {
          // @todo Close all ordered lists, unordered lists, and paragraphs


          // Parse the table
          line = parseTable(context, wiki, line, document, db, wikiListTodo, wikiListDone, in);

          if (line == null) {
            continue;
          }
        }

        // Forms
        if (line.startsWith("[{form")) {
          // @todo close any lists or paragraphs

          // parseForm operates over all the lines that make up the form,
          // it will have to look forward so it returns an unparsed line
          parseForm(context, db, in, line, document, cell);
          continue;
        }


        // Handle code blocks
        // @todo chunk the content similar to WikiToHTMLUtils otherwise inaccurate
        if (line.startsWith("<pre>") || line.startsWith("<code>")) {
          if (!code && !pre) {
            if (line.startsWith("<pre>")) {
              preTag = true;
              pre = true;
            } else if (line.startsWith("<code>")) {
              code = true;
            }
            codeParagraph = new Paragraph("", codeFont);
            codeParagraph.setSpacingBefore(10);

            if (pre && line.length() > ("<pre>").length()) {
              int endOfLine = line.length();
              if (line.endsWith("</pre>")) {
                endOfLine = line.indexOf("</pre>");
              }
              // This line has some extra content that needs to be added
              codeParagraph.add(new Chunk(line.substring(line.indexOf("<pre>") + 5, endOfLine) + Chunk.NEWLINE));
            }
            if (code && line.length() > ("<code>").length()) {
              int endOfLine = line.length();
              if (line.endsWith("</code>")) {
                endOfLine = line.indexOf("</code>");
              }
              // This line has some extra content that needs to be added
              codeParagraph.add(new Chunk(line.substring(line.indexOf("<code>") + 6, endOfLine) + Chunk.NEWLINE));
            }
            // See if this is a single line block
            if (preTag && line.endsWith("</pre>")) {
              // This is a single line block, so finish processing it
            } else if (code && line.endsWith("</code>")) {
              // This is a single line block, so finish processing it
            } else {
              // There are more lines to process, so do that
              continue;
            }
          }
        }
        if (line.startsWith("</code>") || line.endsWith("</code>")) {
          if (code) {
            code = false;
            if (line.indexOf("</code>") > 0 && !line.startsWith("<code>")) {
              // This line has some extra content that needs to be added
              codeParagraph.add(new Chunk(line.substring(0, line.indexOf("</code>")) + Chunk.NEWLINE));
            }
            // Draw the final content
            PdfPTable codeTable = new PdfPTable(1);
            codeTable.setWidthPercentage(100);
            codeTable.setSpacingBefore(10);
            PdfPCell codeCell = new PdfPCell(codeParagraph);
            codeCell.setPadding(20);
            codeCell.setBorderColor(new Color(100, 100, 100));
            codeCell.setBackgroundColor(new Color(200, 200, 200));
//            codeCell.setNoWrap(true);
            codeTable.addCell(codeCell);
            LOG.debug("document.add(codeTable)");
            document.add(codeTable);
            continue;
          }
        }
        if (line.startsWith("</pre>") || line.endsWith("</pre>")) {
          if (pre) {
            preTag = false;
            pre = false;
            if (line.indexOf("</pre>") > 0 && !line.startsWith("<pre>")) {
              // This line has some extra content that needs to be added
              codeParagraph.add(new Chunk(line.substring(0, line.indexOf("</pre>")) + Chunk.NEWLINE));
            }
            // Draw the final content
            PdfPTable codeTable = new PdfPTable(1);
            codeTable.setWidthPercentage(100);
            codeTable.setSpacingBefore(10);
            PdfPCell codeCell = new PdfPCell(codeParagraph);
            codeCell.setPadding(20);
            codeCell.setBorderColor(new Color(100, 100, 100));
            codeCell.setBackgroundColor(new Color(200, 200, 200));
//            codeCell.setNoWrap(true);
            codeTable.addCell(codeCell);
            LOG.debug("document.add(codeTable)");
            document.add(codeTable);
            continue;
          }
        }
        if (code || preTag) {
          // Append the chunk
          codeParagraph.add(new Chunk(line + Chunk.NEWLINE));
          continue;
        }

        // Section
        if (line.startsWith("=") && line.endsWith("=")) {
          // @todo close any open lists or paragraphs

          int hCount = parseHCount(line, "=");
          if (hCount > 6) {
            hCount = 6;
          }
          String section = line.substring(line.indexOf("=") + hCount, line.lastIndexOf("=") - hCount + 1);
          header = true;
          context.foundHeader(hCount);
          String headerAnchor = null;

          // Store the h2's with anchors for table of contents or index
          if (hCount == 2) {
            headerAnchor = StringUtils.toHtmlValue(section).replace(" ", "_");
            context.getHeaderAnchors().put(headerAnchor, section);
          }
          if (hCount == 3) {
            Paragraph title = new Paragraph(section.trim(), section2Font);
            title.setSpacingBefore(10);
            if (cell != null) {
              LOG.debug("phrase.add(title)");
              cell.addElement(title);
            } else {
              LOG.debug("document.add(title)");
              document.add(title);
            }
          } else if (hCount > 3) {
            Paragraph title = new Paragraph(section.trim(), section3Font);
            title.setSpacingBefore(10);
            if (cell != null) {
              LOG.debug("phrase.add(title)");
              cell.addElement(title);
            } else {
              LOG.debug("document.add(title)");
              document.add(title);
            }
          } else {
            Paragraph title = new Paragraph(section.trim(), sectionFont);
            title.setSpacingBefore(10);
            if (cell != null) {
              LOG.debug("phrase.add(title)");
              cell.addElement(title);
            } else {
              LOG.debug("document.add(title)");
              document.add(title);
            }
          }
          continue;
        }
        if (header) {
          header = false;
          if (line.trim().equals("")) {
            // remove the extra space a user may leave after a header
            continue;
          }
        }


        // Determine if this is a bulleted list
        if (line.startsWith("*") || line.startsWith("#")) {
          // Initialize the list array
          if (unorderedParents == null) {
            unorderedParents = new ArrayList();
//            if (phrase != null) {
//              LOG.debug("phrase.add(new Paragraph(Chunk.NEWLINE))");
//              phrase.add(new Paragraph(Chunk.NEWLINE));
//            } else {
//              LOG.debug("document.add(new Paragraph(Chunk.NEWLINE))");
//              document.add(new Paragraph(Chunk.NEWLINE));
//            }
          }
          // Get the indent level
          boolean ol = line.startsWith("#");
          int hCount = WikiPDFUtils.parseHCount(line, ol ? "#" : "*");
          // Determine a shift in the tree
          if (lastIndent == 0) {
            if (ol) {
              thisList = new List(ol, 20);
            } else {
              thisList = new List(ol, 10);
              thisList.setListSymbol(new Chunk("\u2022", FontFactory.getFont(FontFactory.HELVETICA, 12)));
            }
            thisList.setIndentationLeft(36);
            thisList.setIndentationRight(36);
            unorderedParents.add(thisList);
          } else {
            if (hCount > lastIndent) {
              if (ol) {
                thisList = new List(ol, 20);
              } else {
                thisList = new List(ol, 10);
                thisList.setListSymbol(new Chunk("\u2022", FontFactory.getFont(FontFactory.HELVETICA, 12)));
              }
              thisList.setIndentationLeft(36);
              thisList.setIndentationRight(36);
              ((List) unorderedParents.get(unorderedParents.size() - 1)).add(thisList);
              unorderedParents.add(thisList);
            } else if (hCount < lastIndent) {
              unorderedParents.remove(unorderedParents.size() - 1);
              thisList = (List) unorderedParents.get(unorderedParents.size() - 1);
            }
          }
          lastIndent = hCount;
          // Append the item...
          Paragraph thisItem = new Paragraph();
          parseLine(context, line.substring(hCount).trim(), thisItem, db, wikiListTodo, cellWidth, cell);
          thisList.add(new ListItem(thisItem));
          continue;
        }
        // List is finished, so append it to the document before working on
        // other paragraphs
        if (unorderedParents != null) {
          if (cell != null) {
            LOG.debug("phrase.add((List) unorderedParents.get(0))");
            cell.addElement((List) unorderedParents.get(0));
          } else {
            LOG.debug("document.add((List) unorderedParents.get(0))");
            document.add((List) unorderedParents.get(0));
          }
          unorderedParents = null;
          thisList = null;
          lastIndent = 0;
        }

        // Otherwise a simple paragraph
        Paragraph paragraph = new Paragraph();
        parseLine(context, line, paragraph, db, wikiListTodo, cellWidth, cell);
        if (cell != null) {
          LOG.debug("phrase.add(paragraph)");
          if (cell.getHorizontalAlignment() == Element.ALIGN_CENTER) {
            paragraph.setAlignment(Element.ALIGN_CENTER);
          }
          paragraph.setSpacingBefore(5);
          cell.addElement(paragraph);
        } else {
          LOG.debug("document.add(paragraph)");
          paragraph.setSpacingBefore(5);
          document.add(paragraph);
        }
      }

      // Cleanup now that the lines are finished
      if (pre || code) {
        PdfPTable codeTable = new PdfPTable(1);
        codeTable.setWidthPercentage(100);
        codeTable.setSpacingBefore(10);
        PdfPCell codeCell = new PdfPCell(codeParagraph);
        codeCell.setPadding(20);
        codeCell.setBorderColor(new Color(100, 100, 100));
        codeCell.setBackgroundColor(new Color(200, 200, 200));
//        codeCell.setNoWrap(true);
        codeTable.addCell(codeCell);
        LOG.debug("document.add(codeTable)");
        document.add(codeTable);
      }
      if (unorderedParents != null) {
        if (cell != null) {
          LOG.debug("phrase.add((List) unorderedParents.get(0))");
          cell.addElement((List) unorderedParents.get(0));
        } else {
          LOG.debug("document.add((List) unorderedParents.get(0))");
          document.add((List) unorderedParents.get(0));
        }
      }
      in.close();
    } catch (Exception e) {
      LOG.error("parseContent", e);
    }
    return true;
  }

  protected static int parseHCount(String line, String id) {
    int count = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (id.equals(String.valueOf(c))) {
        ++count;
      } else {
        return count;
      }
    }
    return 1;
  }

  private static String parseTable(WikiPDFContext context, Wiki wiki, String line, Document document, Connection db, ArrayList<Integer> wikiListTodo, ArrayList<Integer> wikiListDone, BufferedReader in) throws Exception {
    if (line == null) {
      return null;
    }
    PdfPTable pdfTable = null;
    int columnCount = 0;
    int rowCount = 0;

    // Keep track of the table's custom styles
    HashMap<Integer, String> cStyle = new HashMap<Integer, String>();

    while (line != null && (line.startsWith("|") || line.startsWith("!"))) {

      // Build a complete line
      String lineToParse = line;
      while (!line.endsWith("|")) {
        line = in.readLine();
        if (line == null) {
          // there is an error in the line to process
          return null;
        }
        if (line.startsWith("!")) {
          lineToParse += CRLF + line.substring(1);
        }
      }
      line = lineToParse;

      // Determine if the row can output
      boolean canOutput = true;

      ++rowCount;

      String cellType = null;
      Scanner sc = null;
      if (line.startsWith("||") && line.endsWith("||")) {
        cellType = "th";
        sc = new Scanner(line).useDelimiter("[|][|]");
//        sc = new Scanner(line.substring(2, line.length() - 2)).useDelimiter("[|][|]");
      } else if (line.startsWith("|")) {
        cellType = "td";
        sc = new Scanner(line.substring(1, line.length() - 1)).useDelimiter("\\|(?=[^\\]]*(?:\\[|$))");
      }

      if (sc != null) {

        if (rowCount == 1) {
          // Count the columns, get the specified widths too...
          while (sc.hasNext()) {
            ++columnCount;
            sc.next();
          }
          // Reset the scanner now that the columns have been counted
          if (line.startsWith("||") && line.endsWith("||")) {
            sc = new Scanner(line).useDelimiter("[|][|]");
          } else if (line.startsWith("|")) {
            sc = new Scanner(line.substring(1, line.length() - 1)).useDelimiter("\\|(?=[^\\]]*(?:\\[|$))");
          }

          // Start the table
          pdfTable = new PdfPTable(columnCount);
          //pdfTable.setWidthPercentage(100);
          pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
          pdfTable.setSpacingBefore(10);
          pdfTable.setWidthPercentage(100);
          pdfTable.setKeepTogether(true);
        }


        // Determine the column span
        int colSpan = 1;
        // Determine the cell being output
        int cellCount = 0;

        while (sc.hasNext()) {
          String cellData = sc.next();
          if (cellData.length() == 0) {
            ++colSpan;
            continue;
          }

          // Track the cell count being output
          ++cellCount;

          if (rowCount == 1) {
            // Parse and validate the style input
            if (cellData.startsWith("{") && cellData.endsWith("}")) {
              String[] style = cellData.substring(1, cellData.length() - 1).split(":");
              String attribute = style[0].trim();
              String value = style[1].trim();
              // Determine the width of each column and store it
              if ("width".equals(attribute)) {
                // Validate the width style
                if (StringUtils.hasAllowedOnly("0123456789%.", value)) {
                  cStyle.put(cellCount, attribute + ": " + value + ";");
                }
              }
              canOutput = false;
            }
          }

          // Output the header
          if (canOutput) {

            PdfPCell cell = new PdfPCell();
            cell.setPadding(10);
            cell.setBorderColor(new Color(100, 100, 100));
            if ("th".equals(cellType)) {
              cell.setHorizontalAlignment(Element.ALIGN_CENTER);
              cell.setBackgroundColor(new Color(0xC0, 0xC0, 0xC0));
            }
            if (colSpan > 1) {
              cell.setColspan(colSpan);
            }

            // Output the data
            if (" ".equals(cellData) || "¬†".equals(cellData)) {
              // Put a blank space in blank cells for output consistency
              cell.addElement(new Chunk(" "));
              LOG.debug("   OUTPUTTING A BLANK");
            } else {
              // Output the cell as a complete wiki
              float cellWidth = (100.0f / columnCount);
              parseContent(context, wiki, cellData, document, cell, db, wikiListTodo, wikiListDone, cellWidth);
              LOG.debug("   OUTPUTTING CONTENT");
            }
            pdfTable.addCell(cell);
          }
        }
      }
      // read another line to see if it's part of the table
      line = in.readLine();
    }
    if (pdfTable != null) {
      LOG.debug("document.add(pdfTable)");
      document.add(pdfTable);
//          document.add(Chunk.NEWLINE);
    }
    return line;
  }

  protected static String parseForm(WikiPDFContext context, Connection db, BufferedReader in, String line, Document document, PdfPCell cell) throws Exception {
    if (line == null) {
      return line;
    }
    CustomForm form = WikiToHTMLUtils.retrieveForm(in, line);
    LOG.debug("parseForm");
    for (CustomFormGroup group : form) {
      LOG.debug(" group...");
      // Start the table


      PdfPTable pdfTable = new PdfPTable(2);
      pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
      pdfTable.setSpacingBefore(10);
//      pdfTable.setWidthPercentage(100);
      pdfTable.setKeepTogether(true);

      if (group.getDisplay() && StringUtils.hasText(group.getName())) {
        // output the 1st row with a colspan of 2
        if (StringUtils.hasText(group.getName())) {
          Paragraph groupParagraph = new Paragraph(group.getName());
          PdfPCell groupCell = new PdfPCell(groupParagraph);
          groupCell.setHorizontalAlignment(Element.ALIGN_CENTER);
          groupCell.setColspan(2);
          groupCell.setPadding(20);
          groupCell.setBorderColor(new Color(100, 100, 100));
          groupCell.setBackgroundColor(new Color(200, 200, 200));
          groupCell.setNoWrap(true);
          pdfTable.addCell(groupCell);
        }
      }
      for (CustomFormField field : group) {
        LOG.debug("  field...");
        if (field.hasValue()) {
          // output the row (2 columns: label, value)
          Paragraph fieldLabelParagraph = new Paragraph(field.getLabel());
          PdfPCell fieldLabelCell = new PdfPCell(fieldLabelParagraph);
          fieldLabelCell.setPadding(20);
          fieldLabelCell.setBorderColor(new Color(100, 100, 100));
//          fieldLabelCell.setNoWrap(true);
          pdfTable.addCell(fieldLabelCell);

          Paragraph fieldValueParagraph = new Paragraph(getFieldValue(context, field));
          PdfPCell fieldValueCell = new PdfPCell(fieldValueParagraph);
          fieldValueCell.setPadding(20);
          fieldValueCell.setBorderColor(new Color(100, 100, 100));
//          fieldValueCell.setNoWrap(true);
          pdfTable.addCell(fieldValueCell);
        }
      }
      LOG.debug("document.add(pdfTable)");
      document.add(pdfTable);

    }
    return null;
  }

  private static boolean parseLine(WikiPDFContext context, String line, Paragraph main, Connection db, ArrayList<Integer> wikiListTodo, float cellWidth, PdfPCell cell) throws Exception {

    LOG.debug("PARSING LINE: " + line);

    // Context Objects
    Project project = context.getProject();
    WikiExportBean exportBean = context.getExportBean();
    HashMap<String, ImageInfo> imageList = context.getImageList();
    String fileLibrary = context.getFileLibrary();

    boolean needsCRLF = true;
    boolean bold = false;
    boolean italic = false;
    boolean bolditalic = false;
    StringBuffer subject = new StringBuffer();
    StringBuffer data = new StringBuffer();
    int linkL = 0;
    int linkR = 0;
    int attr = 0;
    int underlineAttr = 0;

    // parse characters
    for (int i = 0; i < line.length(); i++) {
      char c1 = line.charAt(i);
      String c = String.valueOf(c1);
      // False attr/links
      if (!"'".equals(c) && attr == 1) {
        data.append("'").append(c);
        attr = 0;
        continue;
      }
      if (!"_".equals(c) && underlineAttr == 1) {
        data.append("_").append(c);
        underlineAttr = 0;
        continue;
      }
      if (!"[".equals(c) && linkL == 1) {
        data.append("[").append(c);
        linkL = 0;
        continue;
      }
      if (!"]".equals(c) && linkR == 1) {
        data.append("]").append(c);
        linkR = 0;
        continue;
      }
      // Links
      if ("[".equals(c)) {
        ++linkL;
        continue;
      }
      if ("]".equals(c)) {
        ++linkR;
        if (linkL == 2 && linkR == 2) {
          LOG.debug("main.add(new Chunk(data.toString()))");
          main.add(new Chunk(data.toString()));
          data.setLength(0);
          // Different type of links...
          String link = subject.toString().trim();
          if (link.startsWith("Image:") || link.startsWith("image:")) {
            // @note From WikiImageLink.java
            String image = link.substring(6);
            String title = null;
            int frame = -1;
            int thumbnail = -1;
            int left = -1;
            int right = -1;
            int center = -1;
            int none = -1;
            int imageLink = -1;
            int alt = -1;
            if (image.indexOf("|") > 0) {
              // the image is first
              image = image.substring(0, image.indexOf("|"));
              // any directives are next
              frame = link.indexOf("|frame");
              thumbnail = link.indexOf("|thumb");
              left = link.indexOf("|left");
              right = link.indexOf("|right");
              center = link.indexOf("|center");
              none = link.indexOf("|none");
              imageLink = link.indexOf("|link=");
              alt = link.indexOf("|alt=");
              // the optional caption is last
              int last = link.lastIndexOf("|");
              if (last > frame &&
                  last > thumbnail &&
                  last > left &&
                  last > right &&
                  last > center &&
                  last > none &&
                  last > imageLink &&
                  last > alt) {
                title = link.substring(last + 1);
              }
            }

            //A picture, including alternate text:
            //[[Image:Wiki.png|The logo for this Wiki]]

            //You can put the image in a frame with a caption:
            //[[Image:Wiki.png|frame|The logo for this Wiki]]

            // Access some image details
            int width = 0;
            int height = 0;
            ImageInfo imageInfo = imageList.get(image + (thumbnail > -1 ? "-TH" : ""));
            if (imageInfo != null) {
              width = imageInfo.getWidth();
              height = imageInfo.getHeight();
            }

            // Alt
            String altText = null;
            if (alt > -1) {
              int startIndex = alt + 4;
              int endIndex = link.indexOf("|", startIndex);
              if (endIndex == -1) {
                endIndex = link.length();
              }
              altText = link.substring(startIndex, endIndex);
            }

            // Looks like the image needs a link (which is always last)
            String url = null;
            if (imageLink > -1) {
              // Get the entered link
              int startIndex = imageLink + 6;
              int endIndex = link.length();
              String href = link.substring(startIndex, endIndex);

              // Treat as a wikiLink to validate and to create a proper url
              WikiLink wikiLink = new WikiLink(project.getId(), (altText != null ? href + " " + altText : href));
              url = wikiLink.getUrl("");
              if (!url.startsWith("http://") && !url.startsWith("https://")) {
                // @todo Use a local link
                // @todo Use an external link
              }
            }

            // Determine if local or external image
            if ((image.startsWith("http://") || image.startsWith("https://"))) {
              // retrieve external image
              String imageUrl = null;
              try {
                URL imageURL = new URL(image);
                imageUrl = image;
              } catch (Exception e) {

              }
            } else {
              // local image
              try {
                // @todo image alignment and links
                Image thisImage = Image.getInstance(getImageFilename(db, fileLibrary, project, image, (thumbnail > -1)));

                LOG.debug("Drawing image for area: " + cellWidth);

                if (cellWidth > 0f) {
                  LOG.debug(" Image is embedded in cell");
                  // Guess the size of the cell
                  float cellPixels = (500f * (cellWidth / 100f));
                  if (cellPixels > 0f && (float) width > cellPixels) {
                    // Shrink image to fit the cell
                    LOG.debug(" Scaling to fit");
                    thisImage.scaleToFit(cellPixels, 500f);
                  } else {
                    // Align image to left instead of scaling it to fit
                    thisImage.setAlignment(Image.LEFT);
                  }
                  LOG.debug("cell.addElement(thisImage)");
                  cell.addElement(thisImage);
                } else {
                  LOG.debug(" Image is inline");
                  if (width > 500) {
                    LOG.debug(" Scaling to fit");
                    thisImage.scaleToFit(500f, 500f);
                  }
                  LOG.debug("main.add(thisImage)");
                  main.add(thisImage);
                }


//                thisImage.setAlignment();
//                thisImage.Alignment = Image.TEXTWRAP | Image.ALIGN_RIGHT;


//                main.add(thisImage);
              } catch (FileNotFoundException fnfe) {
                LOG.warn("WikiPDFUtils-> Image was not found in the FileLibrary (" + getImageFilename(db, fileLibrary, project, image, (thumbnail > -1)) + ")... will continue.");
              }
            }

            /*
            if (frame > -1 || thumbnail > -1) {
              sb.append("</div>");
              sb.append("<div id=\"caption\" style=\"margin-bottom: 5px; margin-left: 5px; margin-right: 5px; text-align: left;\">");
            }
            if (thumbnail > -1) {
              sb.append("<div style=\"float:right\"><a target=\"_blank\" href=\"ProjectManagementWiki.do?command=Img&pid=" + wiki.getProjectId() + "&subject=" + StringUtils.replace(StringUtils.jsEscape(image), "%20", "+") + "\"><img src=\"images/magnify-clip.png\" width=\"15\" height=\"11\" alt=\"Enlarge\" border=\"0\" /></a></div>");
            }
            if (frame > -1 || thumbnail > -1) {
              if (title != null) {
                sb.append(StringUtils.toHtml(title));
              }
              sb.append(
                  "  </div>\n" +
                      "</div>");
            }
            */
            /*
            if (none > -1) {
              sb.append("<br clear=\"all\">");
            }
            */
            if (i + 1 == line.length() && (right > -1 || left > -1) || none > -1) {
              needsCRLF = false;
            }
          } else {
            // This is most likely a Wiki link
            String title = subject.toString().trim();
            if (link.indexOf("|") > 0) {
              link = link.substring(0, link.indexOf("|")).trim();
              title = title.substring(title.indexOf("|") + 1);
            }
            if (link.indexOf("http://") > -1 || link.indexOf("https://") > -1) {
              String label = link;
              if (link.indexOf(" ") > 0) {
                label = link.substring(link.indexOf(" ") + 1);
                link = link.substring(0, link.indexOf(" "));
              }
              Anchor anchor1 = new Anchor(label, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.UNDERLINE, new Color(0, 0, 255)));
              anchor1.setReference(link);
              anchor1.setName("top");
              main.add(anchor1);
            } else {
              // Place a wiki link
              if (exportBean.getFollowLinks()) {
                // See if target link exists before creating a link to it
                int linkedWikiId = -1;
                if (StringUtils.hasText(title) && !title.startsWith("|")) {
                  Wiki subwiki = WikiList.queryBySubject(db, title, project.getId());
                  if (subwiki.getId() > -1) {
                    linkedWikiId = subwiki.getId();
                  }
                }
                // Display the linked item
                if (linkedWikiId > -1) {
                  // Display as an anchor
                  Anchor linkToWiki = new Anchor(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255)));
                  linkToWiki.setReference("#" + title.toLowerCase());
                  LOG.debug("Link to: #" + title.toLowerCase());
                  main.add(linkToWiki);
                  LOG.debug(" main.add(linkToWiki)");
                  //                  main.add(new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))).setLocalGoto(link));
                  // Add this wiki to the to do list...
                  if (!wikiListTodo.contains(linkedWikiId)) {
                    wikiListTodo.add(linkedWikiId);
                  }
                } else {
                  // Display without the link
                  main.add(new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))));
                }
              } else {
                // Not following links, so display... perhaps as an external link someday
                main.add(new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))));
              }
            }
          }
          subject.setLength(0);
          linkL = 0;
          linkR = 0;
        }
        continue;
      }
      if (!"[".equals(c) && linkL == 2 && !"]".equals(c)) {
        subject.append(c);
        continue;
      }
      // Attribute properties
      if ("'".equals(c)) {
        ++attr;
        continue;
      }
      if (!"'".equals(c) && attr > 1) {
        if (attr == 2) {
          if (!italic) {
            main.add(new Chunk(data.toString()));
            data.setLength(0);
            data.append(c);
            italic = true;
          } else {
            data.append(c);
            main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC, new Color(0, 0, 0))));
            data.setLength(0);
            italic = false;
          }
          attr = 0;
          continue;
        }
        if (attr == 3) {
          if (!bold) {
            main.add(new Chunk(data.toString()));
            data.setLength(0);
            data.append(c);
            bold = true;
          } else {
            data.append(c);
            main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new Color(0, 0, 0))));
            data.setLength(0);
            bold = false;
          }
          attr = 0;
          continue;
        }
        if (attr == 5) {
          if (!bolditalic) {
            main.add(new Chunk(data.toString()));
            data.setLength(0);
            data.append(c);
            bolditalic = true;
          } else {
            data.append(c);
            main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLDITALIC, new Color(0, 0, 0))));
            data.setLength(0);
            bolditalic = false;
          }
          attr = 0;
          continue;
        }
      }
      data.append(c);
    }
    for (int x = 0; x < linkR; x++) {
      data.append("]");
    }
    for (int x = 0; x < linkL; x++) {
      data.append("[");
    }
    if (attr == 1) {
      data.append("'");
    }
    if (italic) {
      main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC, new Color(0, 0, 0))));
    } else if (bold) {
      main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, new Color(0, 0, 0))));
    } else if (bolditalic) {
      main.add(new Chunk(data.toString(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLDITALIC, new Color(0, 0, 0))));
    } else {
      main.add(new Chunk(data.toString()));
    }
    data.setLength(0);
    return needsCRLF;
  }

  public static String getFieldValue(WikiPDFContext context, CustomFormField field) {
    // Return output based on type
    switch (field.getType()) {
      case CustomFormField.TEXTAREA:
        return StringUtils.replace(field.getValue(), "^", CRLF);
      case CustomFormField.SELECT:
        return field.getValue();
      case CustomFormField.CHECKBOX:
        if ("true".equals(field.getValue())) {
          return "Yes";
        } else {
          return "No";
        }
      case CustomFormField.CALENDAR:
        String calendarValue = field.getValue();
        if (StringUtils.hasText(calendarValue)) {
          try {
            String convertedDate = DateUtils.getUserToServerDateTimeString(null, DateFormat.SHORT, DateFormat.LONG, field.getValue());
            Timestamp timestamp = DatabaseUtils.parseTimestamp(convertedDate);
            Locale locale = Locale.getDefault();
            int dateFormat = DateFormat.SHORT;
            SimpleDateFormat dateFormatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(dateFormat, locale);
            calendarValue = dateFormatter.format(timestamp);
          } catch (Exception e) {
            LOG.error(e);
          }
        }
        return calendarValue;
      case CustomFormField.PERCENT:
        return field.getValue() + "%";
      case CustomFormField.INTEGER:
        // Determine the value to display in the field
        String integerValue = StringUtils.toHtmlValue(field.getValue());
        if (StringUtils.hasText(integerValue)) {
          try {
            NumberFormat formatter = NumberFormat.getInstance();
            integerValue = formatter.format(StringUtils.getIntegerNumber(field.getValue()));
          } catch (Exception e) {
            LOG.warn("Could not integer format: " + field.getValue());
          }
        }
        return integerValue;
      case CustomFormField.FLOAT:
        // Determine the value to display in the field
        String decimalValue = field.getValue();
        if (StringUtils.hasText(decimalValue)) {
          try {
            NumberFormat formatter = NumberFormat.getInstance();
            decimalValue = formatter.format(StringUtils.getDoubleNumber(field.getValue()));
          } catch (Exception e) {
            LOG.warn("Could not decimal format: " + field.getValue());
          }
        }
        return decimalValue;
      case CustomFormField.CURRENCY:
        // Use a currency for formatting
        String currencyCode = field.getValueCurrency();
        if (currencyCode == null) {
          currencyCode = field.getCurrency();
        }
        if (!StringUtils.hasText(currencyCode)) {
          currencyCode = "USD";
        }
        try {
          NumberFormat formatter = NumberFormat.getCurrencyInstance();
          if (currencyCode != null) {
            Currency currency = Currency.getInstance(currencyCode);
            formatter.setCurrency(currency);
          }
          return (formatter.format(StringUtils.getDoubleNumber(field.getValue())));
        } catch (Exception e) {
          LOG.error(e.getMessage());
        }
        return field.getValue();
      case CustomFormField.EMAIL:
        return field.getValue();
      case CustomFormField.PHONE:
        PhoneNumberBean phone = new PhoneNumberBean();
        phone.setNumber(field.getValue());
        PhoneNumberUtils.format(phone, Locale.getDefault());
        return phone.toString();
      case CustomFormField.URL:
        String value = StringUtils.toHtmlValue(field.getValue());
        if (StringUtils.hasText(value)) {
          if (!value.contains("://")) {
            value = "http://" + value;
          }
          if (value.contains("://")) {
            return value;
          }
        }
        return value;
      case CustomFormField.IMAGE:
        String image = field.getValue();
        if (StringUtils.hasText(image)) {
          return "WikiImage:" + image;
        }
        return image;
      default:
        return field.getValue();
    }
  }

  private static String getImageFilename(Connection db, String fileLibrary, Project project, String subject, boolean showThumbnail) throws SQLException {
    // Load the file for download
    FileItemList fileItemList = new FileItemList();
    fileItemList.setLinkModuleId(Constants.PROJECT_WIKI_FILES);
    fileItemList.setLinkItemId(project.getId());
    fileItemList.setFilename(subject);
    fileItemList.buildList(db);
    FileItem fileItem = null;
    if (fileItemList.size() > 0) {
      fileItem = (FileItem) fileItemList.get(0);
      return fileLibrary + "1" + fs + "projects" + fs + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
    }
    return null;
  }

  protected static String getDatePath(java.sql.Timestamp fileDate) {
    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy");
    String datePathToUse1 = formatter1.format(fileDate);
    SimpleDateFormat formatter2 = new SimpleDateFormat("MMdd");
    String datePathToUse2 = formatter2.format(fileDate);
    return datePathToUse1 + fs + datePathToUse2 + fs;
  }

}
