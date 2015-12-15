SM_ReadMail ( host ; username ; password ; provider ; callbackFile ; callbackScript )

// Read POP3 email with groovy/java for ScriptMaster Filemaker plugin.
// Callback to FMP script with each raw message text.

// Example FM call
// SM_ReadMail( "mail.esalen.org" ; "wbr" ; "xxxxxxx" ; "pop3s" ) & "¶¶" & SMLastStackTrace
// Note that pop3 is port 110 and pop3s is ssl over port 995

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties; // for mail
import java.io.*; // for string replace

// wbr - Original script uses these, but SM would not load with @ in front of class,
// but then didn't recognize classes without @ in front.
// @GrabConfig(systemClassLoader=true)
// @Grapes(
//   @Grab(group='javax.mail', module='mail', version='1.4.4')
// )

// Setup connection
Properties props = new Properties();
//props.setProperty("mail.store.protocol", provider);
//props.setProperty("mail.pop3.host", host);
//props.setProperty("mail.pop3.port", port.toString());

// Connect to the POP3 server
Session session = Session.getDefaultInstance props, null
session.setDebug(true); // Debug... disable for production.
Store store = session.getStore provider
store.connect host, username, password

// Open the folder
Folder inbox = store.getFolder 'INBOX'
if (!inbox) {
  println 'No INBOX'
  System.exit 1
}
inbox.open(Folder.READ_ONLY)

// Get the messages from the server
// def result = ""; // only for concat of all raw messages.
int messageCount = inbox.getMessageCount()
int newMessageCount = inbox.getNewMessageCount()
Message[] messages = inbox.getMessages(1, messageCount)
messages.eachWithIndex { m, i ->
  // Original output
	//println "------------ Message ${i+1} ------------"
  //m.writeTo(System.out)

	// Get raw message to variable
	ByteArrayOutputStream output = new ByteArrayOutputStream();
	m.writeTo(output)
	
	// Simple concat of all messages (make sure to initialze 'result' somewhere above)
  // result += "------------ Message ${i+1} ------------\n"
  // result += output
  // result += "\n\n"

	// Callback to FMP with raw message
	// This substitution removes double line breaks introduced somewhere in FM (or java?).
	// Java string.replace throws compilation error for some reason, thus using fm evaluate.
	def str = fmpro.evaluate('substitute("' + output.toString() + '"; char(10); char(13))')
	fmpro.performScript(callbackFile, callbackScript, str)
}

// Close the connection
// but don't remove the messages from the server
inbox.close false
store.close()

return [messageCount, newMessageCount].toString()
//return result
