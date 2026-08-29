# IDEA CLI 快捷执行插件实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 开发一个 Java + Gradle 的 IntelliJ IDEA 插件，在顶部工具栏提供一个按钮，点击后打开终端并按顺序执行用户配置的脚本。

**Architecture:** 插件分为配置状态、脚本筛选逻辑、设置页 UI、工具栏动作四部分。可测试的脚本筛选和默认配置逻辑保持为普通 Java 代码，IDE 相关能力只放在 action 和 configurable 中。

**Tech Stack:** Java 17、Gradle 9、IntelliJ Platform Gradle Plugin 2.17.0、JUnit 5、IntelliJ Platform SDK。

---

## 文件结构

- `settings.gradle`：Gradle 插件仓库和项目名。
- `build.gradle`：Java、IntelliJ Platform Gradle Plugin、JUnit、IDE 沙箱运行配置。
- `gradle.properties`：插件版本、IDE 版本、Java 配置。
- `src/main/resources/META-INF/plugin.xml`：插件声明、action 注册、settings configurable 注册。
- `src/main/resources/META-INF/pluginIcon.svg`：插件图标。
- `src/main/resources/icons/cliShortcut.svg`：工具栏 action 图标。
- `src/main/java/com/example/clishortcut/CliScript.java`：单条脚本配置。
- `src/main/java/com/example/clishortcut/CliScriptSelector.java`：筛选可执行脚本。
- `src/main/java/com/example/clishortcut/CliShortcutSettingsState.java`：持久化配置。
- `src/main/java/com/example/clishortcut/CliShortcutConfigurable.java`：设置页。
- `src/main/java/com/example/clishortcut/RunScriptsAction.java`：工具栏按钮动作。
- `src/test/java/com/example/clishortcut/CliScriptSelectorTest.java`：脚本筛选测试。
- `src/test/java/com/example/clishortcut/CliShortcutSettingsStateTest.java`：默认配置和复制测试。

## Task 1: 创建 Gradle 插件项目骨架

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `gradle.properties`
- Create: `src/main/resources/META-INF/plugin.xml`
- Create: `src/main/resources/META-INF/pluginIcon.svg`
- Create: `src/main/resources/icons/cliShortcut.svg`

- [ ] **Step 1: 写 Gradle 配置**

创建 `settings.gradle`：

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        intellijPlatform {
            defaultRepositories()
        }
    }
}

rootProject.name = 'idea-cli-shortcut'
```

创建 `gradle.properties`：

```properties
pluginGroup=com.example
pluginName=CLI Shortcut
pluginVersion=0.1.0
pluginSinceBuild=243
platformType=IC
platformVersion=2024.3.6
org.gradle.jvmargs=-Xmx2g
```

创建 `build.gradle`：

```groovy
plugins {
    id 'java'
    id 'org.jetbrains.intellij.platform' version '2.17.0'
}

group = pluginGroup
version = pluginVersion

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(platformVersion)
        bundledPlugin('org.jetbrains.plugins.terminal')
        instrumentationTools()
        pluginVerifier()
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }

    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.3'
}

