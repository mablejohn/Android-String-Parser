@file:Suppress("unused")

package org.rsp.parser.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.rsp.parser.plugin.ArsParserSettings.Companion.PLUGIN_NAME
import javax.swing.event.HyperlinkEvent

object NotificationBus {

    private val errors = NotificationGroup.balloonGroup("android_string_parser_error")
    private val notificationListener = NotificationListener { notification: Notification?, event: HyperlinkEvent? -> }
    private val verbose = NotificationGroup.logOnlyGroup("android_string_parser_verbos")

    @JvmStatic
    fun postError(project: Project, message: String) {
        sendNotification(project, message, NotificationType.ERROR, errors)
    }

    @JvmStatic
    fun postInfo(project: Project, message: String) {
        sendNotification(project, message, NotificationType.INFORMATION, verbose)
    }

    private fun escapeString(string: String): String {
        return string.replace("\n".toRegex(), "\n<br />")
    }

    private fun sendNotification(
            project: Project,
            message: String,
            notificationType: NotificationType?,
            notificationGroup: NotificationGroup
    ) {
        notificationGroup.createNotification(
                PLUGIN_NAME,
                escapeString(message),
                notificationType!!,
                notificationListener
        ).notify(project)
    }
}