package ancurio.duyguji.client.input.api;

/**
 * Logging interface with similar semantics to apache log4j.
 * The module may need to log warnings or errors, and will do so
 * through this interface if provided.
 */
public interface DuygujiLogger {
    /**
     * Write a line to the debug output.
     *
     * @param format the format string.
     * @param arg the arguments filling each {@code {}} occurence.
     */
    void log(String format, Object ...arg);
}
