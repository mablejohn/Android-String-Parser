package org.rsp.parser.model

import java.io.Serializable

/**
 * This class represent attribute string
 */
data class ResourceString(
        override var name: String = "",
        override var data: String = "",
        var isSelected: Boolean = true
) : IResourceFile, Serializable {

    private var _suggestion: String = ""
    /**
     * Optional field
     */
    var suggestion: String = ""
        get() = _suggestion
        set(value) {
            field = value
            _suggestion = value
        }

    fun isSuggestionValid() = _suggestion.isNotEmpty()
}