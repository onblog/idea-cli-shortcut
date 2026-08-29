package com.example.clishortcut;

import java.util.List;

public final class CliScriptSelector {
    private CliScriptSelector() {
    }

    public static List<CliScript> enabledRunnableScripts(List<CliScript> scripts) {
        return scripts.stream()
                .filter(CliScript::isEnabled)
                .filter(script -> !script.getScript().isBlank())
                .toList();
    }

    public static List<String> commandsToExecute(List<CliScript> scripts) {
        return enabledRunnableScripts(scripts).stream()
                .map(CliScript::getScript)
                .toList();
    }
}
