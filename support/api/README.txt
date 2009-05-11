$Id: README.txt 24135 2008-01-24 06:18:21Z matt $

ConcourseConnect API Tools @TOOLS.VERSION@

----------------------------------------------------------------------------
| LEGAL                                                                    |
----------------------------------------------------------------------------

ConcourseConnect API Tools is licensed under the GNU LESSER GENERAL PUBLIC
LICENSE (LGPL) Version 2.1, February 1999;
http://www.gnu.org/licenses/lgpl-2.1.txt

You should have received a copy of the LGPL with this source code package in
the LICENSE file. Compiling or using this software signifies your acceptance
of the GNU LESSER GENERAL PUBLIC LICENSE.

Copyright(c) 2009 Concursive Corporation.
http://www.concursive.com/
CONCURSIVE CORPORATION MAKES NO REPRESENTATIONS AND EXTENDS NO WARRANTIES,
EXPRESS OR IMPLIED, WITH RESPECT TO THE SOFTWARE, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR ANY PARTICULAR
PURPOSE, AND THE WARRANTY AGAINST INFRINGEMENT OF PATENTS OR OTHER
INTELLECTUAL PROPERTY RIGHTS. THE SOFTWARE IS PROVIDED "AS IS", AND IN NO
EVENT SHALL CONCOURSE SUITE, INC. OR ANY OF ITS AFFILIATES BE LIABLE FOR
ANY DAMAGES, INCLUDING ANY LOST PROFITS OR OTHER INCIDENTAL OR CONSEQUENTIAL
DAMAGES RELATING TO THE SOFTWARE.



----------------------------------------------------------------------------
| INTRODUCTION                                                             |
----------------------------------------------------------------------------

This lightweight package includes Java code to communicate with
the ConcourseConnect application over HTTP/S using XML.

For example, you can add contacts or listings from your existing web site
or Java application and send them straight into ConcourseConnect.  Data can
also be retrieved.

  Methods supported:

    DataRecord.INSERT
    DataRecord.SELECT
    DataRecord.UPDATE
    DataRecord.DELETE
    DataRecord.GET_DATETIME



----------------------------------------------------------------------------
| CHANGELOG                                                                |
----------------------------------------------------------------------------

v1.0

  March 27, 2009
  The included object map is now much more comprehensive.  There are now
  methods to backup and restore partial databases.

  August 12, 2008
  APIConnection has a getter/setter for setting a session cookie to re-use
  sessions between APIConnection constructs

  June 25, 2008
  APIConnection re-uses the session set by the server for multiple calls
  load() has been removed

  June 10, 2008
  Package names have changed to avoid conflicts
   com.concursive.commons.api.APIConnection is the same
   com.zeroio.transfer.* replaces org.aspcfs.apps.transfer.*

  March 18, 2008
  Implemented action="backup" and action="restore"
  The following dependencies to the tools have been added:
   javax.xml.stream.XMLInputFactory;
   javax.xml.stream.XMLStreamException;
   javax.xml.stream.XMLStreamReader;

  March 6, 2008
  Added DataRecord.addField(String, Long);
  Added DataRecord.addField(String, Date);
  Added DataRecord.addField(String, boolean);
  Added DataRecord.NULL value for setting null values on fields
  Fixed getResponseValueAsBoolean() was broken

  February 26, 2008
  Added getResponseValueAsInt("") and getResponseValueAsBoolean("")

  February 19, 2008
  Added PasswordHash class for encrypting user passwords before insert/update
    com.concursive.commons.codec.PasswordHash.encrypt("user's password");

  February 6, 2008
  Methods to retrieve status and records from server response:
    boolean hasError()
    int getRecordCount()
    String getResponseValue(String field)
    ArrayList<DataRecord> getRecords()
  When reusing the APIConnection with multiple commits, several values
    are now properly reset
  load() is not recommended at this time as it performs an immediate commit;
    use save() for all action types



----------------------------------------------------------------------------
| REQUIREMENTS                                                             |
----------------------------------------------------------------------------

The connect_tools.jar can be used with any Java 1.5 application.

The API is accessed by configuring a "client" in the ConcourseConnect
database which allows the client to perform any action and is not
user-specific.

*  In the [sync_client] table, an arbitrary client must be inserted
   with a plain-text password in the [code] field.  This will be used in
   the client authentication code.

-[ sync_client record ]-----------------------------------------------------
client_id  | 1
type       | API
version    | 1.0
entered    | 2008-01-23 22:05:04.425
enteredby  | 1
modified   | 2008-01-23 22:05:04.425
modifiedby | 1
anchor     |
enabled    | t
code       | some-arbitrary-password
----------------------------------------------------------------------------


All objects which the client can access must be added to object_map.xml.


----------------------------------------------------------------------------
| TYPICAL USAGE                                                            |
----------------------------------------------------------------------------

import com.concursive.commons.api.APIConnection;
import com.concursive.commons.api.DataRecord;

print("Starting transaction...");

APIConnection conn = new APIConnection();
conn.setUrl("http://127.0.0.1:8080");
conn.setClientId(1);
conn.setSystemId(1);
conn.setCode("plaintext-code-in-database");

// Example which adds a project and a team member in one transaction

conn.setAutoCommit(false);

DataRecord record = new DataRecord();
record.setName("project");
record.setAction(DataRecord.INSERT);
record.setShareKey(true);
record.addField("title", "API Project Title");
record.addField("shortDescription", "This is the short description");
record.addField("requestDate", "2007-05-01 00:00:00 -0400");
record.addField("requestedBy", "Project Manager");
record.addField("showNews", "true");
record.addField("showWiki", "true");
record.addField("showTeam", "true");
record.addField("enteredBy", 1);
record.addField("modifiedBy", 1);
record.addField("groupId", 1);
conn.save(record);

DataRecord record = new DataRecord();
record.setName("teamMember");
record.setAction(DataRecord.INSERT);
record.addField("projectId", "$C{project.id}");
record.addField("userId", 2);
record.addField("userLevel", 1);
record.addField("enteredBy", 1);
record.addField("modifiedBy", 1);
conn.save(record);

conn.commit();
if (conn.hasError()) {
  System.out.println("Commit error: " + conn.getErrorText());
}
