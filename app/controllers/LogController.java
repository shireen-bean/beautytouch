package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
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
System.out.println(json);
              if (json.get("priority") != null && "INFO".equals(json.get("priority").textValue())) {
System.out.println("info");
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
