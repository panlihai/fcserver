package webservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="sAssembly" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sServerCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parameters" type="{YuanYT.RemotingCall}ArrayOfAnyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "sAssembly", "sServerCode", "parameters" })
@XmlRootElement(name = "GetWebData2")
public class GetWebData2 {

	protected String sAssembly;
	protected String sServerCode;
	protected ArrayOfAnyType parameters;

	/**
	 * Gets the value of the sAssembly property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSAssembly() {
		return sAssembly;
	}

	/**
	 * Sets the value of the sAssembly property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSAssembly(String value) {
		this.sAssembly = value;
	}

	/**
	 * Gets the value of the sServerCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSServerCode() {
		return sServerCode;
	}

	/**
	 * Sets the value of the sServerCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSServerCode(String value) {
		this.sServerCode = value;
	}

	/**
	 * Gets the value of the parameters property.
	 * 
	 * @return possible object is {@link ArrayOfAnyType }
	 * 
	 */
	public ArrayOfAnyType getParameters() {
		return parameters;
	}

	/**
	 * Sets the value of the parameters property.
	 * 
	 * @param value
	 *            allowed object is {@link ArrayOfAnyType }
	 * 
	 */
	public void setParameters(ArrayOfAnyType value) {
		this.parameters = value;
	}

}
