package io.alanda.base.type;

public enum ProcessResultStatus {
    TECHNICAL_ERROR("Technical Error"),
    PROCESS_ERROR("Process Error");
    
    private String displayName;
    
    private ProcessResultStatus(String displayName) {
      this.displayName = displayName;
    }
    
    public String getDisplayName() {
      return displayName;
    }
}
