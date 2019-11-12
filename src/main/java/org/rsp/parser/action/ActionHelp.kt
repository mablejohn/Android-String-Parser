package org.rsp.parser.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.rsp.parser.gui.MainForm
import org.rsp.parser.gui.OnActionCompletedListener

class ActionHelp : AnAction(), OnActionCompletedListener {

    override fun actionPerformed(e: AnActionEvent) {
        MainForm.showWindow(e.project, this)
    }

    override fun onExPortClicked() {
    }

    override fun onImportClicked() {
    }

    override fun onCancelClicked() {

    }
}