package org.rsp.parser.gui;

import com.intellij.openapi.project.Project;
import org.rsp.parser.contract.OnActionCompletedListener;
import org.rsp.parser.gui.constant.Constant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainDialog extends JDialog implements OnActionCompletedListener {

    private JPanel contentPane;

    private MainDialog(Project project, Constant.ActionMode actionMode) {
        setContentPane(new MainForm(project, this, actionMode).panelRoot);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void showDialog(Project project, Constant.ActionMode actionMode) {
        MainDialog dialog = new MainDialog(project, actionMode);
        dialog.setSize(new Dimension(600, 475));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    @Override
    public void onExPortClicked() {

    }

    @Override
    public void onImportClicked() {

    }

    @Override
    public void onCancelClicked() {
        onCancel();
    }
}
