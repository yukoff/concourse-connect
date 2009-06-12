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

package com.concursive.commons.phone;

import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Tests formatting phone numbers
 *
 * @author matt rajkowski
 * @created Sep 2, 2008
 */
public class PhoneNumberUtilsTest extends TestCase {

  public void testFormatNumberWithUSLocale() throws SQLException {
    ArrayList<String> variants = new ArrayList<String>();
    variants.add("800.555.1212");
    variants.add("800-555-1212");
    variants.add("(800) 555-1212");
    variants.add("8005551212");
    variants.add("18005551212");
    for (String number : variants) {
      PhoneNumberBean phone = PhoneNumberUtils.format(number, null);
      assertEquals("(800) 555-1212", phone.getNumber());
      assertEquals("(800) 555-1212", phone.toString());
      assertNull(phone.getExtension());
    }
  }

  public void testFormatAlphaNumberWithUSLocale() throws SQLException {
    ArrayList<String> variants = new ArrayList<String>();
    variants.add("800.KL5.1212");
    variants.add("800-KL5-1212");
    variants.add("(800) KL5-1212");
    variants.add("800KL51212");
    variants.add("1800KL51212");
    for (String number : variants) {
      PhoneNumberBean phone = PhoneNumberUtils.format(number, null);
      assertEquals(number, phone.getNumber());
      assertEquals(number, phone.toString());
      assertNull(phone.getExtension());
    }
  }

  public void testFormatNumberAndExtensionWithUSLocale() throws SQLException {
    ArrayList<String> variants = new ArrayList<String>();
    variants.add("800.555.1212x35");
    variants.add("800-555-1212x35");
    variants.add("(800) 555-1212 ext. 35");
    variants.add("8005551212 ext. 35");
    variants.add("18005551212 ext. 35");
    for (String number : variants) {
      PhoneNumberBean phone = PhoneNumberUtils.format(number, null);
      assertEquals("(800) 555-1212", phone.getNumber());
      assertEquals("35", phone.getExtension());
      assertEquals("(800) 555-1212 ext. 35", phone.toString());
    }
  }

  public void testFormatForeignNumberWithUSLocale() throws SQLException {
    ArrayList<String> variants = new ArrayList<String>();
    variants.add("+44 444 444 4444");
    variants.add("44 444 444 4444");
    for (String number : variants) {
      PhoneNumberBean phone = PhoneNumberUtils.format(number, null);
      assertEquals(number, phone.getNumber());
      assertEquals(number, phone.toString());
      assertNull(phone.getExtension());
    }
  }

}