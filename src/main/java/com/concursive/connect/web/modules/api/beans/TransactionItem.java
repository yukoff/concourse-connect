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

package com.concursive.connect.web.modules.api.beans;

import com.concursive.commons.api.DataRecord;
import com.concursive.commons.api.Record;
import com.concursive.commons.api.RecordList;
import com.concursive.commons.http.RequestUtils;
import com.concursive.commons.objects.ObjectUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.commons.web.mvc.beans.GenericBean;
import com.concursive.commons.workflow.ObjectHookAction;
import com.concursive.commons.xml.XMLUtils;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.indexer.IndexEvent;
import com.concursive.connect.scheduler.ScheduledJobs;
import com.concursive.connect.web.modules.api.dao.SyncTable;
import com.concursive.connect.web.modules.api.services.CustomActionHandler;
import com.concursive.connect.web.utils.CustomLookupElement;
import com.concursive.connect.web.utils.CustomLookupList;
import com.concursive.connect.web.utils.PagedListInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.w3c.dom.Element;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Every Transaction can be made of many TransactionItems. TransactionItems
 * represent objects in which a method will be called upon.<p>
 * <p/>
 * Example:<br>
 * The TransactionItem is to insert an Organization. So, the object is
 * Organization, the action is an INSERT, the meta property contains fields
 * that are to be returned after the insert is executed, any errors that occur
 * are placed in the errorMessage property.
 *
 * @author matt rajkowski
 * @created April 10, 2002
 */
public class TransactionItem {

  private static Log LOG = LogFactory.getLog(TransactionItem.class);

  //Requested object actions
  public final static byte INSERT = 1;
  public final static byte SELECT = 2;
  public final static byte UPDATE = 3;
  public final static byte DELETE = 4;
  private final static byte SYNC = 5;
  private final static byte SYNC_START = 6;
  private final static byte SYNC_END = 7;
  private final static byte SYNC_DELETE = 8;
  private final static byte GET_DATETIME = 9;
  private final static byte CUSTOM_ACTION = 10;
  private final static byte VALIDATE = 11;

  private String name = null;
  private Object object = null;
  private Element objectElement = null;
  private int action = -1;
  private String actionName = null;
  private String actionMethod = null;
  private int identity = 1;
  private PagedListInfo pagedListInfo = null;
  private StringBuffer errorMessage = new StringBuffer();
  private RecordList recordList = null;
  private TransactionMeta meta = null;
  private HashMap<String, String> ignoredProperties = null;
  private PacketContext packetContext = null;
  private TransactionContext transactionContext = null;
  private boolean shareKey = false;
  private boolean cached = false;
  private int count = -1;

  /*
  * Constructor for the TransactionItem object
  */
  public TransactionItem() {
  }

  /*
  * Constructor a TransactionItem Object from an XML element, using the
  * supplied mapping to translate the XML element tag name to a Class.
  *
  * @param objectElement Description of Parameter
  * @param mapping       Description of Parameter
  */
  public TransactionItem(Element objectElement, PacketContext packetContext,
                         TransactionContext context) {
    try {
      this.setAction(objectElement);
      this.setPacketContext(packetContext);
      this.setObject(objectElement, packetContext.getObjectMap());
      this.objectElement = objectElement;
      this.transactionContext = context;
      // Populate just the meta-data here
      if ("meta".equals(name)) {
        ignoredProperties = XMLUtils.populateObject(object, objectElement);
      }
    } catch (Exception e) {
      LOG.debug("Error instantiating TransactionItem for: " + objectElement.getTagName(), e);
      appendErrorMessage("Invalid element: " + objectElement.getTagName());
    }
  }

  /*
  * Sets the object attribute of the TransactionItem object
  *
  * @param tmp The new object value
  */
  public void setObject(Object tmp) {
    object = tmp;
  }

