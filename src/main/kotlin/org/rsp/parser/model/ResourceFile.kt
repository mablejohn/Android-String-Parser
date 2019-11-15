package org.rsp.parser.model

data class ResourceFile(
        override var xmlFilePath: String,
        val nodeList: List<ResourceString>
) : IXmlFile