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

package com.concursive.connect.web.modules.plans.utils;

import com.concursive.connect.web.modules.login.dao.User;
import com.concursive.connect.web.modules.plans.dao.Assignment;
import com.concursive.connect.web.modules.plans.dao.AssignmentFolder;
import com.concursive.connect.web.modules.plans.dao.Requirement;
import net.sf.mpxj.*;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.mpx.MPXReader;
import net.sf.mpxj.mspdi.MSPDIReader;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Imports data from other formats into an outline; adapted from MpxjQuery.java
 *
 * @author matt rajkowski
 * @created September 25, 2007
 */
public class AssignmentMicrosoftProjectImporter {

  public static boolean parse(byte[] buffer, Requirement requirement, Connection db) throws SQLException {
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentMicrosoftProjectImporter-> parse");
    }
    try {
      db.setAutoCommit(false);
      // Determine the correct format
      ProjectFile mpx = null;
      try {
        mpx = new MPXReader().read(new ByteArrayInputStream(buffer));
      }
      catch (Exception ex) {
        mpx = null;
      }

      if (mpx == null) {
        try {
          mpx = new MPPReader().read(new ByteArrayInputStream(buffer));
        }
        catch (Exception ex) {
          mpx = null;
        }
      }

      if (mpx == null) {
        try {
          mpx = new MSPDIReader().read(new ByteArrayInputStream(buffer));
        }
        catch (Exception ex) {
          mpx = null;
        }
      }

      if (mpx == null) {
        throw new Exception("Failed to read file");
      }

      parseProjectHeader(requirement, mpx, db);
      parseTasks(requirement, mpx, db);

      /*
      listResources(mpx);
      listTasks(mpx);
      listAssignments(mpx);
      listAssignmentsByTask(mpx);
      listAssignmentsByResource(mpx);
      listTaskNotes(mpx);
      listResourceNotes(mpx);
      listPredecessors(mpx);
      listSlack(mpx);
      listCalendars(mpx);
      */

      db.commit();
    } catch (Exception e) {
      db.rollback();
      e.printStackTrace(System.out);
      return false;
    } finally {
      db.setAutoCommit(true);
    }
    return true;

  }


  /**
   * Reads basic summary details from the project header.
   *
   * @param file        MPX file
   * @param requirement Requirement object
   */
  private static void parseProjectHeader(Requirement requirement, ProjectFile file, Connection db) throws SQLException {
    ProjectHeader header = file.getProjectHeader();
    if (System.getProperty("DEBUG") != null) {
      System.out.println("AssignmentMicrosoftProjectImporter-> Project Header: StartDate=" + requirement.getStartDate() + " FinishDate=" + requirement.getDeadline());
    }
    requirement.setStartDate(new Timestamp(header.getStartDate().getTime()));
    requirement.setDeadline(new Timestamp(header.getFinishDate().getTime()));
    requirement.update(db);
  }

  /**
   * This method lists all tasks defined in the file in a hierarchical
   * format, reflecting the parent-child relationships between them.
   *
   * @param file MPX file
   */
  private static void parseTasks(Requirement requirement, ProjectFile file, Connection db) throws SQLException {
    for (Task task : file.getChildTasks()) {
      if (!task.getNull()) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("0 Task: " + task.getName());
        }
        // TODO: Some tasks have sub-tasks, so see if this is an assignment
        if (task.getChildTasks().size() > 0) {
          // This is a folder
          AssignmentFolder folder = populateAssignmentFolder(task, requirement, 0, -1);
          folder.insert(db);
          parseChildTasks(requirement, task, db, 0, folder.getId());
        } else {
          Assignment assignment = populateAssignment(task, requirement, db, 0, -1);
          if (!assignment.hasValidTeam(db)) {
            assignment.getAssignedUserList().clear();
          }
          assignment.insert(db);
        }
      }
    }
  }

  /**
   * Helper method called recursively to list child tasks.
   *
   * @param parentTask task whose children are to be displayed
   * @param indent     whitespace used to indent hierarchy levels
   */
  private static void parseChildTasks(Requirement requirement, Task parentTask, Connection db, int indent, int folderId) throws SQLException {
    ++indent;
    for (Task task : parentTask.getChildTasks()) {
      if (!task.getNull()) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println(indent + " Task: " + task.getName());
        }
        if (task.getChildTasks().size() > 0) {
          // This is a folder
          AssignmentFolder folder = populateAssignmentFolder(task, requirement, indent, folderId);
          folder.insert(db);
          parseChildTasks(requirement, task, db, indent, folder.getId());
        } else {
          Assignment assignment = populateAssignment(task, requirement, db, indent, folderId);
          if (!assignment.hasValidTeam(db)) {
            assignment.getAssignedUserList().clear();
          }
          assignment.insert(db);
        }
      }
    }
  }

  private static Assignment populateAssignment(Task task, Requirement requirement, Connection db, int indent, int folderId) throws SQLException {
    Assignment assignment = new Assignment();
    assignment.setProjectId(requirement.getProjectId());
    assignment.setRequirementId(requirement.getId());
    assignment.setIndent(indent);
    assignment.setEnteredBy(requirement.getModifiedBy());
    assignment.setModifiedBy(requirement.getModifiedBy());
    assignment.setFolderId(folderId);

    // Task description
    assignment.setRole(task.getName());
    // Status (Not Started -- default)
    assignment.setStatusId(1);

    Number percentComplete = task.getPercentageComplete();
    assignment.setPercentComplete(percentComplete.intValue());
    if (percentComplete.intValue() > 0) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("   percent: " + percentComplete.toString());
      }
      // In progress
      assignment.setStatusId(2);
      if (percentComplete.intValue() == 100) {
        // Complete
        assignment.setStatusId(5);
      }
    }

    assignment.setPriorityId(2);
    Priority priority = task.getPriority();
    if (priority != null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("   priority: " + priority.getValue());
      }
      if (priority.getValue() > 0 && priority.getValue() < 500) {
        assignment.setPriorityId(1);
      } else if (priority.getValue() > 500) {
        assignment.setPriorityId(3);
      }
    }

    Date estStartDate = task.getStart();
    if (estStartDate != null) {
      assignment.setEstStartDate(new Timestamp(estStartDate.getTime()));
    }
    Date dueDate = task.getFinish();
    if (dueDate != null) {
      assignment.setDueDate(new Timestamp(dueDate.getTime()));
    }
    Duration estimatedDuration = task.getDuration();
    if (estimatedDuration != null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("   estimatedLOE: " + estimatedDuration.toString());
      }
      assignment.setEstimatedLoe(estimatedDuration.toString());
    }

    Date startDate = task.getActualStart();
    if (startDate != null) {
      assignment.setStartDate(new Timestamp(startDate.getTime()));
    }
    Date completeDate = task.getActualFinish();
    if (completeDate != null) {
      assignment.setCompleteDate(new Timestamp(completeDate.getTime()));
    }
    Duration actualDuration = task.getActualDuration();
    if (actualDuration != null) {
      if (System.getProperty("DEBUG") != null) {
        System.out.println("   actualLOE: " + actualDuration.toString());
      }
      assignment.setActualLoe(actualDuration.toString());
    }
    assignment.setAdditionalNote(task.getNotes());

    // Display who is responsible (or add as users)
    StringBuffer assigned = new StringBuffer();
    for (ResourceAssignment resourceAssigned : task.getResourceAssignments()) {
      Resource resource = resourceAssigned.getResource();
      if (!resource.getNull()) {
        if (System.getProperty("DEBUG") != null) {
          System.out.println("   resource");
          System.out.println("    id: " + resource.getUniqueID());
          System.out.println("    name: " + resource.getName());
          System.out.println("    email: " + resource.getEmailAddress());
        }
        boolean match = false;
        // TODO: lookup and associate if a user of the system
        String resourceEmail = resource.getEmailAddress();
        if (resourceEmail != null) {
          int userId = User.getIdByEmailAddress(db, resourceEmail);
          if (userId > -1) {
            assignment.addUser(userId);
            match = true;
          }
        }
        if (!match) {
          String resourceName = resource.getName();
          if (resourceName != null) {
            if (assigned.length() > 0) {
              assigned.append(System.getProperty("line.separator"));
            }
            assigned.append(resourceName);
          }
        }
      }
    }
    if (assigned.length() > 0) {
      assignment.setResponsible(assigned.toString());
    }

    return assignment;
  }

  private static AssignmentFolder populateAssignmentFolder(Task task, Requirement requirement, int indent, int folderId) {
    AssignmentFolder folder = new AssignmentFolder();
    folder.setProjectId(requirement.getProjectId());
    folder.setRequirementId(requirement.getId());
    folder.setParentId(folderId);
    folder.setIndent(indent);
    folder.setEnteredBy(requirement.getModifiedBy());
    folder.setModifiedBy(requirement.getModifiedBy());
    folder.setName(task.getName());
    folder.setDescription(task.getNotes());
    if (task.getResourceAssignments().size() > 0) {
      System.out.println("THIS IS A TASK -- NOT A FOLDER");
    }
    return folder;
  }


  /**
   * This method lists all resources defined.
   *
   * @param file MPX file
   */
  private static void listResources(ProjectFile file) {
    for (Resource resource : file.getAllResources()) {
      System.out.println("Resource: " + resource.getName() + " (Unique ID=" + resource.getUniqueID() + ")");
    }
    System.out.println();
  }


  /**
   * This method lists all resource assignments defined in the file.
   *
   * @param file MPX file
   */
  private static void listAssignments(ProjectFile file) {
    List allAssignments = file.getAllResourceAssignments();
    Iterator iter = allAssignments.iterator();
    ResourceAssignment assignment;
    Task task;
    Resource resource;
    String taskName;
    String resourceName;

    while (iter.hasNext()) {
      assignment = (ResourceAssignment) iter.next();
      task = assignment.getTask();
      if (task == null) {
        taskName = "(null task)";
      } else {
        taskName = task.getName();
      }

      resource = assignment.getResource();
      if (resource == null) {
        resourceName = "(null resource)";
      } else {
        resourceName = resource.getName();
      }

      System.out.println("Assignment: Task=" + taskName + " Resource=" + resourceName);
    }

    System.out.println();
  }

  /**
   * This method displays the resource assignemnts for each task. This time
   * rather than just iterating through the list of all assignments in
   * the file, we extract the assignemnts on a task-by-task basis.
   *
   * @param file MPX file
   */
  private static void listAssignmentsByTask(ProjectFile file) {
    List tasks = file.getAllTasks();
    Iterator taskIter = tasks.iterator();
    Task task;
    List assignments;
    Iterator assignmentIter;
    ResourceAssignment assignment;
    Resource resource;
    String resourceName;

    while (taskIter.hasNext()) {
      task = (Task) taskIter.next();
      System.out.println("Assignments for task " + task.getName() + ":");

      assignments = task.getResourceAssignments();
      assignmentIter = assignments.iterator();

      while (assignmentIter.hasNext()) {
        assignment = (ResourceAssignment) assignmentIter.next();
        resource = assignment.getResource();
        if (resource == null) {
          resourceName = "(null resource)";
        } else {
          resourceName = resource.getName();
        }

        System.out.println("   " + resourceName);
      }
    }

    System.out.println();
  }


  /**
   * This method displays the resource assignemnts for each resource. This time
   * rather than just iterating through the list of all assignments in
   * the file, we extract the assignemnts on a resource-by-resource basis.
   *
   * @param file MPX file
   */
  private static void listAssignmentsByResource(ProjectFile file) {
    List resources = file.getAllResources();
    Iterator taskIter = resources.iterator();
    Resource resource;
    List assignments;
    Iterator assignmentIter;
    ResourceAssignment assignment;
    Task task;

    while (taskIter.hasNext()) {
      resource = (Resource) taskIter.next();
      System.out.println("Assignments for resource " + resource.getName() + ":");

      assignments = resource.getTaskAssignments();
      assignmentIter = assignments.iterator();

      while (assignmentIter.hasNext()) {
        assignment = (ResourceAssignment) assignmentIter.next();
        task = assignment.getTask();
        System.out.println("   " + task.getName());
      }
    }

    System.out.println();
  }


  /**
   * This method lists the predecessors for each task which has
   * predecessors.
   *
   * @param file MPX file
   */
  private static void listPredecessors(ProjectFile file) {
    List tasks = file.getAllTasks();
    Iterator iter = tasks.iterator();
    Task task;
    List predecessors;
    Iterator predecessorIterator;
    Relation relation;

    while (iter.hasNext()) {
      task = (Task) iter.next();
      predecessors = task.getPredecessors();
      if (predecessors != null && !predecessors.isEmpty()) {
        System.out.println(task.getName() + " predecessors:");
        predecessorIterator = predecessors.iterator();
        while (predecessorIterator.hasNext()) {
          relation = (Relation) predecessorIterator.next();
          System.out.println("   Task: " + file.getTaskByUniqueID(relation.getTaskUniqueID()).getName());
          System.out.println("   Type: " + relation.getType());
          System.out.println("   Lag: " + relation.getDuration());
        }
      }
    }
  }

  /**
   * List the slack values for each task.
   *
   * @param file ProjectFile instance
   */
  private static void listSlack(ProjectFile file) {
    List tasks = file.getAllTasks();
    Iterator iter = tasks.iterator();
    Task task;
    List predecessors;
    Iterator predecessorIterator;
    Relation relation;

    while (iter.hasNext()) {
      task = (Task) iter.next();
      System.out.println(task.getName() + " Total Slack=" + task.getTotalSlack() + " Start Slack=" + task.getStartSlack() + " Finish Slack=" + task.getFinishSlack());
    }
  }

  /**
   * List details of all calendars in the file.
   *
   * @param file ProjectFile instance
   */
  private static void listCalendars(ProjectFile file) {
    Iterator iter = file.getBaseCalendars().iterator();
    while (iter.hasNext()) {
      System.out.println(iter.next().toString());
    }

    iter = file.getResourceCalendars().iterator();
    while (iter.hasNext()) {
      System.out.println(iter.next().toString());
    }
  }


}