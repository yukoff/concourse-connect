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

import com.concursive.connect.Constants;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.documents.dao.FileItemList;
import com.concursive.connect.web.modules.documents.dao.ImageInfo;
import com.concursive.connect.web.modules.profile.dao.Project;
import com.concursive.connect.web.modules.wiki.beans.WikiExportBean;
import com.concursive.connect.web.modules.wiki.dao.Wiki;
import com.concursive.connect.web.modules.wiki.dao.WikiList;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class to manipulate wiki objects
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 26, 2006
 */
public class WikiPDFUtils {

  public final static String fs = System.getProperty("file.separator");
  // Define styles
  public final static Font titleFont = FontFactory.getFont(FontFactory.TIMES, 24, Font.NORMAL, new Color(0, 0, 0));
  public final static Font titleSmallFont = FontFactory.getFont(FontFactory.TIMES, 12, Font.NORMAL, new Color(0, 0, 0));
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

  public static boolean exportToFile(Project project, Wiki wiki, File file, HashMap imageList, Connection db, String fileLibrary, WikiExportBean exportBean) throws Exception {
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
      HeaderFooter pageFooter = new HeaderFooter(new Phrase(project.getTitle() + ": " + wiki.getSubject() + " - page "), new Phrase(""));
      pageFooter.setAlignment(Element.ALIGN_CENTER);
      document.setFooter(pageFooter);
    }

    document.open();

    if (exportBean.getIncludeTitle()) {
      //HeaderFooter pageHeader = new HeaderFooter(new Phrase(project.getTitle()), false);
      //document.setHeader(pageHeader);
      HeaderFooter pageFooter = new HeaderFooter(new Phrase(project.getTitle() + ": " + wiki.getSubject() + " - page "), new Phrase(""));
      pageFooter.setAlignment(Element.ALIGN_CENTER);
      document.setFooter(pageFooter);

      // Draw a title page
      Rectangle rectangle = new Rectangle(600, 30);
      rectangle.setBackgroundColor(new Color(100, 100, 100));
      document.add(rectangle);

      document.add(new Paragraph(project.getTitle(), titleFont));
      if (!"".equals(wiki.getSubject())) {
        document.add(new Paragraph(wiki.getSubject(), titleFont));
      }
      document.add(new Paragraph("\r\nLast Modified: " + wiki.getModified(), titleSmallFont));
      document.newPage();
    }


    ArrayList wikiListDone = new ArrayList();
    wikiListDone.add(wiki.getSubject());

