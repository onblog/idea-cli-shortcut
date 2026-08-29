package com.example.clishortcut;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliShortcutSettingsStateTest {
    @Test
    void createsDefaultEchoScript() {
        CliShortcutSettingsState.State state = CliShortcutSettingsState.defaultState();

        assertEquals(1, state.scripts.size());
        assertTrue(state.scripts.get(0).isEnabled());
        assertTrue(state.scripts.get(0).getScript().startsWith("echo "));
    }

    @Test
    void copiesStateWithoutSharingScriptInstances() {
        CliShortcutSettingsState.State source = CliShortcutSettingsState.defaultState();

        CliShortcutSettingsState.State copy = CliShortcutSettingsState.copyOf(source);
        copy.scripts.get(0).setScript("echo changed");

        assertEquals("echo \"CLI Shortcut: configure scripts in Settings | Tools | CLI Shortcut\"",
                source.scripts.get(0).getScript());
        assertEquals("echo changed", copy.scripts.get(0).getScript());
    }
}
