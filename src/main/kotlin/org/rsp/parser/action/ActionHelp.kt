package org.rsp.parser.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.rsp.parser.gui.SheetDialog

class ActionHelp : AnAction() {
    /**
     * Implement this method to provide your action handler.
     *
     * @param e Carries information on the invocation place
     */
    override fun actionPerformed(e: AnActionEvent) {
        SheetDialog.showDialog(e.project)
    }
}