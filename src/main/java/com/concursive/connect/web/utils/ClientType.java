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

package com.concursive.connect.web.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Utility to determine the client's browser type.<br>
 * <br>
 * Can be used for CSS or browser specific features.
 *
 * @author Matt Rajkowski
 * @version $Id$
 * @created March 5, 2002
 */
public class ClientType implements Serializable {

  private static Log LOG = LogFactory.getLog(ClientType.class);

  public final static String allowed = "0123456789.";

  // Client Browser Products, also define the text below
  public final static int NETSCAPE = 1;
  public final static int IE = 2;
  public final static int POCKETIE = 3;
  public final static int OPERA = 4;
  public final static int MOZILLA = 5;
  public final static int APPLEWEBKIT = 6;
  // Client Browser Types
  public final static int HTML_BROWSER = 1;
  public final static int WAP_BROWSER = 2;
  // Client OS
  public final static int WINDOWS = 1;
  public final static int MAC = 2;
  public final static int LINUX = 3;
  // Variables
  private int type = -1;
  private int id = -1;
  private double version = -1;
  private int os = WINDOWS;
  private String language = "en_US";
  private String referer = null;
  private boolean mobileFound = false;
  private boolean mobile = false;
  private boolean bot = false;

  final static long serialVersionUID = 8345658414124283570L;

  /**
   * Constructor for the ClientType object
   */
  public ClientType() {
  }


  /**
   * Constructor for the ClientType object
   *
   * @param request Description of the Parameter
   */
  public ClientType(HttpServletRequest request) {
    this.setParameters(request);
  }


