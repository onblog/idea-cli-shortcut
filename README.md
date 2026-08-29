# CLI Shortcut for IntelliJ IDEA

## English

CLI Shortcut is a lightweight IntelliJ IDEA plugin for running frequently used command-line scripts directly from the IDE toolbar. It is built for developers who repeatedly open Terminal and type the same project commands during daily work.

Typical use cases include:

- Starting local development services.
- Running build, test, or code generation commands.
- Executing project-specific helper scripts.
- Triggering deployment or packaging shortcuts.
- Replaying common CLI workflows without leaving the editor.

After installation, the plugin adds a fixed icon button to the top-right toolbar area. Clicking the button opens or focuses the built-in IDEA Terminal and sends the enabled scripts configured by the user.

### Features

- Fixed toolbar button in IntelliJ IDEA.
- Opens or focuses the built-in IDEA Terminal.
- Supports multiple configured scripts.
- Runs only enabled, non-blank scripts.
- Sends scripts in the order configured by the user.
- Supports adding, removing, editing, enabling, disabling, moving up, and moving down scripts.
- Keeps Terminal visible so output and errors are easy to inspect.

### Settings

Open the settings page from:

```text
Settings | Tools | CLI Shortcut
```

The settings table contains:

- Enabled: whether the script should run.
- Name: the display name of the script.
- Script: the command or multi-line shell content to send to Terminal.

The default configuration includes an explanatory echo script so the plugin has visible behavior immediately after installation.

### Development Environment

- Java 17.
- Gradle 9.
- IntelliJ IDEA 2024.3.x compatible SDK.

If the default Java version is not 17:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

### Run Sandbox IDE

```bash
./gradlew runIde
```

If the current network cannot access `services.gradle.org`, the first `./gradlew` distribution download may time out. If Gradle is installed through Homebrew, use:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle runIde
```

### Build Plugin

```bash
./gradlew buildPlugin
```

Or use system Gradle:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle buildPlugin
```

The plugin zip is generated at:

```text
build/distributions/idea-cli-shortcut-0.1.3.zip
```

### Verify

```bash
./gradlew clean test buildPlugin
```

Or use system Gradle:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle clean test buildPlugin
```

## 中文

CLI Shortcut 是一个轻量级 IntelliJ IDEA 插件，用来从 IDE 工具栏快速执行常用命令行脚本。它适合开发者在日常工作中反复打开 Terminal 并输入相同项目命令的场景。

典型使用场景包括：

- 启动本地开发服务。
- 运行构建、测试或代码生成命令。
- 执行项目内的辅助脚本。
- 触发部署或打包快捷命令。
- 在不离开编辑器的情况下重复执行常用 CLI 工作流。

插件安装后，会在 IDEA 右上角工具栏添加一个固定图标按钮。点击按钮后，插件会打开或聚焦 IDEA 内置 Terminal，然后发送用户配置的已启用脚本。

### 功能

- IntelliJ IDEA 顶部工具栏固定按钮。
- 打开或聚焦 IDEA 内置 Terminal。
- 支持配置多个脚本。
- 只运行已启用且内容非空的脚本。
- 按用户配置顺序发送脚本。
- 支持新增、删除、编辑、启用、停用、上移、下移脚本。
- 保持 Terminal 可见，方便查看输出和错误。

### 设置

设置入口：

```text
Settings | Tools | CLI Shortcut
```

设置表格包含：

- 启用：控制脚本是否执行。
- 名称：脚本显示名称。
- 脚本：发送到 Terminal 的命令或多行 shell 内容。

默认配置包含一个说明用 echo 脚本，安装后可以立刻看到插件效果。

### 开发环境

- Java 17。
- Gradle 9。
- IntelliJ IDEA 2024.3.x 兼容 SDK。

如果本机默认 Java 不是 17：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"
```

### 运行沙箱 IDE

```bash
./gradlew runIde
```

如果当前网络无法访问 `services.gradle.org`，`./gradlew` 首次下载 Gradle distribution 可能超时。已经通过 Homebrew 安装 Gradle 时，可以改用：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle runIde
```

### 打包插件

```bash
./gradlew buildPlugin
```

或使用系统 Gradle：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle buildPlugin
```

打包后插件 zip 位于：

```text
build/distributions/idea-cli-shortcut-0.1.3.zip
```

### 验证

```bash
./gradlew clean test buildPlugin
```

或使用系统 Gradle：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
gradle clean test buildPlugin
```
