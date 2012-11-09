<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <br />
    <table width="648" border="0" cellpadding="0" cellspacing="0" style="margin:25px auto; " align="center">
      <tr>
        <td>
          <h1>${site.title?html}</h1>
        </td>
      </tr>
      <tr>
        <td style="background:#fff; padding:20px 20px 0; border-top:25px solid #e9e9e9; border-right:1px solid #e9e9e9; border-left:1px solid #e9e9e9 ">
          <p>Hello ${member.nameFirstLast?html},</p>
        </td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e9e9e9; border-left:1px solid #e9e9e9">
          <p><strong>${user.nameFirstLast?html}</strong> sends the following message to all members of <strong>${project.title?html}</strong>:</p>
          <p style="padding:10px 20px; font-family:sans-serif;">${emailMessage}</p>
          <p>You received this email because you are subscribed to emails for ${project.title?html}.<br /></p>
          <p>You can:<a href="${member.profileUrl}" target="_blank" style="color:#3f86f8">manage your emails</a>, go to <a href="${project.profileUrl}" target="_blank" style="color:#3f86f8">${project.title?html}</a>, or see the profile for <a href="${user.profileUrl}" target="_blank" style="color:#3f86f8">${user.nameFirstLast?html}</a><br /></p>
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
