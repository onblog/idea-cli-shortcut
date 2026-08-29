package com.example.clishortcut;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.APP)
@com.intellij.openapi.components.State(name = "CliShortcutSettings", storages = @Storage("cliShortcut.xml"))
public final class CliShortcutSettingsState implements PersistentStateComponent<CliShortcutSettingsState.State> {
    private State state = defaultState();

    public static CliShortcutSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CliShortcutSettingsState.class);
    }

    public static State defaultState() {
        State state = new State();
        state.scripts.add(new CliScript(
                true,
                "说明",
                "echo \"CLI Shortcut: configure scripts in Settings | Tools | CLI Shortcut\""
        ));
        return state;
    }

    public static State copyOf(State source) {
        State copy = new State();
        for (CliScript script : source.scripts) {
            copy.scripts.add(new CliScript(script));
        }
        return copy;
    }

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = copyOf(state);
    }

    public List<CliScript> getScripts() {
        return state.scripts;
    }

    public void setScripts(List<CliScript> scripts) {
        State next = new State();
        for (CliScript script : scripts) {
            next.scripts.add(new CliScript(script));
        }
        this.state = next;
    }

    public static class State {
        public List<CliScript> scripts = new ArrayList<>();
    }
}
