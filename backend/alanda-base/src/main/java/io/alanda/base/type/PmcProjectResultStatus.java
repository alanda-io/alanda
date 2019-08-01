package io.alanda.base.type;

public enum PmcProjectResultStatus {
    TECHNICAL_ERROR("Technical Error"),
    PROCESS_ERROR("Process Error");
    
    private String displayName;
    
    private PmcProjectResultStatus(String displayName) {
      this.displayName = displayName;
    }
    
    public String getDisplayName() {
      return displayName;
    }
}
