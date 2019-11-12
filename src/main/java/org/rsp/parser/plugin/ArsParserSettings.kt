package org.rsp.parser.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "ArsParserSettings", storages = [Storage("ArsParserSettings.xml")])
class ArsParserSettings : PersistentStateComponent<ArsParserPluginState> {

    private var pluginState = ArsParserPluginState()

    override fun getState(): ArsParserPluginState? {
        return pluginState
    }

    override fun loadState(state: ArsParserPluginState) {
        this.pluginState = state
    }

    companion object {
        @JvmStatic
        fun getInstance(): PersistentStateComponent<ArsParserPluginState> {
            return ServiceManager.getService(ArsParserSettings::class.java)
        }

        const val PLUGIN_NAME = "ARS-Parser"
    }
}