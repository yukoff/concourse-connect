<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <br />
    <table width="648" border="0" cellpadding="0" cellspacing="0" style="margin:25px auto; " align="center">
      <tr>
        <td style="background:#fff; padding:20px 20px 0; border-top:25px solid #e3ebf8; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8 ">
          <p>Hello ${invite.name?html},</p>
        <</td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8">
          <p>${user.nameFirstLast?html} has setup the following profile and is requesting that you join in:</p>
          <blockquote>
            <b>Profile Name:</b> ${project.title?html}<br />
            <b>Description:</b> ${project.shortDescription?html}<br />
          </blockquote>
          <p>Using this web site, you will be able to review important project information.</p>
          <p>To accept the invitation, click on the following link for more information and to register:</p>
          <p><a href="${link.accept}" target="_blank">${link.accept}</a></p>
          <p>If you do not wish to join this profile, and would like to automatically notify ${user.nameFirstLast} with an automated message, click on the following link:</p>
          <a style="color:#3f86f8" href="${link.reject}" target="_blank">${link.reject}</a>
          <blockquote>
          ${optional.message}
          </blockquote>
          <p>For more information, and to review our privacy and security policies, please visit <a href="${link.info}" target="_blank">${link.info}</a></p>
        </td>
      </tr>
      <tr>
        <td style="background#f1f4f8; text-align:right; padding:10px 10px 0 0; ">
          <p style="font-size:smaller">Powered by <a style="color:#3f86f8" href="http://www.concursive.com/show/concourseconnect" title="ConcourseConnect - Overview - Concursive - Business Social Software Platform">ConcourseConnect - Business Social Software</a></p>
          <br/>
        </td>
      </tr>
    </table>
  </body>
</html>