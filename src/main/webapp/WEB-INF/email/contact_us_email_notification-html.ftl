<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <body bgcolor="#f1f4f8" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="background:#f1f4f8; height:100%; width:100%; font-family:Arial, Helvetica, sans-serif;">
    <br />
    <table width="648" border="0" cellpadding="0" cellspacing="0" style="margin:25px auto;" align="center">
      <tr>
        <td style="background:#fff; padding:20px 20px 0; border-top:25px solid #e3ebf8; border-right:1px solid #e2eaf8; border-left:1px solid #e2eaf8 ">
          <p>
            The following information was submitted using the ${form?html} form:
          </p>
          <#if formData?has_content>
            <p>Form Values: ${formData?html}</p>
          </#if>
          <p>
            Comments/Form Filled Out:<br />
            ${description?html}
          </p>
          <p>
            First Name: ${firstname?html}<br />
            Last Name: ${lastname?html}<br />
            Organization: ${organization?html}<br />
            Email Address: ${email?html}<br />
            Phone Number: ${phone?html}
            <#if phoneExt?has_content>
              ext. ${phoneExt?html}
            </#if>
            <br />
            <#if language?has_content>
              Language: ${language?html}<br />
            </#if>
            <#if addressLine1?has_content>
              Address Line 1: ${addressLine1?html}<br />
            </#if>
            <#if addressLine2?has_content>
              Address Line 2: ${addressLine2?html}<br />
            </#if>
            <#if addressLine3?has_content>
              Address Line 3: ${addressLine3?html}<br />
            </#if>
            <#if city?has_content>
              City: ${city?html}<br />
            </#if>
            <#if state?has_content>
              State: ${state?html}<br />
            </#if>
            <#if country?has_content>
              Country: ${country?html}<br />
            </#if>
            <#if postalCode?has_content>
              Postal Code: ${postalCode?html}<br />
            </#if>
          </p>
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