    appendWiki(project, wiki, document, db, fileLibrary, imageList, wikiListDone, exportBean);
    // Close everything
    document.close();
    return true;
  }

  private static void appendWiki(Project project, Wiki wiki, Document document, Connection db, String fileLibrary, HashMap imageList, ArrayList wikiListDone, WikiExportBean exportBean) throws SQLException {

    // Parse the wiki pages
    int lastIndent = 0;
    boolean pre = false;
    boolean code = false;
    boolean header = true;
    boolean table = false;
    int row = 1;
    ArrayList wikiListTodo = new ArrayList();

    try {
//      Paragraph title = new Paragraph(wiki.getSubject(), sectionFont);

      Chunk wikiTitle = new Chunk(wiki.getSubject());
      wikiTitle.setBackground(new Color(0xFF, 0xDE, 0xAD));
      //title.setUnderline(new Color(0xFF, 0x00, 0x00), 3.0f, 0.0f, -4.0f, 0.0f, PdfContentByte.LINE_CAP_ROUND);
      wikiTitle.setUnderline(0.2f, -2f);
      document.add(wikiTitle);


      BufferedReader in = new BufferedReader(new StringReader(wiki.getContent()));
      String line = null;
      PdfPTable pdfTable = null;
      ArrayList unorderedParents = null;
      List thisList = null;
      Paragraph codeParagraph = null;
      while ((line = in.readLine()) != null) {
        // Handle code blocks
        if (line.startsWith("<code>") || line.startsWith(" ")) {
          if (!code && !pre) {
            if (line.startsWith("<code>")) {
              code = true;
            }
            if (line.startsWith(" ")) {
              pre = true;
            }
            codeParagraph = new Paragraph("", codeFont);
            codeParagraph.setSpacingBefore(10);
            continue;
          }
        }
        if (line.startsWith("</code>")) {
          if (code) {
            code = false;
            PdfPTable codeTable = new PdfPTable(1);
            codeTable.setWidthPercentage(100);
            codeTable.setSpacingBefore(10);
            PdfPCell codeCell = new PdfPCell(codeParagraph);
            codeCell.setPadding(20);
            codeCell.setBorderColor(new Color(100, 100, 100));
            codeCell.setBackgroundColor(new Color(200, 200, 200));
            codeCell.setNoWrap(true);
            codeTable.addCell(codeCell);
            document.add(codeTable);
            continue;
          }
        }
        if (code) {
          // Append the chunk
          codeParagraph.add(new Chunk(line + "\r\n"));
          continue;
        }
        if (pre) {
          if (line.startsWith(" ")) {
            // Append the chunk
            codeParagraph.add(new Chunk(line + "\r\n"));
            continue;
          } else {
            pre = false;
            PdfPTable codeTable = new PdfPTable(1);
            codeTable.setWidthPercentage(100);
            codeTable.setSpacingBefore(10);
            PdfPCell codeCell = new PdfPCell(codeParagraph);
            codeCell.setPadding(20);
            codeCell.setBorderColor(new Color(100, 100, 100));
            codeCell.setBackgroundColor(new Color(200, 200, 200));
            codeCell.setNoWrap(true);
            codeTable.addCell(codeCell);
            document.add(codeTable);
          }
        }

        // Determine if this is a bulleted list
        if (line.startsWith("*") || line.startsWith("#")) {
          // Initialize the list array
          if (unorderedParents == null) {
            unorderedParents = new ArrayList();
            document.add(new Paragraph("\r\n"));
          }
          // Get the indent level
          boolean ol = line.startsWith("#");
          int hCount = WikiPDFUtils.parseHCount(line, ol ? "#" : "*");
          // Determine a shift in the tree
          if (lastIndent == 0) {
            thisList = new List(ol, 20);
            unorderedParents.add(thisList);
          } else {
            if (hCount > lastIndent) {
              thisList = new List(ol, 20);
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
          WikiPDFUtils.parseLine(wiki, line.substring(hCount), thisItem, imageList, project, db, fileLibrary, wikiListTodo, wikiListDone, exportBean);
          thisList.add(new ListItem(thisItem));
          continue;
        }
        // List is finished, so append it to the document before working on
        // other paragraphs
        if (unorderedParents != null) {
          document.add((List) unorderedParents.get(0));
          unorderedParents = null;
          thisList = null;
          lastIndent = 0;
          Chunk space = new Chunk("\r\n");
          document.add(space);
        }

        // Determine if this is a section header
        if (line.startsWith("==") && line.endsWith("==")) {
          int hCount = WikiPDFUtils.parseHCount(line, "=");
          String section = line.substring(line.indexOf("==") + hCount, line.lastIndexOf("==") - hCount + 2);
          header = true;
          if (hCount == 3) {
            Paragraph title = new Paragraph(section.trim(), section2Font);
            title.setSpacingBefore(10);
            document.add(title);
          } else if (hCount > 3) {
            Paragraph title = new Paragraph(section.trim(), section3Font);
            title.setSpacingBefore(10);
            document.add(title);
          } else {
            Paragraph title = new Paragraph(section.trim(), sectionFont);
            title.setSpacingBefore(10);
            document.add(title);
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
        // Determine if this is a table...
        // ||header col1||header col2||
        // |colA1|colA2|
        // |colB1|colB2|
        if (line.startsWith("|") && line.endsWith("|")) {
          if (!table) {
            int columnCount = 1;
            if (line.startsWith("||") && line.endsWith("||")) {
              String[] sp = line.substring(2, line.length() - 2).split("[|][|]");
              columnCount = sp.length;
            } else {
              // TODO: determine columns when no header column is used

            }
            pdfTable = new PdfPTable(columnCount);
            //pdfTable.setWidthPercentage(100);
            pdfTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            pdfTable.setSpacingBefore(10);
            row = 0;
            table = true;
          }
        } else {
          if (table) {
            document.add(pdfTable);
            // TODO: Add a space after the table
            //sb.append("\r\n");
            table = false;
          }
        }
        if (line.startsWith("||") && line.endsWith("||")) {
          int colSpan = 1;
          String[] sp = line.substring(2, line.length() - 2).split("[|][|]");
          for (int x = 0; x < sp.length; x++) {
            String token = sp[x];
            if (token.length() == 0) {
              ++colSpan;
              continue;
            }
            Paragraph paragraph = new Paragraph();
            paragraph.setSpacingBefore(10);
            WikiPDFUtils.parseLine(wiki, token, paragraph, imageList, project, db, fileLibrary, wikiListTodo, wikiListDone, exportBean);
            PdfPCell cell = new PdfPCell(paragraph);
            cell.setBackgroundColor(new Color(0xC0, 0xC0, 0xC0));
            if (colSpan > 1) {
              cell.setColspan(colSpan);
            }
            cell.setExtraParagraphSpace(10);
            pdfTable.addCell(cell);
          }
          continue;
        } else if (line.startsWith("|") && line.endsWith("|")) {
          row = (row != 1 ? 1 : 2);
          int colSpan = 1;

          // Get a String array of the table cells, | can be used within [[]]
          ArrayList cells = new ArrayList();
          int cc = 0;
          StringBuffer thisCell = new StringBuffer();
          boolean inLink = false;
          while (cc < line.length()) {
            char thisCC = line.charAt(cc);
            if (thisCC == '|' && !inLink) {
              if (thisCell.length() > 0) {
                cells.add(thisCell.toString());
                thisCell.setLength(0);
              }
              cc++;
              continue;
            }
            if (thisCC == '[') {
              inLink = true;
            }
            if (thisCC == ']') {
              inLink = false;
            }
            thisCell.append(thisCC);
            cc++;
          }
          cells.add(thisCell.toString());
          Iterator cellIterator = cells.iterator();
          while (cellIterator.hasNext()) {
            String thisCellString = (String) cellIterator.next();
            if (thisCellString.length() == 0) {
              ++colSpan;
              continue;
            }
            Paragraph paragraph = new Paragraph();
            paragraph.setSpacingBefore(10);
            WikiPDFUtils.parseLine(wiki, thisCellString.trim(), paragraph, imageList, project, db, fileLibrary, wikiListTodo, wikiListDone, exportBean);
            PdfPCell cell = new PdfPCell(paragraph);
            cell.setExtraParagraphSpace(10);
            if (colSpan > 1) {
              cell.setColspan(colSpan);
            }
            pdfTable.addCell(cell);
          }
          continue;
        }

        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(10);
        WikiPDFUtils.parseLine(wiki, line, paragraph, imageList, project, db, fileLibrary, wikiListTodo, wikiListDone, exportBean);
        document.add(paragraph);
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
        codeCell.setNoWrap(true);
        codeTable.addCell(codeCell);
        document.add(codeTable);
      }
      if (table) {
        document.add(pdfTable);
      }
      if (unorderedParents != null) {
        document.add((List) unorderedParents.get(0));
        Chunk space = new Chunk("\r\n");
        document.add(space);
      }
      in.close();
    } catch (Exception e) {
      e.printStackTrace(System.out);
    }
    wikiListDone.add(wiki.getSubject());
    if (exportBean.getFollowLinks() && wikiListTodo.size() > 0) {
      Iterator i = wikiListTodo.iterator();
      while (i.hasNext()) {
        String thisSubject = (String) i.next();
        Wiki subwiki = WikiList.queryBySubject(db, thisSubject, project.getId());
        if (subwiki.getId() > -1) {
          appendWiki(project, subwiki, document, db, fileLibrary, imageList, wikiListDone, exportBean);
        }
      }
    }
  }

  private static int parseHCount(String line, String id) {
    int count = 0;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (id.equals(String.valueOf(c))) {
        ++count;
      } else {
        return count;
      }
    }
    return 2;
  }

  private static boolean parseLine(Wiki wiki, String line, Paragraph main, HashMap imageList, Project project, Connection db, String fileLibrary, ArrayList wikiListTodo, ArrayList wikiListDone, WikiExportBean exportBean) throws Exception {
    boolean needsCRLF = true;
    boolean bold = false;
    boolean italic = false;
    boolean bolditalic = false;
    StringBuffer subject = new StringBuffer();
    StringBuffer data = new StringBuffer();
    int linkL = 0;
    int linkR = 0;
    int attr = 0;

    // parse characters
    for (int i = 0; i < line.length(); i++) {
      char c1 = line.charAt(i);
      String c = String.valueOf(c1);
      // False attr/links
      if (!"'".equals(c) && attr == 1) {
        data.append("'" + c);
        attr = 0;
        continue;
      }
      if (!"[".equals(c) && linkL == 1) {
        data.append("[" + c);
        linkL = 0;
        continue;
      }
      if (!"]".equals(c) && linkR == 1) {
        data.append("]" + c);
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
          main.add(new Chunk(data.toString()));
          data.setLength(0);
          // Different type of links...
          String link = subject.toString().trim();
          if (link.startsWith("Image:") || link.startsWith("image:")) {
            String image = link.substring(6);
            String title = null;
            int frame = -1;
            int thumbnail = -1;
            int left = -1;
            int right = -1;
            int none = -1;
            if (image.indexOf("|") > 0) {
              // the image is first
              image = image.substring(0, image.indexOf("|"));
              // any directives are next
              frame = link.indexOf("|frame");
              thumbnail = link.indexOf("|thumb");
              left = link.indexOf("|left");
              right = link.indexOf("|right");
              none = link.indexOf("|none");
              // the optional caption is last
              int last = link.lastIndexOf("|");
              if (last > frame &&
                  last > thumbnail &&
                  last > left &&
                  last > right &&
                  last > none) {
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
            ImageInfo imageInfo = (ImageInfo) imageList.get(image + (thumbnail > -1 ? "-TH" : ""));
            if (imageInfo != null) {
              width = imageInfo.getWidth();
              height = imageInfo.getHeight();
            }

            /*
            if (frame > -1 || thumbnail > -1) {
              // Width = the image width + border size * 2 + margin * 2 of inner div
              // Output the frame
              sb.append(
                  "<div style=\"" +
                      (width > 0 ? "width: " + (width + (1 * 2) + (5 * 2)) + "; " : "") +
                      (right > -1 ? "float: right; clear:right; " : "") +
                      (left > -1 ? "float: left; clear:left; " : "") +
                      "position:relative; border: 1px solid #999999; margin-bottom: 5px; \">\n" +
                      "  <div style=\"border: 1px solid #999999; margin: 5px;\">");
            }
            */

            try {
              Image thisImage = Image.getInstance(getImageFilename(db, fileLibrary, project, image, (thumbnail > -1)));
              main.add(thisImage);
            } catch (FileNotFoundException fnfe) {
              if (System.getProperty("DEBUG") != null) {
                System.out.println("WikiPDFUtils-> Image was not found in the FileLibrary (" + getImageFilename(db, fileLibrary, project, image, (thumbnail > -1)) + ")... will continue.");
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
                main.add(new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))).setLocalDestination(link));
              } else {
                main.add(new Chunk(title, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, new Color(0, 0, 255))));
              }
              if (!wikiListDone.contains(link)) {
                wikiListTodo.add(link);
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
      String filePath = fileLibrary + fs + "1" + fs + "projects" + fs + getDatePath(fileItem.getModified()) + (showThumbnail ? fileItem.getThumbnailFilename() : fileItem.getFilename());
      return filePath;
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