intellijPlatform {
    pluginConfiguration {
        name = pluginName
        version = pluginVersion
        ideaVersion {
            sinceBuild = pluginSinceBuild
        }
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = 'UTF-8'
    options.release = 17
}

test {
    useJUnitPlatform()
}
```

- [ ] **Step 2: 写插件声明和图标**

创建 `src/main/resources/META-INF/plugin.xml`：

```xml
<idea-plugin>
    <id>com.example.cli-shortcut</id>
    <name>CLI Shortcut</name>
    <vendor email="support@example.com">Example</vendor>
    <description>Run configured CLI scripts from the IDEA toolbar.</description>

    <depends>com.intellij.modules.platform</depends>
    <depends>org.jetbrains.plugins.terminal</depends>

    <extensions defaultExtensionNs="com.intellij">
        <applicationConfigurable
                instance="com.example.clishortcut.CliShortcutConfigurable"
                displayName="CLI Shortcut"
                parentId="tools"/>
    </extensions>

    <actions>
        <action id="com.example.clishortcut.RunScriptsAction"
                class="com.example.clishortcut.RunScriptsAction"
                text="Run CLI Shortcut"
                description="Run configured CLI scripts"
                icon="/icons/cliShortcut.svg">
            <add-to-group group-id="MainToolbarRight" anchor="last"/>
        </action>
    </actions>
</idea-plugin>
```

创建 `src/main/resources/META-INF/pluginIcon.svg`：

```xml
<svg width="40" height="40" viewBox="0 0 40 40" xmlns="http://www.w3.org/2000/svg">
  <rect x="4" y="6" width="32" height="28" rx="6" fill="#2B2D30"/>
  <path d="M11 15l6 5-6 5" fill="none" stroke="#6BE675" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M21 26h8" stroke="#FFFFFF" stroke-width="3" stroke-linecap="round"/>
</svg>
```

创建 `src/main/resources/icons/cliShortcut.svg`：

```xml
<svg width="16" height="16" viewBox="0 0 16 16" xmlns="http://www.w3.org/2000/svg">
  <path d="M3 4l4 4-4 4" fill="none" stroke="#6E6E6E" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
  <path d="M8.5 12h4" stroke="#6E6E6E" stroke-width="1.8" stroke-linecap="round"/>
</svg>
```

- [ ] **Step 3: 验证骨架能被 Gradle 识别**

Run: `./gradlew tasks`

Expected: Gradle 输出任务列表，包含 `runIde`、`buildPlugin`、`verifyPlugin`。

- [ ] **Step 4: Commit**

```bash
git add settings.gradle build.gradle gradle.properties src/main/resources
git commit -m "chore: scaffold IntelliJ plugin project"
```

## Task 2: 用 TDD 实现脚本配置和筛选逻辑

**Files:**
- Create: `src/test/java/com/example/clishortcut/CliScriptSelectorTest.java`
- Create: `src/test/java/com/example/clishortcut/CliShortcutSettingsStateTest.java`
- Create: `src/main/java/com/example/clishortcut/CliScript.java`
- Create: `src/main/java/com/example/clishortcut/CliScriptSelector.java`
- Create: `src/main/java/com/example/clishortcut/CliShortcutSettingsState.java`

- [ ] **Step 1: 写失败测试**

创建 `CliScriptSelectorTest.java`：

```java
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
}
```

创建 `CliShortcutSettingsStateTest.java`：

```java
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
```

- [ ] **Step 2: 运行测试，确认失败**

Run: `./gradlew test --tests "com.example.clishortcut.*"`

Expected: FAIL，原因是 `CliScript`、`CliScriptSelector`、`CliShortcutSettingsState` 尚不存在。

- [ ] **Step 3: 写最小实现**

创建 `CliScript.java`：

```java
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
```

创建 `CliScriptSelector.java`：

```java
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
}
```

创建 `CliShortcutSettingsState.java`：

```java
package com.example.clishortcut;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Service(Service.Level.APP)
@State(name = "CliShortcutSettings", storages = @Storage("cliShortcut.xml"))
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
```

- [ ] **Step 4: 运行测试，确认通过**

Run: `./gradlew test --tests "com.example.clishortcut.*"`

Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java src/test/java
git commit -m "feat: add script settings model"
```

## Task 3: 实现设置页面

**Files:**
- Create: `src/main/java/com/example/clishortcut/CliShortcutConfigurable.java`

- [ ] **Step 1: 写设置页实现**

创建 `CliShortcutConfigurable.java`：

