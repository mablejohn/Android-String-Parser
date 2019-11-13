package org.rsp.parser.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.rsp.parser.gui.MainDialog
import org.rsp.parser.gui.constant.Constant

class ActionImport : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MainDialog.showDialog(e.project, Constant.ActionMode.IMPORT)
    }
}