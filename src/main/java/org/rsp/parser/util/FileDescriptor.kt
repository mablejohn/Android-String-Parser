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
                         title: String = "Choose File", description: String = "") {
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
                           title: String = "Choose Folder", description: String = ""): VirtualFile? {
        return FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                project, null)
    }
}