import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;

public class DemacroConfigurableForm {
    JPanel rootPanel;
    EditorEx editor;
    JButton restoreDefaultsButton;
    private JPanel editorPanel;
    JLabel errorLabel;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.setMinimumSize(new Dimension(-1, 300));
        rootPanel.add(editorPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 400), null, 0, false));
        editorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(5, 0, 5, 10), -1, -1));
        rootPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        restoreDefaultsButton = new JButton();
        restoreDefaultsButton.setIcon(new ImageIcon(getClass().getResource("/general/reset.png")));
        restoreDefaultsButton.setText("Restore Defaults");
        panel1.add(restoreDefaultsButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(-44202));
        errorLabel.setText("ERROR");
        panel1.add(errorLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    private void createUIComponents() {
        editorPanel = new JPanel(new BorderLayout());
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document editorDocument = editorFactory.createDocument("");
        editor = (EditorEx) editorFactory.createEditor(editorDocument);
        fillEditorSettings(editor.getSettings());
        setHighlighting(editor);

        editorPanel.add(editor.getComponent(), BorderLayout.CENTER);
    }

    private static void fillEditorSettings(final EditorSettings editorSettings) {
        editorSettings.setWhitespacesShown(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(1);
        editorSettings.setAdditionalLinesCount(1);
        editorSettings.setUseSoftWraps(false);
    }

    private static void setHighlighting(EditorEx editor) {
        final FileType xmlFileType = FileTypeManager.getInstance().getFileTypeByExtension("xml");
        if (xmlFileType == UnknownFileType.INSTANCE) {
            return;
        }
        final EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        final EditorHighlighter editorHighlighter =
                HighlighterFactory.createHighlighter(xmlFileType, scheme, null);
        editor.setHighlighter(editorHighlighter);
    }
}