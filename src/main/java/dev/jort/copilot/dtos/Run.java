package dev.jort.copilot.dtos;

public class Run {
    public static final int AGAIN = 1; //when loop needs to be called again
    public static final int OK = 0; //for onstart
    public static final int ERROR = -3; //when loop errored (unused)
    public static final int DONE = -1; //when loop is done for now and following scripts can run
    public static final int STOP = -2; //when script is done
}
