package org.rsp.parser.helper

import org.apache.xerces.dom.DeferredAttrImpl
import org.apache.xerces.dom.DeferredElementImpl
import org.apache.xerces.dom.DeferredTextImpl
import org.rsp.parser.helper.XSSReadWriteXmlFiles.Companion.xPathName
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
                val name = (eElement.attributes.getNamedItem(xPathName) as DeferredAttrImpl).value

                var childValue = ""
                val childNodes = eElement.childNodes

                if (childNodes.length == 1 &&
                        null != eElement.firstChild) {
                    childValue = (eElement.firstChild as DeferredTextImpl).data
                } else if (childNodes.length > 1) {
                    try {
                        val stringBuilder = StringBuilder()
                        for (i: Int in 0 until childNodes.length) {
                            val item = childNodes.item(i)
                            stringBuilder.append(((item as DeferredElementImpl).firstChild as DeferredTextImpl).data)
                        }
                        childValue = stringBuilder.toString()
                    } catch (e: Exception) {
                    }
                }
                resList.add(ResourceString(name, childValue, true))
            }
        }
        return resList
    }
}