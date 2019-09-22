package net.otlg.wildloader.plugin;

import net.otlg.wildloader.plugin.mode.ShutdownMode;
import net.otlg.wildloader.plugin.mode.StartMode;

public abstract class WildPlugin {
    private boolean isEnabled = true;
    private WildPluginData data;

    public abstract void onStart(StartMode mode);

    public abstract void onShutdown(ShutdownMode mode);

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }


    public WildPluginData getData() {
        return data;
    }

    /**
     * Use internally to set data, do not use unless if you know what are you doing.
     *
     * @param data
     */
    public void setData(WildPluginData data) {
        this.data = data;
    }

    public WildPluginDescription getDescription() {
        return getData().getDescription();
    }

    public String getName() {
        return getDescription().name;
    }

    public String getVersion() {
        return getDescription().version;
    }

    public String getAuthor() {
        return getDescription().author;
    }
}
