package org.rsp.parser.gui;

import com.google.api.services.sheets.v4.Sheets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;
import org.rsp.parser.contract.OnActionCompletedListener;
import org.rsp.parser.gui.adapter.ColumnComboBoxModel;
import org.rsp.parser.gui.adapter.ResTableModel;
import org.rsp.parser.gui.constant.Constant;
import org.rsp.parser.helper.ReadWriteExcelFile;
import org.rsp.parser.helper.XSSReadWriteXmlFiles;
import org.rsp.parser.helper.XmlInterpreter;
import org.rsp.parser.model.*;
import org.rsp.parser.notification.NotificationBus;
import org.rsp.parser.sheet.SheetsQuickstart;
import org.rsp.parser.sheet.manage.SpreadSheetManager;
import org.rsp.parser.util.FileDescriptor;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.rsp.parser.plugin.ArsParserSettings.PLUGIN_NAME;
import static org.rsp.parser.util.FileDescriptor.FILE_CHOOSER_DESCRIPTION;
import static org.rsp.parser.util.FileDescriptor.FILE_CHOOSER_TITLE;
import static org.rsp.parser.util.PluginUtilKt.showErrorDialog;

@SuppressWarnings("unused")
public class MainForm extends JPanel implements Consumer<VirtualFile>,
        ResTableModel.ColumnDataChanged {

    private final Project project;
    public JPanel panelRoot;
    private JPanel panelExport;
    private JPanel panelImport;
    private JRadioButton radioExcel;
    private JRadioButton radioWord;
    private JRadioButton radioCsv;
    private JPanel panelBrowse;
    private JTextField textExportSource;
    private JButton buttonExportBrowse;
    private JPanel actionExportPanel;
    private JPanel panelActionButton;
    private JButton buttonExportOK;
    private JButton buttonExportCancel;
    private JPanel panelImportSourceFile;
    private JTextField textImportSource;
    private JButton buttonSourceImportBrowse;
    private JLabel labelSheet;
    private JPanel panelImportDestination;
    private JTextField textImportDestination;
    private JButton buttonImportDestinationBrowse;
    private JPanel panelImportAction;
    private JButton buttonImport;
    private JPanel panelExportDocType;
    private JButton buttonImportCancel;
    private JTabbedPane tabPane;
    private JTable tableResource;
    private JScrollPane scrollPane;
    private JLabel labelKey;
    private JComboBox<String> comboBoxSheet;
    private JComboBox<String> comboBoxValue;
    private JLabel labelSuggestion;
    private JLabel labelValue;
    private JComboBox<String> comboBoxSuggestion;
    private JPanel panelExportDestination;
    private JTextField textExportDestination;
    private JButton buttonBrowseDestination;
    private JPanel panelListRoot;
    private JCheckBox selectAllCheckBox;
    private JComboBox<String> comboBoxKey;
    private List<ResourceString> listResult;
    private Constant.ActionMode actionMode;
    private OnActionCompletedListener listener;
    private NotificationBus notificationBus;

    MainForm(Project project, OnActionCompletedListener listener, Constant.ActionMode actionMode) {

        this.actionMode = actionMode;
        this.project = project;
        this.listener = listener;
        this.notificationBus = new NotificationBus(this.project);

        selectLandingTab();

        addExportActionListener();
        addImportActionListener();

        addTabChangeListener();
        addToGroup();
        radioWord.addActionListener(e -> {
            try {
                Sheets service = SheetsQuickstart.main();
                SpreadSheetManager sheetManager = new SpreadSheetManager(service);
                sheetManager.create("String");
            } catch (IOException | GeneralSecurityException ex) {
                showErrorDialog(project, ex.getMessage(), PLUGIN_NAME);
            }
        });
    }

    public static void showWindow(Project project, OnActionCompletedListener listener) {
        JFrame jFrame = new JFrame("");
        jFrame.setContentPane(new MainForm(project, listener, Constant.ActionMode.EXPORT).panelRoot);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(600, 475));
        jFrame.setResizable(false);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

    private void addToGroup() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(radioExcel);
        buttonGroup.add(radioWord);
        buttonGroup.add(radioCsv);
    }

    private void selectLandingTab() {

        int index = tabPane.getSelectedIndex();
        tabPane.setSelectedIndex(this.actionMode.getIndex());
        tabPane.setEnabledAt(this.actionMode.getIndex(), true);

        if (index == Constant.ActionMode.IMPORT.getIndex()) {
            loadExcelSheet();
            loadKeyColumns();
            loadValueColumns();
            loadSuggestionColumns();
        }
    }

    @Override
    public void consume(VirtualFile virtualFile) {

    }

    private void addExportActionListener() {
        buttonExportBrowse.addActionListener(e -> {
            if (null != project) {
                new FileDescriptor().browseSingleFile(
                        project,
                        virtualFile -> {
                            textExportSource.setText(virtualFile.getPath());
                            listResult = readResourceFile(virtualFile.getPath());
                            changeSelectAllCheckState();
                            populateStringList(listResult);
                        },
                        FILE_CHOOSER_TITLE,
                        FILE_CHOOSER_DESCRIPTION);
            }
        });
        buttonBrowseDestination.addActionListener(e -> {
            if (null != project) {
                VirtualFile virtualFile = new FileDescriptor()
                        .browseSingleFolder(project, FILE_CHOOSER_TITLE, FILE_CHOOSER_DESCRIPTION);
                if (virtualFile != null && virtualFile.isDirectory()) {
                    textExportDestination.setText(virtualFile.getCanonicalPath());
                }
            }
        });
        buttonExportOK.addActionListener(e -> {
            if (validateExport()) {
                exportResourceString();
                listener.onExPortClicked();

            }
        });
        buttonExportCancel.addActionListener(e -> listener.onCancelClicked());
        selectAllCheckBox.addItemListener(e -> {
            boolean isSelected = checkSelectAllState(e);
            for (int i = 0; i < tableResource.getRowCount(); i++) {
                tableResource.getModel().setValueAt(isSelected, i, Constant.TABLE_COLUMN_SELECTED);
            }
        });
    }

    private boolean checkSelectAllState(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            return true;
        }
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            return false;
        }
        return false;
    }

    private void changeSelectAllCheckState() {
        if (listResult.size() > 0) {
            selectAllCheckBox.setSelected(true);
        }
    }

    private void changeSelectAllCheckState(boolean selected) {
        selectAllCheckBox.setSelected(selected);
    }

    private void addImportActionListener() {
        buttonSourceImportBrowse.addActionListener(e -> {
            if (null != project) {
                new FileDescriptor().browseSingleFile(
                        project,
                        virtualFile -> textImportSource.setText(virtualFile.getPath()),
                        FILE_CHOOSER_TITLE, FILE_CHOOSER_DESCRIPTION);
            }
        });
        buttonImportDestinationBrowse.addActionListener(e -> {
            if (null != project) {
                new FileDescriptor().browseSingleFile(
                        project,
                        virtualFile -> textImportDestination.setText(virtualFile.getPath()),
                        FILE_CHOOSER_TITLE, FILE_CHOOSER_DESCRIPTION);
            }
        });
        buttonImport.addActionListener(e -> {
            if (validateImport()) {

                ExcelReadFile readData = new ExcelReadFile(textImportSource.getText());
                readData.setSheetIndex(comboBoxSheet.getSelectedIndex());
                readData.setColumnKeyIndex(comboBoxKey.getSelectedIndex());
                readData.setColumnValueIndex(comboBoxValue.getSelectedIndex());
                readData.setColumnSuggestionIndex(comboBoxSuggestion.getSelectedIndex());

                try {
                    ReadWriteExcelFile rw = new ReadWriteExcelFile();
                    List<ResourceString> strings = rw.readXLSFile(readData);

                    XSSReadWriteXmlFiles xmlReadWrite = new XSSReadWriteXmlFiles();
                    xmlReadWrite.writeToFile(new ResourceFile(textImportDestination.getText(), strings));
                    listener.onImportClicked();
                } catch (IOException | ParserConfigurationException | SAXException ex) {
                    showErrorDialog(project, ex.getMessage(), PLUGIN_NAME);
                    ex.printStackTrace();
                }
            }
        });
        buttonImportCancel.addActionListener(e -> listener.onCancelClicked());
    }

    private void addTabChangeListener() {
        tabPane.addChangeListener(new ChangeListener() {
            /**
             * Invoked when the target of the listener has changed its state.
             *
             * @param e a ChangeEvent object
             */
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabPane.getSelectedIndex();
                if (selectedIndex == Constant.ActionMode.EXPORT.getIndex()) {
                    actionMode = Constant.ActionMode.EXPORT;
                } else {
                    actionMode = Constant.ActionMode.IMPORT;
                    loadExcelSheet();
                    loadKeyColumns();
                    loadValueColumns();
                    loadSuggestionColumns();
                }
            }
        });
    }

    private void exportResourceString() {
        try {
            ReadWriteExcelFile writeExcelFile = new ReadWriteExcelFile();
            String path = textExportDestination.getText() + "/" + Constant.EXCEL_OUT_PATH;
            ExcelWriteFile excelData = new ExcelWriteFile(path, listResult);
            writeExcelFile.writeXLSFile(excelData);
        } catch (IOException e) {
            showErrorDialog(project, e.getMessage(), PLUGIN_NAME);
            e.printStackTrace();
        }
    }

    private void loadExcelSheet() {

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        for (int i = 0; i < 7; i++) {
            comboBoxModel.addElement(Constant.TITLE_SHEET + " " + i);
        }
        comboBoxSheet.setModel(comboBoxModel);
        comboBoxSheet.setSelectedIndex(0);
    }

    private void loadKeyColumns() {
        comboBoxKey.setModel(new ColumnComboBoxModel(Constant.getColumnIndex()));
        comboBoxKey.setSelectedIndex(IExcelFile.COLUMN_KEY_INDEX);
    }

    private void loadValueColumns() {
        comboBoxValue.setModel(new ColumnComboBoxModel(Constant.getColumnIndex()));
        comboBoxValue.setSelectedIndex(IExcelFile.COLUMN_VALUE_INDEX);
    }

    private void loadSuggestionColumns() {
        comboBoxSuggestion.setModel(new ColumnComboBoxModel(Constant.getColumnIndex()));
        comboBoxSuggestion.setSelectedIndex(IExcelFile.COLUMN_SUGGESTION_INDEX);
    }

    private void populateStringList(List<ResourceString> resourceStrings) {

        ResTableModel model = new ResTableModel(this, resourceStrings, null);
        tableResource.setModel(model);
        tableResource.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tableResource.getColumnModel().getColumn(Constant.TABLE_COLUMN_SELECTED).setMaxWidth(Constant.TABLE_COLUMN_KEY_WIDTH);
        tableResource.getColumnModel().getColumn(Constant.TABLE_COLUMN_KEY).setMinWidth(Constant.TABLE_COLUMN_SELECTED_WIDTH);
        tableResource.getColumnModel().getColumn(Constant.TABLE_COLUMN_VALUE).setMinWidth(Constant.TABLE_COLUMN_VALUE_WIDTH);
        tableResource.setPreferredScrollableViewportSize(tableResource.getPreferredSize());
    }

    @Nullable
    private List<ResourceString> readResourceFile(String path) {

        XSSReadWriteXmlFiles xmlReader = new XSSReadWriteXmlFiles();
        try {
            NodeList nodeList = xmlReader.readFile(path, XSSReadWriteXmlFiles.xPathRoot);
            XmlInterpreter interpreter = new XmlInterpreter();
            return interpreter.interpret(nodeList);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            showErrorDialog(project, e.getMessage(), PLUGIN_NAME);
            e.printStackTrace();
        }
        return null;
    }

    private boolean validateImport() {
        if (textImportSource.getText().isEmpty()) {
            //showErrorDialog(project, "Source file path required", PLUGIN_NAME);
            notificationBus.postError("Source file path required");
            return false;
        } else if (textImportDestination.getText().isEmpty()) {
            //showErrorDialog(project, "Destination file path required", PLUGIN_NAME);
            notificationBus.postError("Destination path required");
            return false;
        }
        return true;
    }

    private boolean validateExport() {
        if (textExportSource.getText().isEmpty()) {
            //showErrorDialog(project, "Source file path required", PLUGIN_NAME);
            notificationBus.postError("Source file path required");
            return false;
        } else if (textExportDestination.getText().isEmpty()) {
            //showErrorDialog(project, "Destination path required", PLUGIN_NAME);
            notificationBus.postError("Destination path required");
            return false;
        }
        return true;
    }

    @Override
    public void onColumnDataChanged(boolean checked, int row, int column) {
        /*if (!checked) {
            changeSelectAllCheckState(false);
            return;
        }

        if (null == listResult)
            return;

        boolean isSelectAll = true;
        for (ResourceString resourceString : listResult) {

            if (!resourceString.isSelected()) {
                isSelectAll = false;
                changeSelectAllCheckState(false);
                break;
            }
        }

        if (isSelectAll) {
            changeSelectAllCheckState(true);
        }*/
    }
}
