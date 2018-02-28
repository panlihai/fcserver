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
 *         &lt;element name="byteHTLoader" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="sCompanyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sAppCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "byteHTLoader", "sCompanyCode", "sAppCode",
		"sYear", "sVersion" })
@XmlRootElement(name = "getDBStructure")
public class GetDBStructure {

	protected byte[] byteHTLoader;
	protected String sCompanyCode;
	protected String sAppCode;
	protected String sYear;
	protected String sVersion;

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
	 * Gets the value of the sCompanyCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSCompanyCode() {
		return sCompanyCode;
	}

	/**
	 * Sets the value of the sCompanyCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSCompanyCode(String value) {
		this.sCompanyCode = value;
	}

	/**
	 * Gets the value of the sAppCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSAppCode() {
		return sAppCode;
	}

	/**
	 * Sets the value of the sAppCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSAppCode(String value) {
		this.sAppCode = value;
	}

	/**
	 * Gets the value of the sYear property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSYear() {
		return sYear;
	}

	/**
	 * Sets the value of the sYear property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSYear(String value) {
		this.sYear = value;
	}

	/**
	 * Gets the value of the sVersion property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSVersion() {
		return sVersion;
	}

	/**
	 * Sets the value of the sVersion property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSVersion(String value) {
		this.sVersion = value;
	}

}
