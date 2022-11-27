package dev.jort.copilot;

import lombok.Value;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Value
class Swap {
    private Predicate<String> optionPredicate;
    private Predicate<String> targetPredicate;
    private String swappedOption;
    private Supplier<Boolean> enabled;
    private boolean strict;
}
