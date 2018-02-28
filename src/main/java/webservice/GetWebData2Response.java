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
 *         &lt;element name="GetWebData2Result" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "getWebData2Result" })
@XmlRootElement(name = "GetWebData2Response")
public class GetWebData2Response {

	@XmlElement(name = "GetWebData2Result")
	protected Object getWebData2Result;

	/**
	 * Gets the value of the getWebData2Result property.
	 * 
	 * @return possible object is {@link Object }
	 * 
	 */
	public Object getGetWebData2Result() {
		return getWebData2Result;
	}

	/**
	 * Sets the value of the getWebData2Result property.
	 * 
	 * @param value
	 *            allowed object is {@link Object }
	 * 
	 */
	public void setGetWebData2Result(Object value) {
		this.getWebData2Result = value;
	}

}
