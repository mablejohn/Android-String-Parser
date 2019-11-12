package org.rsp.parser.helper

import org.apache.xerces.dom.DeferredAttrImpl
import org.apache.xerces.dom.DeferredTextImpl
import org.rsp.parser.model.ResourceString
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList

class XmlInterpreter {
    /**̥
     * Interpret the given node list.
     */
    fun interpret(nodeList: NodeList): List<ResourceString> {
        val resList = arrayListOf<ResourceString>()
        for (idx in 0 until nodeList.length) {
            val nNode: Node = nodeList.item(idx)

            if (nNode.nodeType == Node.ELEMENT_NODE) {
                val eElement = nNode as Element
                val name = (eElement.attributes.getNamedItem("name") as DeferredAttrImpl).value

                var data = ""
                if (null != eElement.firstChild) {
                    data = (eElement.firstChild as DeferredTextImpl).data
                }
                resList.add(ResourceString(name, data, true))
            }
        }
        return resList
    }
}