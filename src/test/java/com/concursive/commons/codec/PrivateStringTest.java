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
package com.concursive.commons.codec;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Hex;

import java.security.Key;

import com.concursive.commons.codec.PrivateString;


/**
 * Tests html utils functions
 *
 * @author matt rajkowski
 * @created August 7, 2008
 */
public class PrivateStringTest extends TestCase {

  protected String plainText = "hello";

  public void testNewKey() throws Exception {
    // Generate a key
    Key key = PrivateString.generateKey();
    assertEquals("AES", key.getAlgorithm());

    // Hex encode the key for portability
    String hexEncodedKey = new String(Hex.encodeHex(key.getEncoded()));
    assertNotNull("hexEncodedKey", hexEncodedKey);

    // Encrypt some text
    String encryptedText = PrivateString.encrypt(key, plainText);
    assertNotNull("encryptedText", encryptedText);

    // Decrypt the text for comparison
    String decryptedText = PrivateString.decrypt(key, encryptedText);
    assertEquals(plainText, decryptedText);

    // Decode the key for re-use and make sure it's the same
    Key hexDecodedKey = PrivateString.decodeHex(hexEncodedKey, key.getAlgorithm());
    assertEquals(key, hexDecodedKey);

    // Decode the encrypted text for comparison
    String hexDecryptedText = PrivateString.decrypt(hexDecodedKey, encryptedText);
    assertEquals(plainText, hexDecryptedText);

    // Compare the original key and the hex encoded key
    String hexEncryptedText = PrivateString.encrypt(hexDecodedKey, hexDecryptedText);
    assertEquals(encryptedText, hexEncryptedText);
  }

  public void testDESKey() throws Exception {
    // Decode the key for re-use
    String hexEncodedDESKey = "ef80859b62e03475";
    Key hexDecodedKey = PrivateString.decodeHex(hexEncodedDESKey, "DES");
    assertNotNull("hexDecodedKey", hexDecodedKey);

    // Decode the encrypted text for comparison
    String hexDecryptedText = PrivateString.decrypt(hexDecodedKey, "iIksWx4npvQ=");
    assertEquals(plainText, hexDecryptedText);
  }
}
