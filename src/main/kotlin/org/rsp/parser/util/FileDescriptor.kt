@file:Suppress("UNUSED_PARAMETER")

package org.rsp.parser.util

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer

class FileDescriptor {

    fun browseSingleFile(project: Project,
                         callback: Consumer<VirtualFile>,
                         title: String = FILE_CHOOSER_TITLE, description: String = FILE_CHOOSER_DESCRIPTION) {
        FileChooser.chooseFile(FileChooserDescriptorFactory
                .createSingleFileDescriptor()
                .withTitle(title)
                .withDescription(description),
                project,
                null,
                callback
        )
    }

    fun browseSingleFolder(project: Project,
                           title: String =
                                   FOLDER_CHOOSER_TITLE, description: String = FILE_CHOOSER_DESCRIPTION): VirtualFile? {
        return FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                project, null)
    }

    companion object {
        const val FILE_CHOOSER_TITLE = "Choose File"
        const val FOLDER_CHOOSER_TITLE = "Choose Folder"
        const val FILE_CHOOSER_DESCRIPTION = ""
    }
}