```java
package com.example.clishortcut;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CliShortcutConfigurable implements Configurable {
    private ScriptTableModel model;
    private JPanel panel;

    @Override
    public String getDisplayName() {
        return "CLI Shortcut";
    }

    @Override
    public @Nullable JComponent createComponent() {
        model = new ScriptTableModel(copyScripts(CliShortcutSettingsState.getInstance().getScripts()));
        JBTable table = new JBTable(model);
        table.setRowHeight(28);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table)
                .setAddAction(button -> model.addScript())
                .setRemoveAction(button -> model.removeScript(table.getSelectedRow()))
                .setMoveUpAction(button -> model.moveScript(table.getSelectedRow(), -1))
                .setMoveDownAction(button -> model.moveScript(table.getSelectedRow(), 1));

        panel = new JPanel(new BorderLayout());
        panel.add(decorator.createPanel(), BorderLayout.CENTER);
        return panel;
    }

    @Override
    public boolean isModified() {
        return !sameScripts(CliShortcutSettingsState.getInstance().getScripts(), model.getScripts());
    }

    @Override
    public void apply() {
        CliShortcutSettingsState.getInstance().setScripts(model.getScripts());
    }

    @Override
    public void reset() {
        model.setScripts(copyScripts(CliShortcutSettingsState.getInstance().getScripts()));
    }

    @Override
    public void disposeUIResources() {
        model = null;
        panel = null;
    }

    private static List<CliScript> copyScripts(List<CliScript> scripts) {
        List<CliScript> copy = new ArrayList<>();
        for (CliScript script : scripts) {
            copy.add(new CliScript(script));
        }
        return copy;
    }

    private static boolean sameScripts(List<CliScript> left, List<CliScript> right) {
        if (left.size() != right.size()) {
            return false;
        }
        for (int i = 0; i < left.size(); i++) {
            CliScript a = left.get(i);
            CliScript b = right.get(i);
            if (a.isEnabled() != b.isEnabled()
                    || !Objects.equals(a.getName(), b.getName())
                    || !Objects.equals(a.getScript(), b.getScript())) {
                return false;
            }
        }
        return true;
    }

    private static final class ScriptTableModel extends AbstractTableModel {
        private final String[] columns = {"启用", "名称", "脚本"};
        private List<CliScript> scripts;

        private ScriptTableModel(List<CliScript> scripts) {
            this.scripts = scripts;
        }

        private List<CliScript> getScripts() {
            return copyScripts(scripts);
        }

        private void setScripts(List<CliScript> scripts) {
            this.scripts = scripts;
            fireTableDataChanged();
        }

        private void addScript() {
            scripts.add(new CliScript(true, "新脚本", "echo \"hello\""));
            fireTableRowsInserted(scripts.size() - 1, scripts.size() - 1);
        }

        private void removeScript(int row) {
            if (row < 0 || row >= scripts.size()) {
                return;
            }
            scripts.remove(row);
            fireTableRowsDeleted(row, row);
        }

        private void moveScript(int row, int direction) {
            int target = row + direction;
            if (row < 0 || row >= scripts.size() || target < 0 || target >= scripts.size()) {
                return;
            }
            CliScript script = scripts.remove(row);
            scripts.add(target, script);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return scripts.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CliScript script = scripts.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> script.isEnabled();
                case 1 -> script.getName();
                case 2 -> script.getScript();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            CliScript script = scripts.get(rowIndex);
            switch (columnIndex) {
                case 0 -> script.setEnabled(Boolean.TRUE.equals(value));
                case 1 -> script.setName(String.valueOf(value));
                case 2 -> script.setScript(String.valueOf(value));
                default -> {
                }
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `./gradlew test`

Expected: PASS。

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/clishortcut/CliShortcutConfigurable.java
git commit -m "feat: add CLI shortcut settings page"
```

## Task 4: 实现工具栏动作和终端执行

**Files:**
- Create: `src/main/java/com/example/clishortcut/RunScriptsAction.java`

- [ ] **Step 1: 写 action 实现**

创建 `RunScriptsAction.java`：

