package io.alanda.base.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Lennard Riebandt, lennard.riebandt@iteratec.at
 */
public interface FileService {

  File[] listDirectories(File directory);

  File[] listFiles(File directory);

  /**
   * Copies the directory and it's content to the destination
   * 
   * @param srcDir
   * @param destDir
   * @throws IOException
   */
  void copyDirectory(File srcDir, File destDir) throws IOException;

  boolean directoryContainsFile(File directory, String wildCardedFileName);

  boolean existsFile(String path);

  void renameDMSLogFile(String path, String processInstanceId) throws IOException;

  /**
   * @param path
   * @param content
   * @throws IOException
   */
  void writeFile(String path, byte[] content) throws IOException;

  InputStream getFileInputSteam(String path) throws IOException;

  void deleteFile(String path) throws IOException;

  void moveFile(String path, String newPath) throws IOException;

  void zipDirectory(String folderPath, OutputStream output) throws IOException;
}
