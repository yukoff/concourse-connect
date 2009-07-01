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
package com.concursive.connect.web.modules.calendar.utils;

import com.concursive.commons.codec.PrivateString;
import com.concursive.commons.net.HTTPUtils;
import com.concursive.commons.text.StringUtils;
import com.concursive.connect.Constants;
import com.concursive.connect.cache.utils.CacheUtils;
import com.concursive.connect.web.modules.calendar.dao.Meeting;
import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.login.utils.UserUtils;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Accesses the Dimdim server methods
 *
 * @author Nanda kumar
 * @created May 28, 2009
 */
public class DimDimUtils {
  private static Log LOG = LogFactory.getLog(DimDimUtils.class);

  public static final int ACTION_MEETING_STATUS_CHANGE = 0;
  public static final int ACTION_MEETING_DIMDIM_START = 1;
  public static final int ACTION_MEETING_DIMDIM_SCHEDULE = 2;
  public static final int ACTION_MEETING_DIMDIM_EDIT = 3;
  public static final int ACTION_MEETING_DIMDIM_JOIN = 4;
  public static final int ACTION_MEETING_DIMDIM_CANCEL = 5;

  private static final String URL_DIMDIM_START = "StartScheduledMeeting.action";
  private static final String URL_DIMDIM_SCHEDULE = "schedule.action";
  private static final String URL_DIMDIM_EDIT = "EditScheduledMeeting.action";
  private static final String URL_DIMDIM_JOIN = "join.action";

  public static final String ATTENDEES_INVITED = "attendeesInvited";
  public static final String ATTENDEES_ACCEPTED = "attendeesAccepted";
  public static final String ATTENDEES_TENTATIVE = "attendeesTentative";
  public static final String ATTENDEES_DECLINED = "attendeesDeclined";
  public static final String CURRENT_ATTENDEE = "currentAttendee";

  public static final String DIMDIM_CODE_SUCCESS = "200";

