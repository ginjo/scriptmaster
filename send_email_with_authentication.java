SM_SendEmailWithAuthentication ( from ; to ; subject ; textBody ; htmlBody ; attachmentPath ; smtpHost ; username ; password ; port ; cc ; bcc )

import javax.mail.*;
import javax.mail.internet.*;

// Don't put comments at top of Groovyscript in 360Works ScriptMaster. (Why not?)
// Sends smtp email, with optional port-user-pass-ssl, includes attachments &
// multipart/alternative text & html.
// Use with Groovyscript in Filemaker's ScriptMaster (from 360Works).
// See http://javamail.kenai.com/nonav/javadocs/javax/mail/internet/MimeMultipart.html
// On SnowLeopard, I had to download JavaMail (javax.mail.jar) into /Library/Java/Extensions.
// Params: from, to, subject, textBody, htmlBody, attachmentPath, smtpHost, username, password, port, cc, bcc.

Properties props = new Properties();
props.setProperty("mail.smtp.host", smtpHost);

if (username && username != "") {
  props.setProperty("mail.smtp.auth", "true");
  props.setProperty("mail.smtps.auth", "true");
}

// main message, including headers
MimeMessage msg = new MimeMessage(Session.getInstance(props));
// main multipart container contains entire message body
Multipart mainContent = new MimeMultipart();
// readable body contains text & html parts
Multipart altContent = new MimeMultipart("alternative");
// body part of mainContent, contains altContent
MimeBodyPart altBody = new MimeBodyPart();

// first message body text
if (textBody && textBody != "") {
  MimeBodyPart bodyPartText = new MimeBodyPart();
  bodyPartText.setContent(textBody, "text/plain");
  altContent.addBodyPart(bodyPartText);
}

// then the message body html
if (htmlBody && htmlBody != "") {
  MimeBodyPart bodyPartHtml = new MimeBodyPart();
  bodyPartHtml.setContent(htmlBody, "text/html");
  altContent.addBodyPart(bodyPartHtml);
}

// add "multipart/alternative" content to altBody
altBody.setContent(altContent);
// add altBody to mainContent
mainContent.addBodyPart(altBody);

// then the attachments
if (attachmentPath && attachmentPath != "") {
  attachmentPath.eachLine{ line ->
    MimeBodyPart attachmentPart = new MimeBodyPart();
    attachmentPart.attachFile(line);
    mainContent.addBodyPart(attachmentPart);
  }
}

msg.setContent(mainContent);
msg.setSubject(subject);
msg.setFrom(new InternetAddress(from));

// handle multiple recipient addresses
if (to && to != "") {
  to.tr(",", "\n").eachLine{line ->
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(line));
  }
}
if (cc && cc != "") {
  cc.tr(",", "\n").eachLine{line ->
    msg.addRecipient(Message.RecipientType.CC, new InternetAddress(line));
  }
}
if (bcc && bcc != "") {
  bcc.tr(",", "\n").eachLine{line ->
    msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(line));
  }
}

if (username && username != "") {
  Transport transport = Session.getDefaultInstance(props).getTransport("smtps");
  // int port = -1; // use the default port
  transport.connect(smtpHost, Integer.parseInt(port), username, password);
  transport.sendMessage(msg, msg.getAllRecipients());
  transport.close();
} else {
  Transport.send(msg);
}

return true