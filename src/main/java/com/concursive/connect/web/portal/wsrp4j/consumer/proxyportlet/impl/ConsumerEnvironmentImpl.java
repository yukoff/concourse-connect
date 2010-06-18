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

package com.concursive.connect.web.portal.wsrp4j.consumer.proxyportlet.impl;

import oasis.names.tc.wsrp.v1.types.StateChange;
import org.apache.wsrp4j.commons.consumer.driver.consumer.GenericConsumerEnvironment;
import org.apache.wsrp4j.commons.consumer.driver.portletdriver.PortletDriverRegistryImpl;
import org.apache.wsrp4j.commons.consumer.driver.urlgenerator.URLRewriterImpl;
import org.apache.wsrp4j.commons.consumer.util.ConsumerConstants;
import org.apache.wsrp4j.commons.util.Constants;
import org.apache.wsrp4j.commons.util.Modes;
import org.apache.wsrp4j.commons.util.WindowStates;
import org.apache.wsrp4j.consumer.proxyportlet.impl.SessionHandlerImpl;
import org.apache.wsrp4j.consumer.proxyportlet.impl.URLTemplateComposerImpl;
import org.apache.wsrp4j.consumer.proxyportlet.impl.UserRegistryImpl;

/**
 * Class implements the consumer environment interface for the Concourse
 * proxy portlet consumer
 *
 * @author Ananth
 * @created Mar 24, 2008
 */
public class ConsumerEnvironmentImpl extends GenericConsumerEnvironment {
  private static String CONSUMER_AGENT = "ConcourseConnect ProxyPortlet Consumer";

  public ConsumerEnvironmentImpl() {
    // set the name of the consumer agent
    setConsumerAgent(CONSUMER_AGENT);

    // define the locales the consumer supports
    String[] supportedLocales = new String[2];
    supportedLocales[0] = Constants.LOCALE_EN_US;
    supportedLocales[1] = Constants.LOCALE_DE_DE;
    setSupportedLocales(supportedLocales);

    // define the modes the consumer supports
    String[] supportedModes = new String[3];
    supportedModes[0] = Modes._view;
    supportedModes[1] = Modes._help;
    supportedModes[2] = Modes._edit;
    setSupportedModes(supportedModes);

    // define the window states the consumer supports
    String[] supportedWindowStates = new String[3];
    supportedWindowStates[0] = WindowStates._normal;
    supportedWindowStates[1] = WindowStates._maximized;
    supportedWindowStates[2] = WindowStates._minimized;
    setSupportedWindowStates(supportedWindowStates);

    // define portlet state change behaviour
    setPortletStateChange(StateChange.readWrite);

    // define the mime types the consumer supports
    setMimeTypes(new String[]{Constants.MIME_TYPE_HTML});

    // define the character sets the consumer supports
    setCharacterEncodingSet(new String[]{Constants.UTF_8});

    // set the authentication method the consumer uses
    setUserAuthentication(ConsumerConstants.NONE);

    // set consumer components
    setUserRegistry(UserRegistryImpl.getInstance());
    setSessionHandler(SessionHandlerImpl.getInstance(this));

    setProducerRegistry(ProducerRegistryImpl.getInstance());
    setPortletRegistry(PortletRegistryImpl.getInstance());

    setTemplateComposer(URLTemplateComposerImpl.getInstance());
    setURLRewriter(URLRewriterImpl.getInstance());

    setPortletDriverRegistry(PortletDriverRegistryImpl.getInstance(this));
  }
}
