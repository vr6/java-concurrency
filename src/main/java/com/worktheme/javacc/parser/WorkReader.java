/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.worktheme.javacc.model.Database;
import com.worktheme.javacc.model.Image;

/**
 * Reads XML data and build the list of images 
 * 
 */
public class WorkReader {
	
	// constants for XML tag names and attributes
	static final String WORK = "work";
	static final String EXIF = "exif";
	static final String URLS = "urls";
	static final String URL = "url";
	static final String MODEL = "model";
	static final String MAKE = "make";
	static final String TYPE = "type";
	static final String URL_THUMB = "small";
	static final String URL_LARGE = "large";

	/**
	 * Uses in-built StAX parser for stream-reading
	 * 
	 * @param worksFile
	 * @param db
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws XMLStreamException
	 */
	public static List<Image> readWorks(String worksFile, Database db)
			throws IOException, SQLException, XMLStreamException {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(worksFile);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		Image image = null;
		List<Image> images = new ArrayList<Image>();

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart() == (WORK)) {
					image = new Image();
				}
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals(MODEL)) {
						event = eventReader.nextEvent();
						image.setModel(event.asCharacters().getData());
						continue;
					}
				}
				if (event.asStartElement().getName().getLocalPart().equals(MAKE)) {
					event = eventReader.nextEvent();
					image.setMake(event.asCharacters().getData());
					continue;
				}
				if (event.asStartElement().getName().getLocalPart().equals(URL)) {
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = event.asStartElement().getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						if (attribute.getName().toString().equals(TYPE)) {
							if (URL_THUMB.equals(attribute.getValue())) {
								event = eventReader.nextEvent();
								image.setThumbURL(event.asCharacters().getData());
							} else if (URL_LARGE.equals(attribute.getValue())) {
								event = eventReader.nextEvent();
								image.setLargeURL(event.asCharacters().getData());
							}
						}
					}
					continue;
				}
			}
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == (WORK)) {
					images.add(image);
				}
			}
		}
		return images;
	}
}