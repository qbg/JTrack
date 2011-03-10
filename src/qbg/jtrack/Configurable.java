package qbg.jtrack;

/**
 * Interface for objects supporting configuration
 */
public interface Configurable {
    /**
     * Save all of the settings to a SettingsBuffer
     * @param sb
     */
    void saveSettings(SettingsBuffer sb);
    /**
     * Load all of the settings from a SettingsBuffer
     * @param sb
     */
    void loadSettings(SettingsBuffer sb);
    
    /**
     * Invoke a command on the object
     * @param command The name of the command
     * @param args The arguments to the command
     * @return The return value from executing the command
     */
    Object invokeCommand(String command, Object... args);
}
