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
 *         &lt;element name="sDataType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sHTLoader" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sHTCorp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sHTFixedCondition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "sDataType", "shtLoader", "shtCorp",
		"shtFixedCondition" })
@XmlRootElement(name = "getEAIWebDataQMYSforJava")
public class GetEAIWebDataQMYSforJava {

	protected String sDataType;
	@XmlElement(name = "sHTLoader")
	protected String shtLoader;
	@XmlElement(name = "sHTCorp")
	protected String shtCorp;
	@XmlElement(name = "sHTFixedCondition")
	protected String shtFixedCondition;

	/**
	 * Gets the value of the sDataType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSDataType() {
		return sDataType;
	}

	/**
	 * Sets the value of the sDataType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSDataType(String value) {
		this.sDataType = value;
	}

	/**
	 * Gets the value of the shtLoader property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSHTLoader() {
		return shtLoader;
	}

	/**
	 * Sets the value of the shtLoader property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSHTLoader(String value) {
		this.shtLoader = value;
	}

	/**
	 * Gets the value of the shtCorp property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSHTCorp() {
		return shtCorp;
	}

	/**
	 * Sets the value of the shtCorp property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSHTCorp(String value) {
		this.shtCorp = value;
	}

	/**
	 * Gets the value of the shtFixedCondition property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSHTFixedCondition() {
		return shtFixedCondition;
	}

	/**
	 * Sets the value of the shtFixedCondition property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSHTFixedCondition(String value) {
		this.shtFixedCondition = value;
	}

}
