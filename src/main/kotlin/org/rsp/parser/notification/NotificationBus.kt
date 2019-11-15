@file:Suppress("unused")

package org.rsp.parser.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.rsp.parser.plugin.ArsParserSettings.Companion.PLUGIN_NAME
import javax.swing.event.HyperlinkEvent

class NotificationBus(private val project: Project) {

    private val errors = NotificationGroup.balloonGroup("android_string_parser_error")
    private val notificationListener = NotificationListener { notification: Notification?, event: HyperlinkEvent? -> }
    private val verbos = NotificationGroup.logOnlyGroup("android_string_parser_verbos")

    fun postError(message: String) {
        sendNotification(message, NotificationType.ERROR, errors)
    }

    fun postInfo(message: String) {
        sendNotification(message, NotificationType.INFORMATION, verbos)
    }

    private fun escapeString(string: String): String {
        return string.replace("\n".toRegex(), "\n<br />")
    }

    private fun sendNotification(
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