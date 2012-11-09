<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <br />
    <table width="648" border="0" cellpadding="0" cellspacing="0" style="margin:25px auto;" align="center">
      <tr>
        <td style="background:#fff; padding:20px 20px 0; border-top:25px solid #e3ebf8; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8 ">
          <p>Hello ${recipient.nameFirstLast?html},</p>
        </td>
      </tr>
      <tr>
        <td style="padding:10px 20px; background:#fff; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8">
          <p>The following blog was posted and sent at your request...</p>
          <blockquote>
            <b>Subject:</b> ${post.subject?html}<br />
            <b>Author:</b> ${author.nameFirstLast?html}<br />
          </blockquote>
          <blockquote>
            ${post.intro}
          </blockquote>
          <#if post.message?has_content>
            <p>View the complete post and add your comments online...</p>
            <#else>
            <p>View and add your comments online...</p>
          </#if>
          <p><a href="${link.post}" target="_blank">${link.post}</a></p>
          <p>
            This email was sent because your user profile for <b>${project.title?html}</b> has notifications enabled.
            To remove yourself, you can access the members page here...
          </p>
          <p><a href="${link.settings}" target="_blank">${link.settings}</a></p>
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