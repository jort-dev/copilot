package dev.jort.copilot.overlays;

import javax.inject.Inject;

public interface CopilotOverlay {

    void clear();
    void enable();
    void disable();

    void setEnabled(boolean enable);
}
