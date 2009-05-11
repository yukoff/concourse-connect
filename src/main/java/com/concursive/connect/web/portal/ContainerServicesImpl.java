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
package com.concursive.connect.web.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.OptionalContainerServices;
import org.apache.pluto.RequiredContainerServices;
import org.apache.pluto.driver.services.container.PortalCallbackServiceImpl;
import org.apache.pluto.spi.PortalCallbackService;
import org.apache.pluto.spi.optional.*;

import javax.portlet.PortalContext;

/**
 * Custom portal capabilities which hook into Pluto
 *
 * @author matt rajkowski
 * @version $Id$
 * @created Feb 8, 2007
 */
public class ContainerServicesImpl implements RequiredContainerServices, OptionalContainerServices {

  private static Log LOG = LogFactory.getLog(ContainerServicesImpl.class);

  private PortalContextImpl context = new PortalContextImpl();
  private PortalCallbackServiceImpl callbackService = new PortalCallbackServiceImpl();
  private PortletPreferencesServiceImpl preferencesService = new PortletPreferencesServiceImpl();

  public ContainerServicesImpl() {
    LOG.info("Constructor");
  }

  public PortalContext getPortalContext() {
    return context;
  }

  public PortalCallbackService getPortalCallbackService() {
    return callbackService;
  }

  public PortletPreferencesService getPortletPreferencesService() {
    return preferencesService;
  }

  public PortletEnvironmentService getPortletEnvironmentService() {
    return null;
  }

  public PortletRegistryService getPortletRegistryService() {
    return null;
  }

  public PortletInvokerService getPortletInvokerService() {
    return null;
  }

  public PortletInfoService getPortletInfoService() {
    return null;
  }

  public PortalAdministrationService getPortalAdministrationService() {
    return null;
  }

  public UserInfoService getUserInfoService() {
    return null;
  }
}