```java
package com.example.clishortcut;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory;
import org.jetbrains.plugins.terminal.TerminalToolWindowManager;
import org.jetbrains.plugins.terminal.ShellTerminalWidget;

import java.util.List;

public final class RunScriptsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            notify(null, "没有可用项目，无法打开终端。", NotificationType.WARNING);
            return;
        }

        List<CliScript> scripts = CliScriptSelector.enabledRunnableScripts(
                CliShortcutSettingsState.getInstance().getScripts()
        );
        if (scripts.isEmpty()) {
            notify(project, "没有已启用的脚本，请到 Settings | Tools | CLI Shortcut 配置。", NotificationType.INFORMATION);
            return;
        }

        try {
            ToolWindow terminalWindow = ToolWindowManager.getInstance(project)
                    .getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID);
            if (terminalWindow != null) {
                terminalWindow.activate(null);
            }

            ShellTerminalWidget terminal = TerminalToolWindowManager.getInstance(project)
                    .createLocalShellWidget(project.getBasePath(), "CLI Shortcut");

            for (CliScript script : scripts) {
                terminal.executeCommand(script.getScript());
            }
        } catch (RuntimeException exception) {
            notify(project, "打开终端或执行脚本失败：" + exception.getMessage(), NotificationType.ERROR);
        }
    }

    private static void notify(Project project, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("CLI Shortcut")
                .createNotification(content, type)
                .notify(project);
    }
}
```

- [ ] **Step 2: 补充通知组声明**

在 `plugin.xml` 的 `<extensions defaultExtensionNs="com.intellij">` 内添加：

```xml
<notificationGroup id="CLI Shortcut" displayType="BALLOON"/>
```

- [ ] **Step 3: 编译验证**

Run: `./gradlew test`

Expected: PASS。如果终端 API 签名在目标 IDE 版本中不同，以编译错误为准，调整为当前 SDK 提供的方法后重新运行。

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/clishortcut/RunScriptsAction.java src/main/resources/META-INF/plugin.xml
git commit -m "feat: run configured scripts from toolbar"
```

## Task 5: 运行插件并做人工验证

**Files:**
- Modify only if verification finds a concrete issue.

- [ ] **Step 1: 启动沙箱 IDE**

Run: `./gradlew runIde`

Expected: 打开一个安装了本插件的沙箱 IDEA。

- [ ] **Step 2: 验证默认行为**

在沙箱 IDEA 中打开任意项目，点击顶部工具栏的 CLI Shortcut 图标。

Expected:

- Terminal 工具窗口打开。
- 终端中出现默认 echo 命令的输出。

- [ ] **Step 3: 验证设置页**

打开 `Settings | Tools | CLI Shortcut`，新增两条启用脚本：

```bash
echo first
```

```bash
echo second
```

再新增一条未启用脚本：

```bash
echo disabled
```

点击工具栏图标。

Expected:

- 终端执行 `echo first`。
- 终端执行 `echo second`。
- 不执行 `echo disabled`。

- [ ] **Step 4: 验证配置持久化**

关闭沙箱 IDEA，再次运行：

```bash
./gradlew runIde
```

Expected: 设置页仍保留之前配置的脚本和启用状态。

- [ ] **Step 5: 打包插件**

Run: `./gradlew buildPlugin`

Expected: `build/distributions/idea-cli-shortcut-0.1.0.zip` 生成。

- [ ] **Step 6: Commit**

如果人工验证发现并修复了问题：

```bash
git add .
git commit -m "fix: polish CLI shortcut plugin behavior"
```

如果没有代码变更，不需要提交。

## Task 6: 最终检查

**Files:**
- Modify only if final checks reveal issues.

- [ ] **Step 1: 运行完整测试**

Run: `./gradlew clean test buildPlugin`

Expected: PASS，并生成插件 zip。

- [ ] **Step 2: 查看 Git 状态**

Run: `git status --short`

Expected: 没有未提交变更，除非 `build/` 目录被 `.gitignore` 忽略。

- [ ] **Step 3: 最终说明**

向用户说明：

- 插件源码已完成。
- 如何运行沙箱 IDE：`./gradlew runIde`。
- 如何打包插件：`./gradlew buildPlugin`。
- 插件 zip 的位置：`build/distributions/idea-cli-shortcut-0.1.0.zip`。
