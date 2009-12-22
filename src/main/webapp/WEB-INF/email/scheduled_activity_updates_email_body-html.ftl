<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <table width="100%" border="0" cellpadding="0" cellspacing="0" style="margin:25px 0 auto auto;" align="center">
      <tr>
        <td style="background:#fff; padding:20px 20px 0; border-top:25px solid #e3ebf8; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8 ">
          <h1>${title?html}</h1>
        </td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8">
          <#list categories?keys as category>
            <h2>${category?html}</h2>
            <#list categories[category]?keys as date>
              <h3>${date}</h3>
              <ul>
                <#assign activities = categories[category][date]>
                <#list activities as activity>
                  <li>${activity}</li>
                </#list>
              </ul>
            </#list>
          </#list>
        </td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8">
          <p>
            This information is sent based on your notification settings.<br />
            <a href="${link.settings}" target="_blank">Manage your notifications.</a>
          </p>
        </td>
      </tr>
      <tr>
        <td style="background#f1f4f8; text-align:right; padding:10px 10px 0 0; ">
          <p style="font-size:smaller">Powered by <a style="color:#3f86f8" href="http://www.concursive.com/show/concourseconnect" title="ConcourseConnect - Overview - Concursive - Business Social Software Platform">ConcourseConnect - Business Social Software</a></p>
        </td>
      </tr>
    </table>
  </body>
</html>