package org.rsp.parser.helper

import org.apache.xerces.dom.DeferredTextImpl
import org.rsp.parser.model.ResourceFile
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XSSReadWriteXmlFiles {

    /**
     *  Parse the content of the given file as an XML document.
     *
     * [filePath]  A pathname string
     * [expression] The XPath expression.
     *  @return The <code>Object</code> that is the result of evaluating the expression and converting the result to
     *   <code>returnType</code>.
     */
    @Throws(ParserConfigurationException::class,
            SAXException::class,
            IOException::class)
    fun readFile(filePath: String, expression: String): NodeList {

        val document: Document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(File(filePath))

        document.documentElement.normalize()
        return XPathFactory.newInstance().newXPath()
                ?.compile(expression)
                ?.evaluate(document, XPathConstants.NODESET) as NodeList
    }

    @Throws(ParserConfigurationException::class,
            SAXException::class,
            IOException::class)
    fun writeToFile(resourceFile: ResourceFile) {

        val document: Document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(InputSource(resourceFile.xmlFilePath))

        document.documentElement.normalize()
        val xPath = XPathFactory.newInstance().newXPath()

        for (dr in resourceFile.nodeList.indices) {
            val dataRow = resourceFile.nodeList[dr]
            if (dataRow.isSuggestionValid()) {

                val nodeList = xPath.evaluate(
                        xPathItem.format(dataRow.name),
                        document,
                        XPathConstants.NODESET
                ) as NodeList

                for (idx in 0 until nodeList.length) {
                    val nNode: Node = nodeList.item(idx)
                    val eElement = nNode as Element
                    if (null != eElement.firstChild) {
                        (eElement.firstChild as DeferredTextImpl).data = dataRow.suggestion
                    }
                }
            }
        }
        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(document), StreamResult(File(resourceFile.xmlFilePath)))
    }

    companion object {
        const val xPathItem = "descendant-or-self::*[contains(@name,'%s')]"
        /**
         *  The XPath expression.
         */
        const val xPathRoot = "resources/string"
    }
}