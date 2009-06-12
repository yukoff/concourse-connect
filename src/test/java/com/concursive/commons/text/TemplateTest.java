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

package com.concursive.commons.text;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Description
 *
 * @author matt rajkowski
 * @created Jun 12, 2009 2:30:15 PM
 */
public class TemplateTest extends TestCase {

  String TEMPLATE =
      "<table id=\"Table_01\" width=\"800\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto; border:1px solid #999;\" bgcolor=\"ffffff\">\n" +
          "                  <tr>\n" +
          "                    <td bgcolor=\"ffffff\" style=\"font-family:Arial, Helvetica, sans-serif; padding:10px\">\n" +
          "                      ${this.title?html} (${this.category.description?html})<br />\n" +
          "                      <!--<#if (this.owner > -1)>\n" +
          "                        ** The user claimed this listing\n" +
          "                      </#if>\n" +
          "                      -->\n" +
          "                      Claim status: ${this.owner}<br />\n" +
          "                      <a href=\"${secureUrl}/show/${this.uniqueId?html}\" target=\"_blank\">${secureUrl}/show/${this.uniqueId?html}</a><br />\n" +
          "                      <br />\n" +
          "                      Created by ${userInfo.nameFirstLast?html}<br />\n" +
          "                      <!--<#if userInfo.company?has_content>-->\n" +
          "                        ${userInfo.company?html}<br />\n" +
          "                      <!--</#if>-->\n" +
          "                      ${userInfo.email}<br />\n" +
          "                      <!--<#if userInfo.profileProject.location?has_content>-->\n" +
          "                        ${userInfo.profileProject.location?html}\n" +
          "                      <!--</#if>-->\n" +
          "                      <a href=\"${secureUrl}/show/${userInfo.profileProject.uniqueId?html}\" target=\"_blank\">${secureUrl}/show/${userInfo.profileProject.uniqueId?html}</a><br />\n" +
          "                    </td>\n" +
          "                  </tr>\n" +
          "                </table>";

  String RESULT =
      "<table id=\"Table_01\" width=\"800\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto; border:1px solid #999;\" bgcolor=\"ffffff\">\n" +
          "                  <tr>\n" +
          "                    <td bgcolor=\"ffffff\" style=\"font-family:Arial, Helvetica, sans-serif; padding:10px\">\n" +
          "                      New Listing (Businesses)<br />\n" +
          "                      <!--<#if (this.owner > -1)>\n" +
          "                        ** The user claimed this listing\n" +
          "                      </#if>\n" +
          "                      -->\n" +
          "                      Claim status: -1<br />\n" +
          "                      <a href=\"http://127.0.0.1:8080/connect/show/new-listing\" target=\"_blank\">http://127.0.0.1:8080/connect/show/new-listing</a><br />\n" +
          "                      <br />\n" +
          "                      Created by John Example<br />\n" +
          "                      <!--<#if userInfo.company?has_content>-->\n" +
          "                        None<br />\n" +
          "                      <!--</#if>-->\n" +
          "                      john@example.com<br />\n" +
          "                      <!--<#if userInfo.profileProject.location?has_content>-->\n" +
          "                        \n" +
          "                      <!--</#if>-->\n" +
          "                      <a href=\"http://127.0.0.1:8080/connect/show/john-example\" target=\"_blank\">http://127.0.0.1:8080/connect/show/john-example</a><br />\n" +
          "                    </td>\n" +
          "                  </tr>\n" +
          "                </table>";

  public void testTemplate() {

    Template template = new Template();
    template.setText(TEMPLATE);

    ArrayList templateVariables = template.getVariables();
    for (Object templateVariable : templateVariables) {
      String variable = (String) templateVariable;
    }

    assertTrue(templateVariables.contains("secureUrl"));
    template.addParseElement("${secureUrl}", "http://127.0.0.1:8080/connect");

    assertTrue(templateVariables.contains("userInfo.profileProject.uniqueId?html"));
    template.addParseElement("${userInfo.profileProject.uniqueId?html}", "john-example");

    assertTrue(templateVariables.contains("userInfo.nameFirstLast?html"));
    template.addParseElement("${userInfo.nameFirstLast?html}", "John Example");

    assertTrue(templateVariables.contains("userInfo.company?html"));
    template.addParseElement("${userInfo.company?html}", "None");

    assertTrue(templateVariables.contains("userInfo.email"));
    template.addParseElement("${userInfo.email}", "john@example.com");

    assertTrue(templateVariables.contains("userInfo.profileProject.location?html"));
    template.addParseElement("${userInfo.profileProject.location?html}", "");

    assertTrue(templateVariables.contains("this.uniqueId?html"));
    template.addParseElement("${this.uniqueId?html}", "new-listing");

    assertFalse(templateVariables.contains("this.title"));
    assertTrue(templateVariables.contains("this.title?html"));
    template.addParseElement("${this.title?html}", "New Listing");

    assertTrue(templateVariables.contains("this.category.description?html"));
    template.addParseElement("${this.category.description?html}", "Businesses");

    assertTrue(templateVariables.contains("this.owner"));
    template.addParseElement("${this.owner}", "-1");


    String parsed = template.getParsedText();
    assertEquals(RESULT, parsed);
  }
}
