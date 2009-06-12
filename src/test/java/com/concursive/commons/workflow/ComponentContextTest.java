package com.concursive.commons.workflow;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.profile.dao.Project;
import junit.framework.TestCase;

/**
 * Description
 *
 * @author matt rajkowski
 * @created Jun 12, 2009 2:45:19 PM
 */
public class ComponentContextTest extends TestCase {

  String TEMPLATE =
      "<table id=\"Table_01\" width=\"800\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto; border:1px solid #999;\" bgcolor=\"ffffff\">\n" +
          "                  <tr>\n" +
          "                    <td bgcolor=\"ffffff\" style=\"font-family:Arial, Helvetica, sans-serif; padding:10px\">\n" +
          "                      ${this.title?html}<br />\n" +
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
          "                    </td>\n" +
          "                  </tr>\n" +
          "                </table>";

  String RESULT =
      "<table id=\"Table_01\" width=\"800\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 auto; border:1px solid #999;\" bgcolor=\"ffffff\">\n" +
          "                  <tr>\n" +
          "                    <td bgcolor=\"ffffff\" style=\"font-family:Arial, Helvetica, sans-serif; padding:10px\">\n" +
          "                      New Listing<br />\n" +
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
          "                    </td>\n" +
          "                  </tr>\n" +
          "                </table>";

  public void testComponentContextTemplate() {

    // Provide a listing
    Project thisListing = new Project();
    thisListing.setUniqueId("new-listing");
    thisListing.setTitle("New Listing");
    thisListing.setOwner(-1);

    // Provide a user
    User user = new User();
    user.setCompany("None");
    user.setEmail("john@example.com");
    user.setFirstName("John");
    user.setLastName("Example");

    // Construct the context
    ComponentContext context = new ComponentContext();
    context.setThisObject(thisListing);
    context.setAttribute("userInfo", user);
    context.setParameter("notification.body", TEMPLATE);
    context.setParameter("secureUrl", "http://127.0.0.1:8080/connect");

    // Return the parsed parameter
    String parsed = context.getParameter("notification.body");
    assertEquals(RESULT, parsed);
  }

}
