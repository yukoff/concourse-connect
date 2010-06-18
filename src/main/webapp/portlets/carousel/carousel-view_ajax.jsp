<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItemList"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="com.concursive.connect.web.modules.documents.dao.FileItem"%>
<%@ page import="com.concursive.commons.text.StringUtils" %>
<%  FileItemList fileItemList = (FileItemList)request.getAttribute("fileItemList");
    Iterator<FileItem> fileItemItr = fileItemList.iterator();
    StringBuffer responseBuffer = new StringBuffer();
    responseBuffer.append("[");
    while (fileItemItr.hasNext()) {
      	FileItem fileItem = fileItemItr.next();
        responseBuffer.append("{");
        responseBuffer.append(" \"uniqueId\" : \"");
        responseBuffer.append(String.valueOf(fileItem.getProject().getUniqueId()));
        responseBuffer.append("\" , \"url\" :\"");
        responseBuffer.append(fileItem.getUrlName(210, 150));
        responseBuffer.append("\" , \"comment\" :\"");
        responseBuffer.append(StringUtils.toHtmlValue(fileItem.getComment()));
        responseBuffer.append("\" , \"name\" :\"");
        responseBuffer.append(StringUtils.toHtmlValue(fileItem.getProject().getTitle()));
        responseBuffer.append("\" ");
        responseBuffer.append(" }");
      	 if (fileItemItr.hasNext()) {
          responseBuffer.append(",");
        }
  	}
    responseBuffer.append("]");
    out.println(responseBuffer.toString());
 %>