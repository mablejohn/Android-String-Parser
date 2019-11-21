@file:Suppress("unused")

package org.rsp.parser.notification

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import org.rsp.parser.plugin.ArsParserSettings.Companion.PLUGIN_NAME

object NotificationBus {

    private val errors = NotificationGroup.balloonGroup("android_string_parser_error")
    private val verbose = NotificationGroup.balloonGroup("android_string_parser_verbos")

    @JvmStatic
    fun postError(project: Project, message: String) {
        sendNotification(project, message, NotificationType.ERROR, errors)
    }

    @JvmStatic
    fun postInfo(project: Project, message: String) {
        sendNotification(project, message, NotificationType.INFORMATION, verbose)
    }

    @JvmStatic
    fun postInfo(
            project: Project,
            title: String,
            subtitle: String,
            content: String,
            listener: NotificationListener
    ) {
        sendNotification(project, title, subtitle, content, listener)
    }

    private fun escapeString(string: String): String {
        return string.replace("\n".toRegex(), "\n<br />")
    }

    private fun sendNotification(
            project: Project,
            content: String,
            notificationType: NotificationType,
            notificationGroup: NotificationGroup
    ) {
        notificationGroup.createNotification(
                PLUGIN_NAME,
                escapeString(content),
                notificationType,
                null
        ).notify(project)
    }

    private fun sendNotification(
            project: Project,
            title: String,
            subtitle: String,
            content: String,
            listener: NotificationListener
    ) {
        verbose.createNotification(
                title,
                subtitle,
                content,
                NotificationType.INFORMATION,
                listener
        ).notify(project)
    }
}