  /*
  * Sets the object attribute of the TransactionItem object from XML based on
  * the mapping data. If the element tag is "contact" and there is a mapping
  * to class "Contact", then the Object is created and populated from the XML.
  *
  * @param element The new object value
  * @param mapping The new object value
  * @throws Exception Description of Exception
  */
  public void setObject(Element element, HashMap mapping) throws Exception {
    name = element.getTagName();
    if (mapping.containsKey(name)) {
      SyncTable thisMapping = (SyncTable) mapping.get(name);
      // Instantiate the object
      object = Class.forName(thisMapping.getMappedClassName()).newInstance();
      LOG.debug("Instantiated object: " + object.getClass().getName());
    } else {
      LOG.warn("Mapping does not exist for creating object: " + name);
    }
  }

  public void setAdditionalObjectParameters(HashMap mapping) throws Exception {
    if (name == null) {
      throw new Exception("TransactionItem-> name is null");
    }
    // If there are other attributes, set them on the object after the object has been populated
    if (mapping.containsKey(name)) {
      SyncTable thisMapping = (SyncTable) mapping.get(name);
      if (thisMapping.getTableName() != null) {
        ObjectUtils.setParam(object, "tableName", thisMapping.getTableName());
      }
      if (thisMapping.getUniqueField() != null) {
        ObjectUtils.setParam(object, "uniqueField", thisMapping.getUniqueField());
      }
      if (thisMapping.getSortBy() != null) {
        ObjectUtils.setParam(object, "sortBy", thisMapping.getSortBy());
      }
    }
    // Override the supplied instance on INSERTs or SELECTs
    if (action == TransactionItem.INSERT || action == TransactionItem.SELECT) {
      ObjectUtils.setParam(object, "instanceId", packetContext.getInstanceId());
    }
  }

  /**
   * Sets the action attribute of the TransactionItem object
   *
   * @param tmp The new action value
   */
  public void setAction(int tmp) {
    action = tmp;
  }

  /**
   * Determines the methods that are allowed from a specified action. These are
   * the methods that can be executed on the new Object.
   *
   * @param tmp The new action value
   */
  public void setAction(String tmp) {
    if (DataRecord.INSERT.equals(tmp)) {
      setAction(INSERT);
    } else if (DataRecord.UPDATE.equals(tmp)) {
      setAction(UPDATE);
    } else if (DataRecord.SELECT.equals(tmp)) {
      setAction(SELECT);
    } else if (DataRecord.DELETE.equals(tmp)) {
      setAction(DELETE);
    } else if ("getDateTime".equals(tmp)) {
      setAction(GET_DATETIME);
    } else {
      setAction(CUSTOM_ACTION);
    }
    actionName = tmp;
    // TODO: Add CUSTOM_PROCESS?
  }

  /**
   * Sets the action attribute of the TransactionItem object
   *
   * @param objectElement The new action value
   */
  public void setAction(Element objectElement) {
    if (objectElement.hasAttributes()) {
      //Get the action for this item (Insert, Update, Delete, Select, etc.)
      String thisAction = objectElement.getAttribute("type");
      if (thisAction == null || thisAction.trim().equals("")) {
        thisAction = objectElement.getAttribute("action");
      }
      this.setAction(thisAction);
      //If specified, get the client's next id that should be used when
      //sending insert statements to the client
      String thisIdentity = objectElement.getAttribute("identity");
      try {
        identity = Integer.parseInt(thisIdentity);
      } catch (Exception e) {
      }
      // Enable paging
      if (objectElement.hasAttribute("offset") ||
          objectElement.hasAttribute("items") ||
          objectElement.hasAttribute("sort")) {
        // Ready for a new pagedListInfo
        pagedListInfo = new PagedListInfo();
        // If specified, get the number of max records to return, and the offset
        // to begin returning records at -- useful for large datasets
        String thisCurrentOffset = objectElement.getAttribute("offset");
        String thisItemsPerPage = objectElement.getAttribute("items");
        if (StringUtils.hasText(thisCurrentOffset) ||
            StringUtils.hasText(thisItemsPerPage)) {
          pagedListInfo.setItemsPerPage(thisItemsPerPage);
          pagedListInfo.setCurrentOffset(thisCurrentOffset);
        }
        // Determine the sort order
        if (objectElement.hasAttribute("sort")) {
          pagedListInfo.setDefaultSort(objectElement.getAttribute("sort"), objectElement.getAttribute("sortOrder"));
        }
      }
      // See if the primary key of this object should be exposed to other
      // items within the same transaction
      shareKey = "true".equals(objectElement.getAttribute("shareKey"));
      cached = "true".equals(objectElement.getAttribute("cached"));
    }
  }

