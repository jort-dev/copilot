package dev.jort.copilot.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Heat {
    LOW("Low"),
    MED("Medium"),
    HIGH("High"),
    NONE("Not in range");

    private final String name;
}
