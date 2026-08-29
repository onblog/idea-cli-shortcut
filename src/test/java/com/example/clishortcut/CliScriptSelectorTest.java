package com.example.clishortcut;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CliScriptSelectorTest {
    @Test
    void returnsEnabledNonBlankScriptsInOriginalOrder() {
        List<CliScript> scripts = List.of(
                new CliScript(true, "first", "echo first"),
                new CliScript(false, "disabled", "echo disabled"),
                new CliScript(true, "blank", "   "),
                new CliScript(true, "second", "echo second")
        );

        List<CliScript> selected = CliScriptSelector.enabledRunnableScripts(scripts);

        assertEquals(List.of("echo first", "echo second"),
                selected.stream().map(CliScript::getScript).toList());
    }

    @Test
    void returnsOnlyEnabledNonBlankScriptsFromCommandsToExecute() {
        List<CliScript> scripts = List.of(
                new CliScript(true, "first", "echo first"),
                new CliScript(false, "disabled", "echo disabled"),
                new CliScript(true, "second", "echo second")
        );

        List<String> commands = CliScriptSelector.commandsToExecute(scripts);

        assertEquals(List.of(
                "echo first",
                "echo second"
        ), commands);
    }
}
