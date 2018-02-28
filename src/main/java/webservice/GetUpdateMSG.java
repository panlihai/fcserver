package webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="byteHTLoader" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="sUpdateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sMSGType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "byteHTLoader", "sUpdateTime", "smsgType" })
@XmlRootElement(name = "getUpdateMSG")
public class GetUpdateMSG {

	protected byte[] byteHTLoader;
	protected String sUpdateTime;
	@XmlElement(name = "sMSGType")
	protected String smsgType;

	/**
	 * Gets the value of the byteHTLoader property.
	 * 
	 * @return possible object is byte[]
	 */
	public byte[] getByteHTLoader() {
		return byteHTLoader;
	}

	/**
	 * Sets the value of the byteHTLoader property.
	 * 
	 * @param value
	 *            allowed object is byte[]
	 */
	public void setByteHTLoader(byte[] value) {
		this.byteHTLoader = ((byte[]) value);
	}

	/**
	 * Gets the value of the sUpdateTime property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSUpdateTime() {
		return sUpdateTime;
	}

	/**
	 * Sets the value of the sUpdateTime property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSUpdateTime(String value) {
		this.sUpdateTime = value;
	}

	/**
	 * Gets the value of the smsgType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSMSGType() {
		return smsgType;
	}

	/**
	 * Sets the value of the smsgType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSMSGType(String value) {
		this.smsgType = value;
	}

}