  public int getAction() {
    return action;
  }

  /**
   * Sets the meta attribute of the TransactionItem object
   *
   * @param tmp The new meta value
   */
  public void setMeta(TransactionMeta tmp) {
    this.meta = tmp;
  }

  /**
   * Sets the packetContext attribute of the TransactionItem object
   *
   * @param tmp The new packetContext value
   */
  public void setPacketContext(PacketContext tmp) {
    this.packetContext = tmp;
  }

  public PacketContext getPacketContext() {
    return packetContext;
  }

  /**
   * Sets the transactionContext attribute of the TransactionItem object
   *
   * @param tmp The new transactionContext value
   */
  public void setTransactionContext(TransactionContext tmp) {
    this.transactionContext = tmp;
  }

  /**
   * Sets the shareKey attribute of the TransactionItem object
   *
   * @param tmp The new shareKey value
   */
  public void setShareKey(boolean tmp) {
    this.shareKey = tmp;
  }

  /**
   * Gets the errorMessage attribute of the TransactionItem object
   *
   * @return The errorMessage value
   */
  public String getErrorMessage() {
    return (errorMessage.toString());
  }

  /**
   * Gets the name attribute of the TransactionItem object
   *
   * @return The name value
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the object attribute of the TransactionItem object
   *
   * @return The object value
   */
  public Object getObject() {
    return object;
  }

  /**
   * Gets the recordList attribute of the TransactionItem object
   *
   * @return The recordList value
   */
  public RecordList getRecordList() {
    return recordList;
  }

  public void setRecordList(RecordList recordList) {
    this.recordList = recordList;
  }

  /**
   * Gets the meta attribute of the TransactionItem object
   *
   * @return The meta value
   */
  public TransactionMeta getMeta() {
    return meta;
  }

  /**
   * Gets the transactionContext attribute of the TransactionItem object
   *
   * @return The transactionContext value
   */
  public TransactionContext getTransactionContext() {
    return transactionContext;
  }

  /**
   * Gets the shareKey attribute of the TransactionItem object
   *
   * @return The shareKey value
   */
  public boolean getShareKey() {
    return shareKey;
  }

  public boolean isCached() {
    return cached;
  }

