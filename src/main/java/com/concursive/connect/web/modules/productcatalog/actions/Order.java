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

package com.concursive.connect.web.modules.productcatalog.actions;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.email.SMTPMessage;
import com.concursive.commons.email.SMTPMessageFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.concursive.commons.web.mvc.actions.ActionContext;
import com.concursive.connect.Constants;
import com.concursive.connect.config.ApplicationPrefs;
import com.concursive.connect.web.controller.actions.GenericAction;
import com.concursive.connect.web.modules.documents.beans.FileDownload;
import com.concursive.connect.web.modules.documents.dao.FileItem;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserAdmins;
import com.concursive.connect.web.modules.productcatalog.beans.OrderBean;
import com.concursive.connect.web.modules.productcatalog.dao.*;
import com.concursive.connect.web.modules.productcatalog.gateway.AuthorizeNet;
import com.concursive.connect.web.utils.PagedListInfo;

import java.security.Key;
import java.sql.Connection;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Description of the Class
 *
 * @author matt rajkowski
 * @version $Id$
 * @created September 21, 2004
 */
public final class Order extends GenericAction {

	private static final Log LOG = LogFactory.getLog(Order.class);

  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDefault(ActionContext context) {
    //Make sure demo is enabled
    /*
     *  if (!"true".equals(getPref(context, "DEMO"))) {
     *  return "PermissionError";
     *  }
     */
    Connection db = null;
    try {
      // Allow paging through products
      PagedListInfo productListInfo = this.getPagedListInfo(
          context, "productListInfo");
      if ("".equals(productListInfo.getLink())) {
        productListInfo.setLink(context, ctx(context) + "/Order.do?command=Default");
        productListInfo.setItemsPerPage(50);
      }
      // Prepare the product list
      ProductList productList = new ProductList();
      productList.setEnabled(Constants.TRUE);
      productList.setParents(Constants.TRUE);
      productList.setShowInCatalog(Constants.TRUE);
      // Build the product list
      db = getConnection(context);
      productList.setPagedListInfo(productListInfo);
      productList.buildList(db);
      context.getRequest().setAttribute("productList", productList);
      return "OrderCatalogOK";
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return "OrderCatalogError";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandDetails(ActionContext context) {
    //Make sure demo is enabled
    /*
     *  if (!"true".equals(getPref(context, "DEMO"))) {
     *  return "PermissionError";
     *  }
     */
    String productId = context.getRequest().getParameter("pid");
    Connection db = null;
    try {
      db = getConnection(context);
      // Load the product
      Product product = new Product(db, Integer.parseInt(productId));
      if (product.getEnabled()) {
        context.getRequest().setAttribute("product", product);
        // See if the product has any children
        ProductList productList = new ProductList();
        productList.setEnabled(Constants.TRUE);
        productList.setParentId(product.getId());
        productList.setParents(Constants.FALSE);
        productList.buildList(db);
        // Prepare the product(s)
        productList.add(0, product);
        Iterator i = productList.iterator();
        while (i.hasNext()) {
          Product thisProduct = (Product) i.next();
          // Load the product's options
          OptionList optionList = new OptionList();
          optionList.setEnabled(Constants.TRUE);
          optionList.setProductId(thisProduct.getId());
          optionList.buildList(db);
          thisProduct.setOptionList(optionList);
          // Load the product's attachments
          AttachmentList attachmentList = new AttachmentList();
          attachmentList.setProductId(thisProduct.getId());
          attachmentList.buildList(db);
          thisProduct.setAttachmentList(attachmentList);
        }
        context.getRequest().setAttribute("productList", productList);
        return "OrderProductDetailsOK";
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return "OrderCatalogError";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandAdd(ActionContext context) {
    //Make sure demo is enabled
    /*
     *  if (!"true".equals(getPref(context, "DEMO"))) {
     *  return "PermissionError";
     *  }
     */
    String productId = context.getRequest().getParameter("pid");
    Connection db = null;
    try {
      OrderBean orderBean = (OrderBean) context.getFormBean();
      orderBean.setIpAddress(context.getIpAddress());
      orderBean.setBrowser(context.getBrowser());
      db = getConnection(context);
      // Load the product
      Product product = new Product(db, Integer.parseInt(productId));
      if (product.getEnabled()) {
        // Load the product's options
        OptionList optionList = new OptionList();
        optionList.setEnabled(Constants.TRUE);
        optionList.setProductId(product.getId());
        optionList.buildList(db);
        optionList.populate(context.getRequest());
        product.setOptionList(optionList);
        if (product.isValid()) {
          orderBean.add(product);
          return "OrderListOK";
        }
        context.getRequest().setAttribute("actionError", "Product configuration is invalid.  Please adjust the option values.");
        context.getRequest().setAttribute("configError", product.getConfigurationErrors());
        return "OrderProductConfigError";
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      freeConnection(context, db);
    }
    return "OrderCatalogError";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandList(ActionContext context) {
    //Make sure demo is enabled
    /*
     *  if (!"true".equals(getPref(context, "DEMO"))) {
     *  return "PermissionError";
     *  }
     */
    try {
      OrderBean orderBean = (OrderBean) context.getFormBean();
      return "OrderListOK";
    } catch (Exception e) {
			LOG.error("executeCommandList", e);
    }
    return "OrderCatalogError";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandRemove(ActionContext context) {
    //Make sure demo is enabled
    /*
     *  if (!"true".equals(getPref(context, "DEMO"))) {
     *  return "PermissionError";
     *  }
     */
    String uniqueId = context.getRequest().getParameter("uid");
    try {
      OrderBean orderBean = (OrderBean) context.getFormBean();
      orderBean.remove(Integer.parseInt(uniqueId));
      return "OrderListOK";
    } catch (Exception e) {
				LOG.error("executeCommandRemove", e);
    }
    return "OrderCatalogError";
  }


  /**
   * Description of the Method
   *
   * @param context Description of the Parameter
   * @return Description of the Return Value
   */
  public String executeCommandSave(ActionContext context) {
    ApplicationPrefs prefs = (ApplicationPrefs) context.getServletContext().getAttribute("applicationPrefs");
    Connection db = null;
    try {
      OrderBean orderBean = (OrderBean) context.getFormBean();
      if ("edit".equals(context.getRequest().getParameter("billing"))) {
        orderBean.getBilling().setReset(true);
      }
      if ("edit".equals(context.getRequest().getParameter("payment"))) {
        orderBean.getPayment().setReset(true);
      }
      if (orderBean.getProductList().size() == 0) {
        return "OrderListOK";
      }
      if (orderBean.requiresContactInformation()) {
        if (orderBean.getContactInformation().getAttempt()) {
          processErrors(context, orderBean.getContactInformation().getErrors());
        }
        return "OrderContactInformationFormOK";
      }
      // Check for billing information
      if (orderBean.requiresBillingAddress()) {
        orderBean.getBilling().setReset(false);
        if (!orderBean.getBilling().getAttempt() &&
            orderBean.getContactInformation().isValid()) {
          // Prefill contact information into billing form from contact information
          orderBean.getBilling().setNameFirst(orderBean.getContactInformation().getNameFirst());
          orderBean.getBilling().setNameLast(orderBean.getContactInformation().getNameLast());
          orderBean.getBilling().setOrganization(orderBean.getContactInformation().getOrganization());
          orderBean.getBilling().setEmail(orderBean.getContactInformation().getEmail());
        }
        if (orderBean.getBilling().getAttempt()) {
          processErrors(context, orderBean.getBilling().getErrors());
        }
        return "OrderBillingFormOK";
      }
      if (orderBean.requiresShippingAddress()) {
        if (orderBean.getShipping().getAttempt()) {
          processErrors(context, orderBean.getShipping().getErrors());
        }
        return "OrderShippingFormOK";
      }
      if (orderBean.requiresPayment() && orderBean.getChargeAmount() > 0.0) {
        orderBean.getPayment().setReset(false);
        if (!orderBean.getPayment().getAttempt()) {
          Key publicKey = PrivateString.loadKey(getPref(context, "FILELIBRARY") + "public.key");
          orderBean.getPayment().setKey(publicKey);
        }
        if (orderBean.getPayment().getAttempt()) {
          processErrors(context, orderBean.getPayment().getErrors());
        }
        return "OrderPaymentFormOK";
      }
      if (context.getRequest().getAttribute("actionError") != null) {
        return "OrderSystemError";
      }
	  LOG.debug("Order entry triggered");
      db = getConnection(context);
      synchronized (this) {
        if (!orderBean.isSaved()) {
          User thisUser = getUser(context);
          if (thisUser != null && thisUser.getId() > 0) {
            orderBean.setUserId(thisUser.getId());
          }
          // Insert with processed status = false
          orderBean.insert(db);
          // Process the credit card if specified
          if (orderBean.getPayment().getType() == Payment.TYPE_CREDITCARD &&
              orderBean.getChargeAmount() > 0.0) {
            if ("AUTHORIZE.NET".equals(prefs.get("AGENT.NAME"))) {
              AuthorizeNet authorizeNet = new AuthorizeNet(orderBean);
              authorizeNet.setLogin(prefs.get("AGENT.LOGIN"));
              authorizeNet.setTransactionKey(prefs.get("AGENT.KEY"));
              authorizeNet.setDescription("Online Order");
              if (orderBean.getPayment().getCreditCard().getNumericNumber().equals("5424000000000015")) {
                authorizeNet.setTestMode(true);
              }
              // TODO: add a field in orderBean to record the AGENT.NAME
              authorizeNet.authorizeAndCapture();
              if (authorizeNet.getResponseCode() != AuthorizeNet.APPROVED) {
                // TODO: Keep this logic in OrderBean
                context.getRequest().setAttribute("actionError", authorizeNet.getResponseText());
                orderBean.getBilling().setReset(true);
                orderBean.getPayment().setReset(true);
                orderBean.setSaved(false);
                return "CreditCardError";
              }
              // Update status to processed
              orderBean.getPayment().updateProcessed(db);
            }
          }
          // Send the order to the rules engine
          processInsertHook(context, orderBean);

          // Order is now complete, remove from session
          context.getSession().removeAttribute("orderBean");
          context.getRequest().setAttribute("orderBean", orderBean);
          OrderRecord orderRecord = new OrderRecord(db, orderBean.getId());
          context.getRequest().setAttribute("orderRecord", orderRecord);

          // Provide the attachment URLs to the user too...
          AttachmentList attachmentList = new AttachmentList();
          attachmentList.setProductIdRange(orderBean.getProductList().getIdRange());
          attachmentList.setAllowAfterCheckout(Constants.TRUE);
          attachmentList.buildList(db);
          context.getRequest().setAttribute("attachmentList", attachmentList);

          // Prepare the email notifications
          String adminEmails = UserAdmins.getEmailAddresses(db);

          // Provide the attachment URLs to the user too which will be emailed...
          AttachmentList emailAttachments = new AttachmentList();
          emailAttachments.setProductIdRange(orderBean.getProductList().getIdRange());
          emailAttachments.setSendAsEmail(Constants.TRUE);
          emailAttachments.buildList(db);

          // Finished with the connection
          freeConnection(context, db);
          db = null;

          // Notify those interested
          try {
            String CRLF = System.getProperty("line.separator");
            if (1 == 1) {
              SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
              mail.setType("text/plain");
              mail.setTo(adminEmails);
              mail.setFrom(prefs.get("ORDER.EMAILADDRESS"));
              mail.addReplyTo(prefs.get("ORDER.EMAILADDRESS"));
              mail.setSubject(prefs.get("TITLE") + " Order #" + orderBean.getId());
              mail.setBody(
                  "A new order has been received." + CRLF +
                      CRLF +
                      orderBean.toString() + CRLF +
                      CRLF +
                      "");
              mail.send();
            }
            // Also send to the user
            if (orderBean.getBilling().getEmail() != null) {
              SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
              mail.setType("text/plain");
              mail.setTo(orderBean.getBilling().getEmail());
              mail.setFrom(prefs.get("ORDER.EMAILADDRESS"));
              mail.addReplyTo(prefs.get("ORDER.EMAILADDRESS"));
              mail.setSubject(prefs.get("TITLE") + " Order #" + orderBean.getId());
              mail.setBody(
                  "Thank you for your order!" + CRLF +
                      CRLF +
                      "Your order has been received with the following details:" + CRLF +
                      CRLF +
                      orderBean.toString() + CRLF +
                      CRLF +
                      "");
              mail.send();
            }
            if (emailAttachments.size() > 0) {
              // Send the email
              SMTPMessage mail = SMTPMessageFactory.createSMTPMessageInstance(prefs.getPrefs());
              mail.setType("text/plain");
              if (orderBean.getContactInformation().getEmail() != null) {
                mail.setTo(orderBean.getContactInformation().getEmail());
              } else if (orderBean.getBilling().getEmail() != null) {
                mail.setTo(orderBean.getBilling().getEmail());
              }
              mail.setFrom(prefs.get("ORDER.EMAILADDRESS"));
              mail.addReplyTo(prefs.get("ORDER.EMAILADDRESS"));
              mail.setSubject(prefs.get("TITLE") + " Registration #" + orderBean.getId());
              mail.setBody(
                  "Thank you for your registration!" + CRLF +
                      CRLF +
                      "The documents you requested from us are attached..." + CRLF +
                      CRLF +
                      "");
              String filePath = this.getPath(context, "projects");
              Iterator i = emailAttachments.iterator();
              while (i.hasNext()) {
                Attachment thisAttachment = (Attachment) i.next();
                mail.addFileAttachment(
                    filePath +
                        getDatePath(thisAttachment.getAttachmentModified()) +
                        thisAttachment.getAttachmentFilename(),
                    thisAttachment.getAttachmentClientFilename());
              }
              int result = mail.send();
              if (result != 0) {
                System.out.println("Order-> Save -- Send attachment result: " + mail.getErrorMsg());
              }
            }
          } catch (Exception e) {
            e.printStackTrace(System.out);
          }
        }
      }
      return "OrderCompleteOK";
    } catch (Exception e) {
      e.printStackTrace(System.out);
    } finally {
      if (db != null) {
        freeConnection(context, db);
      }
    }
    context.getRequest().setAttribute("actionError", "Checkout failed due to a system error");
    return "OrderSystemError";
  }

  public String executeCommandClear(ActionContext context) {
    context.getSession().removeAttribute("orderBean");
    return "OrderError";
  }

  public String executeCommandDownloadAttachment(ActionContext context) {
    Connection db = null;
    boolean allowDownload = false;
    FileItem thisItem = null;
    int userId = -1;
    Exception errorMessage = null;
    try {
      db = getConnection(context);
      // Parse params
      int attachmentId = Integer.parseInt(context.getParameter("id"));
      String orderHash = context.getParameter("hash");
      // Load the attachment
      Attachment thisAttachment = new Attachment(db, attachmentId);
      // Load the product for verification
      Product thisProduct = new Product(db, thisAttachment.getProductId());
      if (thisAttachment.getAllowBeforeCheckout()) {
        // Always allow this file to be downloaded, if the product is enabled
        if (!thisProduct.getEnabled()) {
          return "AttachmentUnavailableError";
        }
        allowDownload = true;
      } else if (thisAttachment.getAllowAfterCheckout()) {
        if (orderHash != null) {
          // Verify the order
          StringTokenizer tokens = new StringTokenizer(orderHash, "-");
          int orderId = Integer.parseInt(tokens.nextToken());
          long orderDate = Long.parseLong(tokens.nextToken());
          OrderRecord thisOrder = new OrderRecord(db, orderId, orderDate);
          if (thisAttachment.isExpired(thisOrder.getEntered().getTime())) {
            return "AttachmentExpiredError";
          }
          // Verify the attachment is part of the order
          ProductList productList = new ProductList();
          productList.setOrderId(orderId);
          productList.setProductId(thisAttachment.getProductId());
          productList.buildList(db);
          if (productList.size() > 0) {
            allowDownload = true;
            userId = thisOrder.getUserId();
          } else {
            return "AttachmentDownloadError";
          }
        } else {
          return "AttachmentDownloadError";
        }
      }
      if (allowDownload) {
        // Load the fileItem for downloading...
        thisItem = new FileItem(db, thisAttachment.getFileId());
      }
    } catch (Exception e) {
			LOG.error("executeCommandDownloadAttachment", e);
    } finally {
      freeConnection(context, db);
    }
    if (thisItem != null) {
      try {
        FileItem itemToDownload = thisItem;
        itemToDownload.setEnteredBy(userId);
        String filePath = this.getPath(context, "projects") + getDatePath(itemToDownload.getModified()) + itemToDownload.getFilename();
        FileDownload fileDownload = new FileDownload();
        fileDownload.setFullPath(filePath);
        fileDownload.setDisplayName(itemToDownload.getClientFilename());
        if (fileDownload.fileExists()) {
          fileDownload.sendFile(context);
          //Get a db connection now that the download is complete
          db = getConnection(context);
          itemToDownload.updateCounter(db);
        }
      } catch (java.net.SocketException se) {
        //User either canceled the download or lost connection
      } catch (Exception e) {
        errorMessage = e;
      } finally {
        this.freeConnection(context, db);
      }
      if (errorMessage == null) {
        return ("-none-");
      } else {
        context.getRequest().setAttribute("Error", errorMessage);
        return ("SystemError");
      }
    }
    return "AttachmentDownloadError";
  }
}


