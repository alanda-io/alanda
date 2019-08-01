package io.alanda.base.io;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.alanda.base.listener.OnChangeListener;

public class FileMonitor {

  private final static Logger logger = LoggerFactory.getLogger(FileMonitor.class);

  private final Path fileToMonitor;

  private WatchService watchService;

  private final List<OnChangeListener> listenerList = new ArrayList<>();

  public FileMonitor(Path fileToMonitor) {
    this.fileToMonitor = fileToMonitor; // Check for exists & isFile
  }

  public void start() {
    new Thread(new Runnable() {

      @Override
      public void run() {
        FileMonitor.this.run();
      }
    }).start();
  }

  public void close() throws IOException {
    watchService.close();
  }

  public void addOnChangedListener(OnChangeListener listener) {
    listenerList.add(listener);
  }

  private void run() {
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      this.watchService = watchService;

      Path folder = fileToMonitor.getParent();
      folder.register(watchService, ENTRY_MODIFY);

      while (true) {
        WatchKey take = watchService.take();
        for (WatchEvent<?> event : take.pollEvents()) {
          if (event.kind() == OVERFLOW) {
            continue;
          }
          if (event.kind() == ENTRY_MODIFY) {
            Path modifiedPath = ((WatchEvent<Path>) event).context();
            try {
              if (Files.exists(folder.resolve(modifiedPath)) && Files.isSameFile(folder.resolve(modifiedPath), fileToMonitor)) {
                logger.info(fileToMonitor.getFileName() + " modified, notifying listeners..");
                notifyOnChangedListener();
              }
            } catch (NoSuchFileException ex) {
              // DO NOTHING
            }
          }
        }
        take.reset();
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace(); // Todo restart?
    }
  }

  private void notifyOnChangedListener() {
    for (OnChangeListener listener : listenerList) {
      listener.onChange();
    }
  }

}
