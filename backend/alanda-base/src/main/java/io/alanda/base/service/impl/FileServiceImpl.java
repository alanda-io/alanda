package io.alanda.base.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.service.FileService;

public class FileServiceImpl implements FileService {

  private final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

  @Override
  public File[] listDirectories(File directory) {
    File[] files = directory.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
    if (files == null) {
      throw new IllegalArgumentException("Directory " + directory + " not found");
    }
    return files;
  }

  @Override
  public File[] listFiles(File directory) {
    return directory.listFiles((FileFilter) FileFilterUtils.fileFileFilter());
  }

  @Override
  public void copyDirectory(File srcDir, File destDir) throws IOException {
    logger.debug("try to copy:: " + srcDir.getPath() + " --> " + destDir.getPath());

    String destDirTotalPath = FilenameUtils.concat(destDir.getPath(), srcDir.getName());
    FileUtils.copyDirectory(srcDir, new File(destDirTotalPath));

    logger.debug("successfully copied");
  }

  @Override
  public boolean directoryContainsFile(File directory, String wildCardedFileName) {
    FileFilter fileFilter = new WildcardFileFilter(wildCardedFileName);
    File[] files = directory.listFiles(fileFilter);
    return files != null && files.length > 0;
  }

  @Override
  public boolean existsFile(String path) {
    if (path != null) {
      if (path.contains("*")) {
        File[] matchedFiles = getMatchingFilesForWildcardedFilename(path);
        return matchedFiles != null && matchedFiles.length > 0;
      } else {
        return new File(path).exists();
      }
    }
    return false;
  }

  private File[] getMatchingFilesForWildcardedFilename(String pathToWildcardedFileName) {
    int lastBackSlash = pathToWildcardedFileName.lastIndexOf("\\");
    int lastForwardSlash = pathToWildcardedFileName.lastIndexOf("/");

    int lastSeperator = lastBackSlash > lastForwardSlash ? lastBackSlash : lastForwardSlash;

    String directoryPath = pathToWildcardedFileName.substring(0, lastSeperator);
    String fileName = pathToWildcardedFileName.substring(lastSeperator + 1, pathToWildcardedFileName.length());

    FileFilter fileFilter = new WildcardFileFilter(fileName);
    return new File(directoryPath).listFiles(fileFilter);
  }

  @Override
  public void renameDMSLogFile(String path, String processInstanceId) throws IOException {
    if (path.contains("*")) {
      File[] matchedFiles = getMatchingFilesForWildcardedFilename(path);
      if (matchedFiles == null) {
        throw new IOException("No files match '" + path + "'.");
      } else if (matchedFiles.length > 1) {
        throw new IOException("'" + path + "' matches more than a single file (" + matchedFiles.length + ").");
      }
      path = matchedFiles[0].getAbsolutePath();
    }
    FileUtils.moveFile(new File(path), new File(path + "." + processInstanceId));
    logger.debug(path + " was renamed");
  }

  @Override
  public void writeFile(String path, byte[] content) throws IOException {
    File file = new File(path);
    FileOutputStream fop = null;
    try {
      if ( !file.exists()) {
        File parent = new File(file.getParent());
        if ( !parent.exists()) {
          parent.mkdirs();
        }
        file.createNewFile();
      }

      fop = new FileOutputStream(file);

      fop.write(content);
      fop.flush();
    } catch (IOException ex) {
      logger.warn("Error writing file to path: " + path + " : " + ex.getMessage());
    } finally {
      IOUtils.closeQuietly(fop);
    }
  }

  @Override
  public InputStream getFileInputSteam(String path) throws IOException {
    File file = new File(path);
    return FileUtils.openInputStream(file);
  }

  @Override
  public void deleteFile(String path) throws IOException {
    File file = new File(path);

    if (!file.exists()) {
      logger.warn(String.format("delete file: no file found for path %s", path));
      return;
    }
    if (file.isFile()) {
      boolean success = file.delete();
      logger.info("Deleting file: " + file.getCanonicalPath() + ", success=" + success);
    } else {
      throw new IllegalArgumentException("Can't delete file " + path + ": It's a folder");
    }
  }

  @Override
  public void moveFile(String path, String newPath) throws IOException {
    File file = new File(path);
    if (file.isFile()) {
      File newFile = new File(newPath);
      FileUtils.moveFile(file, newFile);
    } else {
      throw new IllegalArgumentException("Can't move file " + path + ": It isn't a file");
    }
  }

  @Override
  public void zipDirectory(String folderPath, OutputStream output) throws IOException {
    File directory = new File(folderPath);
    zipDirectory(directory, output);
  }

  public void zipDirectory(File directory, File zipFile) throws IOException {
    if (directory.isDirectory()) {
      zipDirectory(directory, new FileOutputStream(zipFile));
    } else {
      throw new IllegalArgumentException("Can't zip directory " + directory.getPath() + ": It isn't a directory");
    }

  }

  public void zipDirectory(File directory, OutputStream outputStream) throws IOException {
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
      processFolder(directory, zipOutputStream, directory.getPath().length() + 1);
    }
  }

  private void processFolder(File directory, ZipOutputStream zipOutputStream, int prefixLength) throws IOException {
    for (File file : directory.listFiles()) {
      if (file.isFile()) {
        ZipEntry zipEntry = new ZipEntry(file.getPath().substring(prefixLength));
        zipOutputStream.putNextEntry(zipEntry);
        try (FileInputStream inputStream = new FileInputStream(file)) {
          IOUtils.copy(inputStream, zipOutputStream);
        }
        zipOutputStream.closeEntry();
      } else if (file.isDirectory()) {
        processFolder(file, zipOutputStream, prefixLength);
      }
    }
  }
}
