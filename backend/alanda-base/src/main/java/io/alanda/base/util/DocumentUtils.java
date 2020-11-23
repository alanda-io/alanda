/**
 * 
 */
package io.alanda.base.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author jlo
 */
public class DocumentUtils {

  public static String getFileName(MultivaluedMap<String, String> header) {

    String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

    for (String filename : contentDisposition) {
      if ((filename.trim().startsWith("filename"))) {

        String[] name = filename.split("=");

        String finalFileName = name[1].trim().replaceAll("\"", "");
        try {
            finalFileName = URLDecoder.decode(finalFileName, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DocumentUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return finalFileName;
      }
    }
    throw new BadRequestException("filename header not found");
  }

  public static MediaType getMediaTypeByFilename(String filename) {
    filename = filename.toLowerCase();
    if (filename.endsWith(".pdf")) {
      return new MediaType("application", "pdf");
    }
    return null;
  }

}
