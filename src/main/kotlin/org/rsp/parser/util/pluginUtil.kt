@file:Suppress("unused")

package org.rsp.parser.util

import com.intellij.execution.ExecutionManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.ui.actions.CloseAction
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import java.awt.BorderLayout
import javax.swing.Icon
import javax.swing.JPanel

fun showDialog(
        project: Project?,
        message: String,
        title: String
) {
    Messages.showMessageDialog(project, message, title, Messages.getInformationIcon())
}

fun showErrorDialog(
        project: Project?,
        message: String,
        title: String
) {
    Messages.showMessageDialog(project, message, title, Messages.getErrorIcon())
}

private fun showInConsole(
        message: String,
        consoleTitle: String,
        project: Project,
        contentType: ConsoleViewContentType
) {
    val runnable = {
        val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        console.print(message, contentType)

        val toolbarActions = DefaultActionGroup()
        val consoleComponent = MyConsolePanel(console, toolbarActions)
        val descriptor = object : RunContentDescriptor(console, null, consoleComponent, consoleTitle) {
            override fun isContentReuseProhibited(): Boolean = true
            override fun getIcon(): Icon? = AllIcons.Nodes.Plugin
        }
        val executor = DefaultRunExecutor.getRunExecutorInstance()

        toolbarActions.add(CloseAction(executor, descriptor, project))
        toolbarActions.addAll(*console.createConsoleActions())

        ExecutionManager.getInstance(project).contentManager.showRunContent(executor, descriptor)
    }
    ApplicationManager.getApplication().invokeAndWait(runnable, ModalityState.NON_MODAL)
}

private class MyConsolePanel internal constructor(
        consoleView: ExecutionConsole,
        toolbarActions: ActionGroup
) : JPanel(BorderLayout()) {
    init {
        val toolbarPanel = JPanel(BorderLayout())
        toolbarPanel.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false).component)
        add(toolbarPanel, BorderLayout.WEST)
        add(consoleView.component, BorderLayout.CENTER)
    }
}

