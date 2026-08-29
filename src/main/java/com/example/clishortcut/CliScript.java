package com.example.clishortcut;

public class CliScript {
    private boolean enabled;
    private String name;
    private String script;

    public CliScript() {
        this(false, "", "");
    }

    public CliScript(boolean enabled, String name, String script) {
        this.enabled = enabled;
        this.name = name;
        this.script = script;
    }

    public CliScript(CliScript other) {
        this(other.enabled, other.name, other.script);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScript() {
        return script == null ? "" : script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
