package com.example.clishortcut;

import com.intellij.openapi.options.Configurable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
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
        return model != null && !sameScripts(CliShortcutSettingsState.getInstance().getScripts(), model.getScripts());
    }

    @Override
    public void apply() {
        if (model != null) {
            CliShortcutSettingsState.getInstance().setScripts(model.getScripts());
        }
    }

    @Override
    public void reset() {
        if (model != null) {
            model.setScripts(copyScripts(CliShortcutSettingsState.getInstance().getScripts()));
        }
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