  /**
   * Calls Dimdim server methods based on the action set on MeetingInviteesBean
   *
   * @param meetingInviteesBean - meeting parameters to be called are to be set to the class
   * @param attendeeUser        - host or participant based on the meeting action
   * @return - Url to dimdim server or the message returned
   */
  public static HashMap<String, String> processDimdimMeeting(MeetingInviteesBean meetingInviteesBean, User attendeeUser) {

    //return result
    HashMap<String, String> resultMap = new HashMap<String, String>();

    try {
      //get meeting
      Meeting meeting = meetingInviteesBean.getMeeting();

      //get meeting host
      User hostUser = UserUtils.loadUser(meeting.getOwner());

      //comma seperate the attendee mailids for dimdim
      String attendeeMailIds = "";
      Set<User> userSet = meetingInviteesBean.getMembersFoundList().keySet();
      for (User user : userSet) {
        attendeeMailIds += user.getEmail() + ", ";
      }
      for (User user : meetingInviteesBean.getMeetingChangeUsers()) {
        attendeeMailIds += user.getEmail() + ", ";
      }
      attendeeMailIds = trimComma(attendeeMailIds);

      //Modify meeting
      if (meetingInviteesBean.getAction() == ACTION_MEETING_DIMDIM_EDIT) {
        //return result

        //check for meetingId if not present then call schedule meeting
        if (!StringUtils.hasText(meeting.getDimdimMeetingId())) {
          meetingInviteesBean.setAction(ACTION_MEETING_DIMDIM_SCHEDULE);
          resultMap = processDimdimMeeting(meetingInviteesBean, attendeeUser);
          meetingInviteesBean.setAction(ACTION_MEETING_DIMDIM_EDIT);
          return resultMap;
        }

        //set the query string values as params
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", meeting.getDimdimUsername());
        params.put("password", meeting.getDimdimPassword());
        params.put("meetingID", meeting.getDimdimMeetingId());

        SimpleDateFormat dtFormater = new SimpleDateFormat("MMMM dd, yyyy");

        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        if (hostUser.getTimeZone() != null)
          timeZone = TimeZone.getTimeZone(hostUser.getTimeZone());
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(meeting.getStartDate());

        params.put("startDate", dtFormater.format(meeting.getStartDate()));
        params.put("endDate", dtFormater.format(meeting.getEndDate()));
        params.put("startHour", calendar.get(Calendar.HOUR) + "");
        params.put("startMinute", calendar.get(Calendar.MINUTE) + "");
        params.put("timeAMPM", calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        params.put("confname", meeting.getTitle());
        params.put("timezone", timeZone.getID());
        params.put("feedback", hostUser.getEmail());
        params.put("attendees", attendeeMailIds);
        params.put("agenda", meeting.getDescription());
        params.put("response", "json");

        //post to dimdim server and process response
        String urlPrefix = meeting.getDimdimUrl() + URL_DIMDIM_EDIT;
        JSONObject dimdimResp = JSONObject.fromObject(HTTPUtils.post(urlPrefix, params));

        //int resSuccess = dimdimResp.getInt("code");
        String resSuccess = dimdimResp.getString("code");
        String resText = dimdimResp.getJSONObject("data").getString("text");

        //get meetingid if successful
        if (DIMDIM_CODE_SUCCESS.equals(resSuccess)) {
          resultMap.put(resSuccess, meeting.getDimdimMeetingId());
          return resultMap;
        }

        resultMap.put(resSuccess, resText);
        return resultMap;
      }

      //create a new meeting
      if (meetingInviteesBean.getAction() == ACTION_MEETING_DIMDIM_SCHEDULE) {
        //set the query string values as params
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", meeting.getDimdimUsername());
        params.put("password", meeting.getDimdimPassword());

        SimpleDateFormat dtFormater = new SimpleDateFormat("MMMM dd, yyyy");

        TimeZone timeZone = Calendar.getInstance().getTimeZone();
        if (hostUser.getTimeZone() != null)
          timeZone = TimeZone.getTimeZone(hostUser.getTimeZone());
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(meeting.getStartDate());

        params.put("startDate", dtFormater.format(meeting.getStartDate()));
        params.put("endDate", dtFormater.format(meeting.getEndDate()));
        params.put("startHour", calendar.get(Calendar.HOUR) + "");
        params.put("startMinute", calendar.get(Calendar.MINUTE) + "");
        params.put("timeAMPM", calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM");
        params.put("confname", meeting.getTitle());
        params.put("timezone", timeZone.getID());
        params.put("feedback", hostUser.getEmail());
        params.put("attendees", attendeeMailIds);
        params.put("agenda", meeting.getDescription());
        params.put("response", "json");

        //post to dimdim server and process response
        String urlPrefix = meeting.getDimdimUrl() + URL_DIMDIM_SCHEDULE;
        JSONObject dimdimResp = JSONObject.fromObject(HTTPUtils.post(urlPrefix, params));

        //int resSuccess = dimdimResp.getInt("code");
        String resSuccess = dimdimResp.getString("code");
        String resText = dimdimResp.getJSONObject("data").getString("text");

        //get meetingid if successful
        if (DIMDIM_CODE_SUCCESS.equals(resSuccess)) {
          resText = resText.substring(resText.lastIndexOf("is ") + 3);
          resultMap.put(resSuccess, resText);
          return resultMap;
        }

        resultMap.put(resSuccess, resText);
        return resultMap;
      }

      //join an existing meeting
      if (meetingInviteesBean.getAction() == ACTION_MEETING_DIMDIM_JOIN) {
        //set the query string values as params
        Map<String, String> params = new HashMap<String, String>();
        params.put("meetingRoomName", meeting.getDimdimUsername());
        params.put("displayname", attendeeUser.getNameFirstLast());
        params.put("response", "json");

        //post to dimdim server and process response
        String urlPrefix = meeting.getDimdimUrl() + URL_DIMDIM_JOIN;
        JSONObject dimdimResp = JSONObject.fromObject(HTTPUtils.post(urlPrefix, params));
        //int resSuccess = dimdimResp.getInt("code");
        String resSuccess = dimdimResp.getString("code");
        String resText = dimdimResp.getJSONObject("data").getString("text");

        //if successful return dimdim url
        if (DIMDIM_CODE_SUCCESS.equals(resSuccess)) {
          resultMap.put(resSuccess, urlPrefix + buildDimdimUrl(params));
          return resultMap;
        }

        resultMap.put(resSuccess, resText);
        return resultMap;
      }

      //start a meeting
      if (meetingInviteesBean.getAction() == ACTION_MEETING_DIMDIM_START) {
        //set the query string values as params
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", meeting.getDimdimUsername());
        params.put("password", meeting.getDimdimPassword());
        params.put("meetingID", meeting.getDimdimMeetingId());
        params.put("response", "json");

        //post to dimdim server and process response
        String urlPrefix = meeting.getDimdimUrl() + URL_DIMDIM_START;
        JSONObject dimdimResp = JSONObject.fromObject(HTTPUtils.post(urlPrefix, params));

        //int resSuccess = dimdimResp.getInt("code");
        String resSuccess = dimdimResp.getString("code");
        String resText = dimdimResp.getJSONObject("data").getString("text");

        if (DIMDIM_CODE_SUCCESS.equals(resSuccess)) {
          resultMap.put(resSuccess, urlPrefix + buildDimdimUrl(params));
          return resultMap;
        }

        resultMap.put(resSuccess, resText);
        return resultMap;
      }
      LOG.error("Unknown Dimdim meeting senario or action.");
      resultMap.put("0", "Error occured while accessing Dimdim server.");
      return resultMap;
    } catch (Exception e) {
      LOG.error(e.toString());
      resultMap.put("0", "Error occured while accessing Dimdim server.");
      return resultMap;
    }
  }

  // builds a query from the params
  private static String buildDimdimUrl(Map<String, String> param) {
    if (param.size() < 1) {
      return "";
    }

    //query string start char
    String queryString = "?";

    //append name value pair prefixed with amp symbol
    for (String name : param.keySet()) {
      //discard the 'response' param from query string
      if (!"response".equalsIgnoreCase(name)) {
        queryString += "&" + name + "=" + param.get(name);
      }
    }
    return queryString;
  }

  /**
   * Removes leading and trailing commas from a comma seperated string
   *
   * @param in - comma seperated input string
   * @return Returns the string with leading and trailing commas removed. If the input string is empty or null then the same string is returned
   */
  public static String trimComma(String in) {
    // return if empty or null
    if (!StringUtils.hasText(in)) {
      return in;
    }

    //remove leading commas
    in = in.trim();
    while (in.startsWith(",")) {
      in = in.substring(1).trim();
    }

    //remove trailing commas
    while (in.endsWith(",")) {
      in = in.substring(0, in.lastIndexOf(",")).trim();
    }
    return in;
  }

  /**
   * Encrypts data
   *
   * @param password string to be encrypted
   * @return the encrypted password
   */
  public static String encryptData(String password) {
    if (!StringUtils.hasText(password)) {
      return password;
    }
    try {
      // the password needs to be encrypted
      Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_KEY_CACHE);
      Element element = cache.get(1);
      Key key = (Key) element.getObjectValue();
      return PrivateString.encrypt(key, password);
    } catch (Exception e) {
      // the password may not be encypted
      return password;
    }
  }

  /**
   * Decrypts data
   *
   * @param password the encrypted password
   * @return password decrypted. If the param password is empty or null then the return value is the same.
   */
  public static String decryptData(String password) {
    if (!StringUtils.hasText(password)) {
      return password;
    }
    try {
      // the password is encrypted
      Ehcache cache = CacheUtils.getCache(Constants.SYSTEM_KEY_CACHE);
      Element element = cache.get(1);
      Key key = (Key) element.getObjectValue();
      return PrivateString.decrypt(key, password);
    } catch (Exception e) {
      // the password may not be encypted
      return password;
    }
  }
}
