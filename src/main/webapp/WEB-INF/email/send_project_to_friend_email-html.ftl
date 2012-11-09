<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <br />
    <table width="648" border="0" cellpadding="0" cellspacing="0" style="margin:25px auto;" align="center">
      <tr>
        <td bgcolor="ffffff" style="font-family:Arial, Helvetica, sans-serif; padding:10px">
          Hello,<br /><br/>
          ${sentFromName?html} has sent you a link to the following profile:<br />
          <b>${project.title?html}</b><br />
          ${project.shortDescription?html}<br />
          <address>${project.addressToAndLocation?html}</address><br/>
          See the complete profile at <a href="${link.secureUrl}" target="_blank">${link.secureUrl}</a><br/><br/>
          Message from ${sentFromName?html}:<br />
          ${message?html}<br />
          <p>For more information and to review our privacy and security policies, please visit <a href="${link.siteUrl}" target="_blank">${link.siteUrl}</a></p>
          <br />
        </td>
      </tr>
      <tr>
        <td style="background:#f1f4f8; text-align:right; padding:10px 10px 0 0; ">
          <p style="font-size:smaller">Powered by <a style="color:#3f86f8" href="http://www.concursive.com/show/concourseconnect" title="ConcourseConnect - Overview - Concursive - Business Social Software Platform">ConcourseConnect - Business Social Software</a></p>
          <br/>
        </td>
      </tr>
    </table>
  </body>
</html>