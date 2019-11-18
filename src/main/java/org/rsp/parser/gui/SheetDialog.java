package org.rsp.parser.gui;

import com.google.api.services.sheets.v4.Sheets;
import com.intellij.openapi.project.Project;
import org.rsp.parser.sheet.SheetsQuickstart;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.rsp.parser.plugin.ArsParserSettings.PLUGIN_NAME;
import static org.rsp.parser.util.PluginUtilKt.showErrorDialog;

public class SheetDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton authenticateButton;
    private JTextField textSheetId;
    private JTextArea textAreaResponse;
    private JPanel panelSheet;
    private JLabel labelSheetId;
    private Project project;

    private SheetDialog(Project project) {

        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addActionListener();
        addAuthenticateListener(project);
    }

    public static void showDialog(Project project) {
        SheetDialog dialog = new SheetDialog(project);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void addAuthenticateListener(Project project) {
        authenticateButton.addActionListener(e -> {
            try {
                Sheets service = SheetsQuickstart.authorizeApiClient();
            } catch (IOException | GeneralSecurityException ex) {
                showErrorDialog(project, ex.getMessage(), PLUGIN_NAME);
            }
        });
    }

    private void addActionListener() {

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}