  /**
   * Sets the parameters attribute of the ClientType object
   *
   * @param request The new parameters value
   */
  public void setParameters(HttpServletRequest request) {
    this.type = HTML_BROWSER;
    String wapCheck = request.getHeader("x-up-subno");
    if (wapCheck != null) {
      LOG.trace("WAP String: " + wapCheck);
    }
    if (wapCheck != null) {
      this.type = WAP_BROWSER;
    }

    String header = request.getHeader("USER-AGENT");
    if (header == null) {
      header = request.getHeader("User-Agent");
    }
    if (header == null) {
      header = request.getHeader("user-agent");
    }

    if (header != null) {
      header = header.toLowerCase();
      LOG.trace("Client browser header string: " + header);
      // Determine OS
      if (header.indexOf("linux") > -1) {
        os = LINUX;
      } else if (header.indexOf("mac_powerpc") > -1) {
        os = MAC;
      } else if (header.indexOf("macintosh") > -1) {
        os = MAC;
      }

      if (header.indexOf("msie") > -1) {
        //User-Agent: mozilla/4.0 (compatible; msie 6.0; windows 98; .net clr 1.0.3705)
        //User-Agent: mozilla/4.0 (compatible; msie 5.01; windows nt 5.0)
        this.id = IE;
        //Search for "msie x"
        version = parseVersion(header.substring(header.indexOf("msie ") + 5,
            header.indexOf(";", header.indexOf("msie "))));
      } else if (header.indexOf("applewebkit") > -1) {
        //User-Agent: mozilla/5.0 (macintosh; u; ppc mac os x; en) applewebkit/125.2 (khtml, like gecko) safari/125.7
        //mozilla/5.0 (macintosh; u; ppc mac os x; en) applewebkit/125.2 (khtml, like gecko) safari/125.8
        this.id = APPLEWEBKIT;
        version = parseVersion(header.substring(header.indexOf("applewebkit") + 12, header.indexOf("(khtml")));
        // Header String: mozilla/5.0 (iphone; u; cpu like mac os x; en) applewebkit/420+ (khtml, like gecko) version/3.0 mobile/1a543a safari/419.3
        // Browser Id: applewebkit
        // Browser Version: 420.0
        if (header.indexOf("mobile") > -1) {
          mobileFound = true;
          mobile = true;
        }
      } else if (header.indexOf("opera") > -1) {
        //Opera likes to impersonate other browsers
        //User-Agent: mozilla/4.0 (compatible; msie 6.0; msie 5.5; windows 98) opera 7.02  [en]
        //User-Agent: mozilla/3.0 (windows 98; u) opera 7.02  [en]
        //User-Agent: opera/9.25 (macintosh; intel mac os x; u; en)
        this.id = OPERA;
        if (header.indexOf("[", header.indexOf("opera")) != -1) {
          version = parseVersion(header.substring(header.indexOf("opera") + 5, header.indexOf("[", header.indexOf("opera"))).trim());
        }
        if (header.indexOf("(", header.indexOf("opera")) != -1) {
          version = parseVersion(header.substring(header.indexOf("opera") + 5, header.indexOf("(", header.indexOf("opera"))).trim());
        }
      } else if (header.indexOf("mozilla") > -1) {
        //User-Agent: mozilla/5.0 (x11; u; linux i686; en-us; rv:1.3b) gecko/20030211
        //User-Agent: mozilla/5.0 (macintosh; u; ppc mac os x; en-us; rv:1.0.1) gecko/20021104 chimera/0.6
        //User-Agent: mozilla/5.0 (x11; u; linux i686; en-us; rv:1.0.1) gecko/20020830
        //User-Agent: mozilla/5.0 (windows; u; win98; en-us; rv:1.0.2) gecko/20030208 netscape/7.02
        if (header.indexOf("gecko/") > -1 && header.indexOf("rv:") > -1) {
          this.id = MOZILLA;
          version = parseVersion(header.substring(header.indexOf("rv:") + 3, header.indexOf(") gecko")));
        } else if (header.indexOf("gecko") > -1) {
          this.id = NETSCAPE;
          version = 6;
        } else {
          //Just make a default
          this.id = NETSCAPE;
          this.version = 4;
        }
      } else {
        this.id = NETSCAPE;
        this.version = 4;
      }

      // Test for a few bots to reduce tracker counts
      if (header.indexOf("Googlebot") > -1) {
        bot = true;
      } else if (header.indexOf("Yahoo! Slurp") > -1) {
        bot = true;
      } else if (header.indexOf("search.msn.com/msnbot") > -1) {
        bot = true;
      } else if (header.indexOf("VoilaBot") > -1) {
        bot = true;
      } else if (header.indexOf("ZyBorg") > -1) {
        bot = true;
      } else if (header.indexOf("FAST-WebCrawler") > -1) {
        bot = true;
      } else if (header.indexOf("DeepIndex") > -1) {
        bot = true;
      } else if (header.indexOf("Ask Jeeves") > -1) {
        bot = true;
      } else if (header.indexOf("Gigabot") > -1) {
        bot = true;
      } else if (header.indexOf("Openbot") > -1) {
        bot = true;
      }
      LOG.trace("Browser Id: " + getBrowserId());
      LOG.trace("Browser Version: " + getBrowserVersion());
      LOG.trace("Browser O/S: " + getOsString());
    }
    String acceptLanguage = request.getHeader("Accept-Language");
    if (acceptLanguage != null) {
      StringTokenizer languages = new StringTokenizer(acceptLanguage, ",");
      if (languages.hasMoreTokens()) {
        String firstLanguage = languages.nextToken();
        if (firstLanguage.indexOf(";") > -1) {
          firstLanguage = firstLanguage.substring(0, firstLanguage.indexOf(";"));
        }
        firstLanguage = firstLanguage.trim();
        try {
          Locale locale = null;
          int index = firstLanguage.indexOf("-");
          if (index > -1) {
            locale = new Locale(firstLanguage.substring(0, index), firstLanguage.substring(index + 1));
          } else {
            locale = new Locale(firstLanguage, "");
          }
          language = locale.toString();
          if (language != null) {
            LOG.trace("Language: " + language);
          }
        } catch (Exception e) {
          language = "en_US";
        }
      }
    }
    // During the logout process, re-use the previous language setting
    if (request.getAttribute("webSiteLanguage") != null) {
      language = (String) request.getAttribute("webSiteLanguage");
    }
    referer = request.getHeader("Referer");
    if (referer != null) {
      LOG.trace("Referer: " + referer);
    }
    //TODO: Log this new user's information for reference
  }


