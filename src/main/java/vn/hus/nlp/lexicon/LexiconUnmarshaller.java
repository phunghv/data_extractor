/**
 * (C) Le Hong Phuong, phuonglh@gmail.com
 */
package vn.hus.nlp.lexicon;

import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import vn.hus.nlp.lexicon.jaxb.Corpus;
import vn.hus.nlp.lexicon.jaxb.ObjectFactory;

/**
 * @author Le Hong Phuong, phuonglh@gmail.com
 *         <p>
 *         A unmarshaller for lexicon.
 */
public class LexiconUnmarshaller {

	JAXBContext jaxbContext;

	Unmarshaller unmarshaller;

	/**
	 * Default constructor.
	 */
	public LexiconUnmarshaller() {
		// create JAXB context
		//
		createContext();
	}

	private void createContext() {
		jaxbContext = null;
		try {
			ClassLoader cl = ObjectFactory.class.getClassLoader();
			jaxbContext = JAXBContext.newInstance(IConstants.PACKAGE_NAME, cl);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the marshaller object.
	 * 
	 * @return the marshaller object
	 */
	protected Unmarshaller getUnmarshaller() {
		if (unmarshaller == null) {
			try {
				// create the unmarshaller
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return unmarshaller;
	}

	/**
	 * Unmarshal a lexicon.
	 * 
	 * @param filename
	 *            a lexicon file
	 * @return a Corpus object.
	 */
	public Corpus unmarshal(String filename) {
		try {
			InputStream stream = getClass().getResourceAsStream(filename);

			URL url = getClass().getResource(filename);
			if (url == null) {
				System.out.println("returl null" + filename);
			} else {
				System.out.println("URL path: " + url.getPath());
			}
			// System.out.println(url.get);

			if (stream != null) {
				Object object = getUnmarshaller().unmarshal(stream);
				if (object instanceof Corpus) {
					Corpus corpus = (Corpus) object;
					return corpus;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

}
