<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <table id="Table_01" width="800" border="0" cellpadding="0" cellspacing="0" style="margin:0 auto; border:1px solid #999;" bgcolor="ffffff">
      <#if site.title?has_content>
      <tr>
        <td>
          <h1>${site.title?html}</h1>
        </td>
      </tr>
      </#if>
      <tr>
        <td bgcolor="ffffff" style="font-family:Arial, Helvetica, sans-serif; padding:10px">${customMessage}<br />
          <br />
          <br />
        </td>
      </tr>
    </table>
  </body>
</html>