  /**
   * Sets the type attribute of the ClientType object
   *
   * @param tmp The new type value
   */
  public void setType(int tmp) {
    this.type = tmp;
  }


  /**
   * Sets the id attribute of the ClientType object
   *
   * @param tmp The new id value
   */
  public void setId(int tmp) {
    this.id = tmp;
  }


  /**
   * Sets the version attribute of the ClientType object
   *
   * @param tmp The new version value
   */
  public void setVersion(double tmp) {
    this.version = tmp;
  }


  /**
   * Sets the os attribute of the ClientType object
   *
   * @param tmp The new os value
   */
  public void setOs(int tmp) {
    this.os = tmp;
  }


  /**
   * Gets the type attribute of the ClientType object
   *
   * @return The type value
   */
  public int getType() {
    return type;
  }


  /**
   * Gets the id attribute of the ClientType object
   *
   * @return The id value
   */
  public int getId() {
    return id;
  }


  /**
   * Gets the version attribute of the ClientType object
   *
   * @return The version value
   */
  public double getVersion() {
    return version;
  }


  /**
   * Gets the os attribute of the ClientType object
   *
   * @return The os value
   */
  public int getOs() {
    return os;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getReferer() {
    return referer;
  }

  public boolean getMobile() {
    return mobile;
  }

  public void setMobile(boolean mobile) {
    this.mobile = mobile;
  }

  public boolean getMobileFound() {
    return mobileFound;
  }

  public boolean isBot() {
    return bot;
  }

  public void setBot(boolean bot) {
    this.bot = bot;
  }

  /**
   * Gets the browserId attribute of the ClientType object
   *
   * @return The browserId value
   */
  public String getBrowserId() {
    String thisId = null;
    switch (id) {
      case IE:
        thisId = "ie";
        break;
      case MOZILLA:
        thisId = "moz";
        break;
      case NETSCAPE:
        thisId = "ns";
        break;
      case POCKETIE:
        thisId = "pie";
        break;
      case OPERA:
        thisId = "opera";
        break;
      case APPLEWEBKIT:
        thisId = "applewebkit";
        break;
      default:
        thisId = "moz";
        break;
    }
    return thisId;
  }


  /**
   * Gets the browserVersion attribute of the ClientType object
   *
   * @return The browserVersion value
   */
  public double getBrowserVersion() {
    return version;
  }


  /**
   * Gets the osString attribute of the ClientType object
   *
   * @return The osString value
   */
  public String getOsString() {
    switch (os) {
      case LINUX:
        return "linux";
      case MAC:
        return "mac";
      case WINDOWS:
        return "win";
      default:
        return "win";
    }
  }


  /**
   * Description of the Method
   *
   * @param versionText Description of the Parameter
   * @return Description of the Return Value
   */
  public double parseVersion(String versionText) {
    try {
      return Double.parseDouble(versionText);
    } catch (Exception e) {
      StringBuffer sb = new StringBuffer();
      boolean hasPoint = false;
      for (int i = 0; i < versionText.length(); i++) {
        char c = versionText.charAt(i);
        if (allowed.indexOf(c) > -1) {
          if (c == '.') {
            if (hasPoint) {
              break;
            } else {
              hasPoint = true;
            }
          }
          sb.append(c);
        }
      }
      try {
        return Double.parseDouble(sb.toString());
      } catch (Exception e2) {
        return -1;
      }
    }
  }


  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public boolean showApplet() {
    //if ((id == APPLEWEBKIT && version < 312) ||
    if (id == APPLEWEBKIT ||
        (id == IE && version >= 5 && version < 5.5)) {
      return true;
    } else {
      return false;
    }
  }
}