  public void setCached(boolean cached) {
    this.cached = cached;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public PagedListInfo getPagedListInfo() {
    return pagedListInfo;
  }

  /**
   * Assumes that the Object has already been built and populated, now the
   * specified action will be executed. A database connection is passed along
   * since the Object will need it.<p>
   * <p/>
   * Data can be selected, inserted, updated, deleted, and synchronized with
   * client systems.
   *
   * @param db Description of Parameter
   * @throws Exception Description of Exception
   */
  public void execute(Connection db) throws Exception {
    LOG.debug("Executing transaction...");
    // Query the object from the database if an "UPDATE" or "DELETE" action is requested
    if (action == TransactionItem.UPDATE || action == TransactionItem.DELETE) {
      // Might need a shared parameter
      String nodeText = XMLUtils.getNodeText(XMLUtils.getFirstChild(objectElement, "id"));
      if (nodeText != null && nodeText.startsWith("$C{")) {
        nodeText = transactionContext.getPropertyMap().get(
            nodeText.substring(nodeText.indexOf("$C{") + 3, nodeText.indexOf("}")));
      }
      // Construct the object before setting any parameters
      object = ObjectUtils.constructObject(
          object.getClass(), db, Integer.parseInt(nodeText));
    }
    // Populate the object from XML and store any unset values for later use
    ignoredProperties = XMLUtils.populateObject(object, objectElement);
    // Override any values from the object map
    setAdditionalObjectParameters(packetContext.getObjectMap());
    // Validate several requirements
    if ((object == null || name == null) && !"system".equals(name)) {
      appendErrorMessage("Unsupported object specified");
      return;
    }
    // A record list will be returned to the client
    recordList.setName(name);
    // A pagedList will allow a subset of a query to be returned if specified by client
    if (pagedListInfo != null) {
      doSetPagedListInfo();
    }
    // Populate any items from the TransactionContext
    setContextParameters();
    //Begin action specific processing
    if (action == GET_DATETIME) {
      Record thisRecord = new Record("info");
      thisRecord.put(
          "dateTime", String.valueOf(
              new java.sql.Timestamp(new java.util.Date().getTime())));
      recordList.add(thisRecord);
    } else if (action == CUSTOM_ACTION) {
      LOG.debug("Custom action...");
      // A CUSTOM_ACTION is when an object has an action other than INSERT/UPDATE/DELETE/SELECT
      // Instantiate the named custom action for the specified object
      SyncTable syncMappings = packetContext.getObjectMap().get(actionName);
      if (syncMappings == null) {
        LOG.error("Invalid action called: " + actionName);
      } else {
        String customClassName = syncMappings.getMappedClassName();
        Object customAction = Class.forName(customClassName).newInstance();
        if (customAction instanceof CustomActionHandler) {
        	ignoredProperties = XMLUtils.populateObject(customAction, objectElement);
          LOG.debug("Processing..." + customAction.getClass().getName());
          Object result = ((CustomActionHandler) customAction).process(this, db);
          checkResult(result);
          if (result instanceof Boolean && !((Boolean) result)) {
            appendErrorMessage("There was an error while processing the requested action.");
          }
        } else {
          LOG.error("Object is not an instance of CustomActionHandler: " + customAction.getClass().getName());
          appendErrorMessage("Object does not implement CustomActionHandler and cannot be processed: " + customAction.getClass().getName());
        }
      }
    } else {
      //This is a typical insert, update, delete, select record(s) request
      LOG.debug("Standard action");
      //Determine the method to execute on the object
      String executeMethod = null;
      switch (action) {
        case -1:
          appendErrorMessage("Action not specified");
          break;
        case INSERT:
          if (actionMethod != null) {
            executeMethod = actionMethod;
          } else {
            executeMethod = "insert";
          }
          break;
        case UPDATE:
          executeMethod = "update";
          break;
        case DELETE:
          executeMethod = "delete";
          break;
        case SELECT:
          executeMethod = "buildList";
          break;
        case VALIDATE:
          // TODO: Implement object validation whether an insert or update
          break;
        default:
          appendErrorMessage("Unsupported action specified");
          break;
      }
      if (executeMethod != null) {
        // Execute the action
        Object result = doExecute(object, db, action, packetContext, executeMethod);
        checkResult(result);
        LOG.debug("Executing: " + executeMethod);
        if (action == INSERT) {
          // Insert the guid / id into client mapping, at this point, the object will have its
          // newly inserted id, so set the syncMap before the insert
          if (ignoredProperties != null && ignoredProperties.containsKey("guid")) {
            // Need to log the date/time of the new record for later approval of updates
            // Reload the newly inserted object to get its insert/modified date
            Object insertedObject = ObjectUtils.constructObject(
                object.getClass(), db, Integer.parseInt(
                    ObjectUtils.getParam(object, "id")));
            if (insertedObject == null) {
              // Might be a lookupElement
              insertedObject = ObjectUtils.constructObject(
                  object.getClass(), db, Integer.parseInt(
                      ObjectUtils.getParam(object, "id")), ObjectUtils.getParam(
                      object, "tableName"));
            }
            if (insertedObject == null) {
              // Might be a customLookupElement
              insertedObject = ObjectUtils.constructObject(
                  object.getClass(), db, Integer.parseInt(
                      ObjectUtils.getParam(object, "id")), ObjectUtils.getParam(
                      object, "tableName"), ObjectUtils.getParam(
                      object, "uniqueField"));
            }
            if (insertedObject == null) {
              LOG.warn("The object was inserted, but could not be reloaded: possible invalid constructor for: " + object.getClass().getName());
            }
          }
          addRecords(object, "processed");
        } else if (action == UPDATE) {
          // If the result is an Integer == 1, then the update is successful, else a "conflict"
          // since someone else updated it first
          if ((Integer) result == 1) {
            // Update the modified date in client mapping
            if (ignoredProperties != null && ignoredProperties.containsKey("guid")) {
              Object updatedObject = ObjectUtils.constructObject(
                  object.getClass(), db, Integer.parseInt(
                      ObjectUtils.getParam(object, "id")));
            }
            addRecords(object, "processed");
          } else {
            appendErrorMessage("Record could not be updated due to criteria/conflict");
          }
        } else if (action == DELETE) {
          addRecords(object, DataRecord.DELETE);
        } else if (action == SELECT) {
          // It wasn't an insert, update, or delete...
          recordList.clear();
          addRecords(object, null);
        }
      }
    }
    if (pagedListInfo != null) {
      recordList.setTotalRecords(pagedListInfo.getMaxRecords());
    }
  }

  /*
   * Description of the Method
   *
   * @return Description of the Returned Value
  */
  public boolean hasError() {
    return (errorMessage.length() > 0);
  }

  /*
   * Description of the Method
   *
   * @param tmp Description of Parameter
  */
  public void appendErrorMessage(String tmp) {
    if (tmp != null) {
      if (errorMessage.length() > 0) {
        errorMessage.append(System.getProperty("line.separator"));
      }
      errorMessage.append(tmp);
    }
  }

  /*
   * Sets the contextParameters, these are values from other objects within the
   * same transaction
  */
  private void setContextParameters() {
    // Go through the ignored property values and see if any need data from the context
    if (ignoredProperties != null && ignoredProperties.size() > 0) {
      for (String param : ignoredProperties.keySet()) {
        if (param != null) {
          String value = ignoredProperties.get(param);
          if (value != null && value.indexOf("$C{") > -1) {
            value = transactionContext.getPropertyMap().get(
                value.substring(value.indexOf("$C{") + 3, value.indexOf("}")));
            if (value != null) {
              LOG.debug("Setting context parameter: " + param + " data: " + value);
              ObjectUtils.setParam(object, param, value);
            }
          }
        }
      }
    }
  }


  /**
   * Processes the object according to the executeMethod
   *
   * @param object        Description of Parameter
   * @param db            Description of Parameter
   * @param action
   * @param packetContext
   * @param executeMethod Description of Parameter
   * @return Description of the Returned Value
   * @throws Exception Description of Exception
   */
  public static Object doExecute(Object object, Connection db, int action, PacketContext packetContext, String executeMethod) throws Exception {
    return doExecute(object, db, action, packetContext, executeMethod, true, true);
  }

  public static Object doExecute(Object object, Connection db, int action, PacketContext packetContext, String executeMethod, boolean hook, boolean cached) throws Exception {
    // Prepare the objects for execution
    Class[] argClass = new Class[]{Class.forName("java.sql.Connection")};
    Object[] argObject = new Object[]{db};
    Method method = null;
    if (action == DELETE) {
      try {
        // load the method that takes the fileLibrary path as an argument
        argClass = new Class[]{Class.forName("java.sql.Connection"), Class.forName("java.lang.String")};
        argObject = new Object[]{db, packetContext.getBaseFilePath()};
        object.getClass().getMethod(executeMethod, argClass);
      } catch (NoSuchMethodException nsme) {
        // method does not exist
        argClass = new Class[]{Class.forName("java.sql.Connection")};
        argObject = new Object[]{db};
      }
    }
    method = object.getClass().getMethod(executeMethod, argClass);
    // Retrieve the previous object before executing an action
    Object previousObject = null;
    if (hook && packetContext.getObjectHookManager() != null) {
      if (action == UPDATE || action == DELETE) {
        try {
          previousObject = ObjectUtils.constructObject(object.getClass(), db, Integer.parseInt(ObjectUtils.getParam(object, "id")));
          // TODO: it's possible that if the previous object wasn't found, it doesn't cause an exception
          // you would know by if the ID field isn't populated by the DAO, so check for id = -1
        } catch (Exception e) {
          // already deleted
          if (action == DELETE) {
            return true;
          }
          LOG.error("Previous object does not exist... update and delete actions require a previous object");
        }
      }
    }
    // Execute the action
    Object result = (method.invoke(object, argObject));

    // Update the Lucene Index
    Scheduler scheduler = packetContext.getScheduler();
    if (scheduler != null) {
      if (action == INSERT || action == UPDATE) {
        IndexEvent indexEvent = new IndexEvent(object, IndexEvent.ADD);
        ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      } else if (action == DELETE) {
        IndexEvent indexEvent = new IndexEvent(object, IndexEvent.DELETE);
        ((Vector) scheduler.getContext().get("IndexArray")).add(indexEvent);
      }
      scheduler.triggerJob("indexer", (String) scheduler.getContext().get(ScheduledJobs.CONTEXT_SCHEDULER_GROUP));
    }

    // Process any hooks
    if (hook && packetContext.getObjectHookManager() != null) {
      // Prepare required objects for ObjectHookManager
      ApplicationPrefs prefs = packetContext.getApplicationPrefs();
      boolean sslEnabled = "true".equals(prefs.get("SSL"));
      String url = ("http://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), packetContext.getActionContext().getRequest()));
      String secureUrl = ("http" + (sslEnabled ? "s" : "") + "://" + RequestUtils.getServerUrl(prefs.get(ApplicationPrefs.WEB_URL), prefs.get(ApplicationPrefs.WEB_PORT), packetContext.getActionContext().getRequest()));
      // Execute the process asynchronously
      switch (action) {
        case INSERT:
          packetContext.getObjectHookManager().process(ObjectHookAction.INSERT, null, object, -1, url, secureUrl);
          break;
        case UPDATE:
          packetContext.getObjectHookManager().process(ObjectHookAction.UPDATE, previousObject, object, -1, url, secureUrl);
          break;
        case DELETE:
          if (previousObject != null) {
            packetContext.getObjectHookManager().process(ObjectHookAction.DELETE, previousObject, null, -1, url, secureUrl);
          }
          break;
        default:
          break;
      }
    }
    return result;
  }


  /*
* Processes any errors returned by the object, currently for debugging
  *
  * @param result Description of the Parameter
  */
  private void checkResult(Object result) {
    try {
      if (result instanceof Boolean && !((Boolean) result)) {
        LOG.debug("Object failed...");
        if (object instanceof GenericBean) {
          HashMap<String, String> errors = ((GenericBean) object).getErrors();
          for (String errorKey : errors.keySet()) {
            String errorText = errors.get(errorKey);
            LOG.debug(" " + errorText);
          }
        }
      }
    } catch (Exception e) {
    }
  }

  /**
   * Configures the object with a pagedList, used when a subset of objects in a
   * list will be returned
   */
  private void doSetPagedListInfo() {
    try {
      Class[] theClass = new Class[]{pagedListInfo.getClass()};
      Object[] theObject = new Object[]{pagedListInfo};
      Method method = object.getClass().getMethod(
          "setPagedListInfo", theClass);
      method.invoke(object, theObject);
    } catch (Exception e) {
      //This class must not support the pagedListInfo
      if (System.getProperty("DEBUG") != null) {
        System.out.println(
            "TransactionItem-> Object does not have setPagedListInfo method");
      }
    }
  }


  /**
   * Adds a feature to the Records attribute of the TransactionItem object
   *
   * @param object       The feature to be added to the Records attribute
   * @param recordAction The feature to be added to the Records attribute
   * @return Description of the Return Value
   * @throws SQLException Description of Exception
   */
  public Record addRecords(Object object, String recordAction) throws SQLException {
    //Need to see if the Object is a collection of Objects, otherwise
    //just process it as a single record.
    if (object instanceof java.util.AbstractList) {
      if (object.getClass().getName().equals("CustomLookupList")) {
        // This is a class for custom lookup lists
        Iterator objectItems = ((CustomLookupList) object).iterator();
        while (objectItems.hasNext()) {
          CustomLookupElement objectItem = (CustomLookupElement) objectItems.next();
          Record thisRecord = new Record(recordAction);
          this.addFields(thisRecord, meta, objectItem);
          recordList.add(thisRecord);
        }
        return null;
      } else {
        // pojo
        Iterator objectItems = ((java.util.AbstractList) object).iterator();
        while (objectItems.hasNext()) {
          Object objectItem = objectItems.next();
          Record thisRecord = new Record(recordAction);
          this.addFields(thisRecord, meta, objectItem);
          recordList.add(thisRecord);
        }
      }
      return null;
    } else if (object instanceof java.util.AbstractMap) {
      Iterator objectItems = ((java.util.AbstractMap) object).values().iterator();
      while (objectItems.hasNext()) {
        Object objectItem = objectItems.next();
        Record thisRecord = new Record(recordAction);
        this.addFields(thisRecord, meta, objectItem);
        recordList.add(thisRecord);
      }
      return null;
    } else {
      Record thisRecord = new Record(recordAction);
      this.addFields(thisRecord, meta, object);
      recordList.add(thisRecord);
      return thisRecord;
    }
  }

  /**
   * Adds property names and values to the Record object, based on the supplied
   * meta data
   *
   * @param thisRecord The feature to be added to the Fields attribute
   * @param thisMeta   The feature to be added to the Fields attribute
   * @param thisObject The feature to be added to the Fields attribute
   * @throws SQLException Description of Exception
   */
  private void addFields(Record thisRecord, TransactionMeta thisMeta, Object thisObject) throws SQLException {
    if (thisMeta != null && thisMeta.getFields() != null) {
      for (String thisField : thisMeta.getFields()) {
        String thisValue = null;
        if (thisField.endsWith("Guid")) {
          String lookupField = thisField.substring(
              0, thisField.lastIndexOf("Guid"));
          String param = thisField.substring(0, thisField.lastIndexOf("Guid"));
          if (param.indexOf("^") > -1) {
            param = param.substring(param.indexOf("^") + 1);
            lookupField = thisField.substring(0, thisField.indexOf("^"));
            thisField = thisField.substring(0, thisField.indexOf("^"));
          }
        } else {
          // CustomLookupElement is a HashMap of CustomLookupColumn records
          if (thisObject instanceof CustomLookupElement && !"id".equals(thisField)) {
            thisValue = ((CustomLookupElement) thisObject).get(thisField).getValue();
          } else {
            thisValue = ObjectUtils.getParam(thisObject, thisField);
          }
          if (thisField.indexOf(".guid") > -1) {
            //This is a sub-object, so get the correct guid for the client
            String lookupField = thisField.substring(
                0, thisField.indexOf(".guid"));
          }
        }
        if (thisValue == null) {
          thisValue = DataRecord.NULL;
        }
        thisRecord.put(thisField, thisValue);
      }
      try {
        //Special items when sending back an action to the client...
        thisRecord.setRecordId(ObjectUtils.getParam(thisObject, "id"));
        if (thisRecord.containsKey("guid")) {
          if (thisRecord.getAction().equals("processed")) {
            thisRecord.put("guid", ignoredProperties.get("guid"));
          } else if (thisRecord.getAction().equals(DataRecord.INSERT)) {
            thisRecord.put("guid", String.valueOf(identity++));
          } else if (thisRecord.getAction().equals(DataRecord.UPDATE)) {
            //
          } else if (thisRecord.getAction().equals(DataRecord.DELETE)) {
            //Let the client know that its record was deleted
            thisRecord.put("guid", ignoredProperties.get("guid"));
          } else if (thisRecord.getAction().equals("conflict")) {
            thisRecord.put("guid", ignoredProperties.get("guid"));
          }
        }
      } catch (java.lang.NumberFormatException e) {
        //This object doesn't have an id, might have multiple keys
      }
    }
  }
}
