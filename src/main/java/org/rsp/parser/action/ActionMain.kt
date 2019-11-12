package org.rsp.parser.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.rsp.parser.gui.MainDialog

class ActionMain : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        MainDialog.showDialog(e.project)
    }
}