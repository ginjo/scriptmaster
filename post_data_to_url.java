SM_PostDataToURL ( url ; data ; path ; type ; accept )

// GroovyScript
// Post URL, get string response to file.
// Data = url-encoded parameter string (user=bill&pass=1234), or xml, yaml, json, etc.
// Params - url, data, path, type, accept.
// Path = OS file path
// Type = request type (text/html, text/xml, text/yaml, text/json, etc...)
// Accept = response expected (pretty much same options as above)
// Type should be "application/x-www-form-urlencoded" for regular Post parameters.


// Don't need URL encoder... yet.
// String data = URLEncoder.encode(dat, "UTF-8");

if (data == null) {data = ""}

// Send data
URL url = new URL(url);
URLConnection conn = url.openConnection();
if (type != null) {conn.setRequestProperty( "content-type", "" + type )}
if (accept != null) {conn.setRequestProperty( "accept", "" + accept )}
conn.setDoOutput(true);
OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
wr.write(data);
wr.flush();

if (path == null) {
	// Get to string
	String response = conn.getInputStream().getText("utf-8");
	wr.close();
	return response;
} else {
	// Get to file
	def file = new FileOutputStream(path);
	def out = new BufferedOutputStream(file);
	out << conn.getInputStream().getText("utf-8");
	out.close();
	wr.close();
}