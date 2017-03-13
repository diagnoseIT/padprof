//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.01.16 um 05:25:40 PM CET 
//


package call_tree_new;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}node" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="call_tree" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="time_ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="own_time_ms" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "node"
})
@XmlRootElement(name = "node")
public class Node {

    protected List<Node> node;
    @XmlAttribute(name = "call_tree")
    protected String callTree;
    @XmlAttribute(name = "time_ms")
    protected Integer timeMs;
    @XmlAttribute(name = "own_time_ms")
    protected Integer ownTimeMs;

    /**
     * Gets the value of the node property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the node property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Node }
     * 
     * 
     */
    public List<Node> getNode() {
        if (node == null) {
            node = new ArrayList<Node>();
        }
        return this.node;
    }

    /**
     * Ruft den Wert der callTree-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallTree() {
        return callTree;
    }

    /**
     * Legt den Wert der callTree-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallTree(String value) {
        this.callTree = value;
    }

    /**
     * Ruft den Wert der timeMs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTimeMs() {
        return timeMs;
    }

    /**
     * Legt den Wert der timeMs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTimeMs(Integer value) {
        this.timeMs = value;
    }

    /**
     * Ruft den Wert der ownTimeMs-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOwnTimeMs() {
        return ownTimeMs;
    }

    /**
     * Legt den Wert der ownTimeMs-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOwnTimeMs(Integer value) {
        this.ownTimeMs = value;
    }

}
