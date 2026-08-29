package com.example.clishortcut;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.terminal.ui.TerminalWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;

import javax.swing.Timer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class RunScriptsAction extends AnAction {
    private static final int COMMAND_SEND_DELAY_MS = 1000;

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            notify(null, "没有可用项目，无法打开终端。", NotificationType.WARNING);
            return;
        }

        List<String> commands = CliScriptSelector.commandsToExecute(
                CliShortcutSettingsState.getInstance().getScripts()
        );

        try {
            TerminalToolWindowManager terminalManager = TerminalToolWindowManager.getInstance(project);
            ToolWindow terminalWindow = terminalManager.getToolWindow();
            if (terminalWindow != null) {
                terminalWindow.activate(null);
            }

            runCommandsWhenTerminalIsReady(terminalManager, commands);
            createTerminal(terminalManager);
        } catch (RuntimeException exception) {
            notify(project, "打开终端或执行脚本失败：" + exception.getMessage(), NotificationType.ERROR);
        }
    }

    private static void runCommandsWhenTerminalIsReady(TerminalToolWindowManager terminalManager, List<String> commands) {
        AtomicReference<Disposable> setupHandlerDisposable = new AtomicReference<>();
        Disposable disposable = Disposer.newDisposable("CLI Shortcut terminal setup handler");
        setupHandlerDisposable.set(disposable);

        terminalManager.addNewTerminalSetupHandler(terminal -> {
            try {
                sendCommandsSequentially(terminal, commands);
            } finally {
                Disposable handler = setupHandlerDisposable.get();
                if (handler != null) {
                    Disposer.dispose(handler);
                }
            }
        }, disposable);
    }

    private static void sendCommandsSequentially(TerminalWidget terminal, List<String> commands) {
        if (commands.isEmpty()) {
            return;
        }

        terminal.sendCommandToExecute(commands.get(0));
        if (commands.size() == 1) {
            return;
        }

        int[] nextCommandIndex = {1};
        Timer[] timerRef = new Timer[1];
        timerRef[0] = new Timer(COMMAND_SEND_DELAY_MS, event -> {
            terminal.sendCommandToExecute(commands.get(nextCommandIndex[0]));
            nextCommandIndex[0]++;
            if (nextCommandIndex[0] >= commands.size()) {
                timerRef[0].stop();
            }
        });
        timerRef[0].setInitialDelay(COMMAND_SEND_DELAY_MS);
        timerRef[0].start();
    }

    @SuppressWarnings("deprecation")
    private static TerminalWidget createTerminal(TerminalToolWindowManager terminalManager) {
        return terminalManager.createNewSession();
    }

    private static void notify(Project project, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("CLI Shortcut")
                .createNotification(content, type)
                .notify(project);
    }
}
