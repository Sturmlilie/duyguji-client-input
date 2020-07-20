package ancurio.duyguji.client.input.api;

public interface DuygujiLogger {
    /**
     * Logging syntax is similar to apache log4j.
     *
     * @param format the format string.
     * @param arg the arguments filling each {@code {}} occurence.
     */
    void log(String format, Object ...arg);
}
