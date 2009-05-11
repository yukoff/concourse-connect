ConcourseConnect


----------------------------------------------------------------------------
| LEGAL                                                                    |
----------------------------------------------------------------------------

ConcourseConnect
Copyright 2009 Concursive Corporation
http://www.concursive.com

Concursive ConcourseConnect is free software: you can redistribute it and/or
modify it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, version 3 of the License.

Under the terms of the GNU Affero General Public License you must release the
complete source code for any application that uses any part of ConcourseConnect
(system header files and libraries used by the operating system are excluded).
These terms must be included in any work that has ConcourseConnect components.
If you are developing and distributing open source applications under the
GNU Affero General Public License, then you are free to use ConcourseConnect
under the GNU Affero General Public License.

If you are deploying a web site in which users interact with any portion of
ConcourseConnect over a network, the complete source code changes must be made
available.  For example, include a link to the source archive directly from
your web site.

For OEMs, ISVs, SIs and VARs who distribute ConcourseConnect with their
products, and do not license and distribute their source code under the GNU
Affero General Public License, Concursive provides a flexible commercial
license.

To anyone in doubt, we recommend the commercial license. Our commercial license
is competitively priced and will eliminate any confusion about how
ConcourseConnect can be used and distributed.

ConcourseConnect is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more
details.

You should have received a copy of the GNU Affero General Public License
along with ConcourseConnect.  If not, see <http://www.gnu.org/licenses/>.

Attribution Notice: ConcourseConnect is an Original Work of software created
by Concursive Corporation


----------------------------------------------------------------------------
| INTRODUCTION                                                             |
----------------------------------------------------------------------------

Welcome to ConcourseConnect!

The installation and configuration is intended to be as simple as possible.

PLEASE READ ALL INSTRUCTIONS TO AVOID ANY PROBLEMS.

If you are going to be developing ConcourseConnect, please make sure
to read the developer information at http://www.concursive.com,
you can also find information about installing, configuring, and using
ConcourseConnect there.

ConcourseConnect includes and licenses many 3rd party libraries.
These are distributed with ConcourseConnect for convenience.
See below, in the "About this Software" section, for specific license
information.


----------------------------------------------------------------------------
| INSTALLATION SUMMARY                                                     |
----------------------------------------------------------------------------

Stable versions of ConcourseConnect are deployed as a .war, and only
when all of the install and upgrade scripts have been thoroughly tested.
The developers include an automated installer and upgrader as a
precompiled .war file.  This .war includes end-of-cycle install and upgrade
scripts and is available at the http://www.concursive.com web site.

Deploying from a .war file:

1. Install Sun Java (JRE 5+)
   http://www.java.com/en/download/manual.jsp
2. Install the Apache Tomcat Web Application Server (Tomcat 5.5+)
   http://tomcat.apache.org
3. Install the PostgreSQL Database Server (PostgreSQL 8+)
   http://www.postgresql.org
4. Copy the connect.war file into Tomcat's webapp directory
5. Access the web app using a browser at "http://localhost/connect" and
follow the web-based instructions

For more detailed instructions, download the Installation and Setup Guide
from:
http://www.concursive.com/show/concourseconnect-early-access/documents


----------------------------------------------------------------------------
| SOURCE INSTALLATION                                                      |
----------------------------------------------------------------------------

Source versions of ConcourseConnect can be deployed as an exploded webapp
directory into Apache Tomcat.  This is the easiest way to develop
ConcourseConnect.  In this scenario, you can also deploy within your
preferred Java IDE with embedded Tomcat container, bypassing the ant
script altogether.

The following software is required to package and test ConcourseConnect:

  Sun J2SE JDK 5 or 6
  http://java.sun.com

  Apache Tomcat 5.5, use with Sun J2SE 5.0
  Apache Tomcat 6.0, use with Sun J2SE 6.0
  http://tomcat.apache.org

  Apache Ant 1.7
  http://ant.apache.org

  Postgresql 8.3
  http://www.postgresql.org

Steps for packaging the source:

1. Copy the file build.properties.example to build.properties, be sure to
   edit the new file and update the specified file paths
2. Execute "ant package"
3. Look in the "target/concourseconnect" directory for the .war file


----------------------------------------------------------------------------
| ABOUT THIS SOFTWARE                                                      |
----------------------------------------------------------------------------

ConcourseConnect licenses libraries and code from the following
projects, some are proprietary in which Concursive has been
granted a license to redistribute, some are open source and are used
according to the project license:

Project Name                      License
--------------------------------  -----------------------------------------
backport-util-concurrent          Public Domain
Batik                             Apache Software License
Bean Shell                        Sun Public License
Bouncy Castle Crypto API          Bouncy Castle Open Source License
Castor                            Apache Software License v2.0
CivicSpace Labs DB                Creative Commons Attribution-ShareAlike
EHCache                           Apache Software License v2.0
FreeMarker                        Visigoth Software Society BSD License
Google:maps                       Freeware
HTMLCleaner                       BSD
HTTPMultiPartParser               iSavvix Public License
iText                             LGPL
JFreeChart                        LGPL
Jakarta Collections               Apache Software License
Jakarta Commons                   Apache Software License
Jakarta Digester                  Apache Software License
Jakarta JRCS                      Apache Software License
Jakarta Taglibs JSTL              Apache Software License
Jasper Reports                    LGPL
Java Activitation Framework       Sun License
Java Image Scaling                LGPL v3.0
Java Mail                         Sun License
Java Transaction API              Sun License
JHLabs                            Apache Software License v2.0
JSTL                              Sun License
jTDS Microsoft SQL Server Driver  LGPL
JUnit                             Common Public License 1.0
JUnit-addons                      JUnit-addons Software License 1.0
Log4J                             Apache Software License
Lucene                            Apache Software License
NekoHTML                          Apache style
OpenCSV                           Apache Software License v2.0
PackTag                           LGPL
Pluto                             Apache Software License v2.0
PDFBox                            BSD
POI                               Apache Software License
PostgreSQL JDBC Driver            BSD
Quartz                            Apache Software License
ROME                              Apache Software License
Spring                            Apache Software License v2.0
TinyMCE                           LGPL
TMExtractors                      Apache style
UnitPNGFix by Unit Interactive    Creative Commons Attribution 3.0 Unported
 http://labs.unitinteractive.com/unitinteractive.com
Xerces                            Apache Software License
Ximian Icons                      LGPL
Yahoo User Interface (YUI)        BSD
ypSlideOutMenu                    Creative Commons Attribution 2.0 license
Yusuke Kamiyamane Icons           Creative Commons Attribution 3.0 license
