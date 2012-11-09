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
          <p>Hello ${teamMember.firstName?html},</p>
        </td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e9e9e9; border-left:1px solid #e9e9e9">
          <p><strong>${user.nameFirstLastInitial?html}</strong> has sent the following private message to <strong>${project.title?html}</strong>:</p>
          <p style="padding:10px 20px; font-family:sans-serif;">${private.message}</p>
          <p>You are being sent this email because you have access to read and reply to the message at:<br />
            <a style="color:#3f86f8" href="${link.projectMessages}" target="_blank">${link.projectMessages}</a>.</p>
        </td>
      </tr>
      <tr>
        <td style="background:#fff; padding:0 20px 20px; border-bottom:1px solid #e9e9e9; border-right:1px solid #e9e9e9; border-left:1px solid #e9e9e9">
          <hr/>
          <p>For more information and to review our privacy and security policies,
          please visit <a href="${link.info}" target="_blank" style="color:#3f86f8">${link.info}</a></p>
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
