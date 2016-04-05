package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.IOException;

import play.Logger;
import play.libs.Json;
import play.mvc.*;

public class LogController extends Controller {

  private static final Logger.ALogger logger = Logger.of(LogController.class);

  /**
   * Accept a batch of log entries from a machine.
   */
  @BodyParser.Of(value=BodyParser.Raw.class, maxLength=11000000)
  public static Result log(final String machine) throws Exception {
    try {
      // Throw an exception if machine ID is invalid.
      Database.getMachine(machine);

      // Log a "log" event.
      Database.logEvent(machine, "log");

      if (request().body().isMaxSizeExceeded()) {
        throw new Exception("max size exceeded");
      }

      // Access the file that Play used to buffer the content.
      final File file = request().body().asRaw().asFile();

      // Process the file in the background.
      new Thread(new Runnable() {
        @Override
        public void run() {
          // Parse log entries, line by line.
          parseLogEntries(file, machine);

          // Move the whole file to the /tmp folder for upload to S3.
          // (The directory name should be a configuration option...)
          if (!file.renameTo(new File("/tmp", logFileName(machine)))) {
            logger.warn("/log/" + machine + " failed to move"); 
          }
        }
      }).start();

      return ok();
    }
    catch (Exception e) {
      logger.error("/log/" + machine + " error", e); 
      return badRequest();
    }
  }

  private static void parseLogEntries(File file, String machine) {
    try {
      FileReader reader = new FileReader(file);
      try {
        BoundedLineReader lineReader = new BoundedLineReader(new FileReader(file), 2048);
        String line;
        while ((line = lineReader.readLine()) != null) {
          if (line.trim().length() > 0) {   // ignore blank lines
            try {
              JsonNode json = Json.parse(line);
              if (json.get("priority") != null) {
            	  String time = json.get("time").asText();
            	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss,SSS");
            	  Date parsedDate = dateFormat.parse(time);
            	  Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            	  if ("INFO".equals(json.get("priority").textValue())) {
            		  //write to events table
            		  JsonNode jn = json.get("message");
            		  System.out.println(jn);
            		  String machine_id = jn.get("machine_id").asText();
            		  String event_type = jn.get("event").asText();
            		  String product_sku = jn.get("product_id").asText();
            		  Database.logEvent(machine_id, event_type, product_sku, timestamp);
            	  } else if ("DEBUG".equals(json.get("priority").textValue())) {
            		  //write to machine_log table
            		  JsonNode jn = json.get("message");
            		  int machine_id = jn.get("machine_id").asInt();
            		  int traffic = jn.get("traffic").asInt();
            		  int jammed = jn.get("jammed").asInt();
            		  try {
            		      Database.logMachineStatus(machine_id, jammed, traffic, timestamp);
            		  } catch (Exception e) {
            			  logger.error("SQLException: " + e.toString());
            		  }
            	  } else if ("ERROR".equals(json.get("priority").textValue())) {
            		  //write to file for later 
            	  } else if ("WARN".equals(json.get("priority").textValue())) {
            		  //email team to alert
            		  Email.emailLogError(json.get("message").toString());
            	  } else {
            		  //unknown or other priority - TRACE
            	  }
              } else {
            	  //missing priority from json object
              }
            }
            catch (Exception ignore) {
            }
          }
        }
      }
      finally {
        reader.close();
      }
    }
    catch (IOException e) {
      logger.error("/log/" + machine + " error", e); 
    }
  }

  private static String logFileName(String machine) {
    return "bt-" + machine + "-" + Long.toString(System.currentTimeMillis(), 16) + ".log";
  }

  private static class BoundedLineReader {
    private final Reader reader;
    private final int maxLineLength;
    private final StringBuilder buf = new StringBuilder();

    public BoundedLineReader(Reader reader, int maxLineLength) {
      this.reader = reader;
      this.maxLineLength = maxLineLength;
    }

    public String readLine() throws IOException {
      int c;
      while ((c = reader.read()) >= 0) {
        if (buf.length() < maxLineLength) {
          buf.append((char) c);
        }
        if (c == '\n') {
          break;
        }
      }
      if (buf.length() > 0) {
        String str = buf.toString();
        buf.setLength(0);
        return str;
      }
      return null;
    }
  }
}
