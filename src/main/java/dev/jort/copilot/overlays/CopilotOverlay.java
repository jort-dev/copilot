package dev.jort.copilot.overlays;

public interface CopilotOverlay {

    void clear();
    void enable();
    void disable();

    void setEnabled(boolean enable);
}
