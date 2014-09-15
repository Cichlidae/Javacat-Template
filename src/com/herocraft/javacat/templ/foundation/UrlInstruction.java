/**
 *   @(#)  UrlInstruction.java	   0.1	 08/02/21
 *
 *   Copyright (C) 2007-2008 HeroCraft, Kaliningrad, Russia.
 *
 *   All rights reserved
 */

package com.herocraft.javacat.templ.foundation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.util.Properties;
import java.util.Enumeration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*****************************************************************************
 * JCT (jct:url) instruction of Javacat Templates
 *
 * @author Amri (Anna A. Semyonova)
 * @version 0.1
 */

public class UrlInstruction implements Instruction {
	
	/* Processes URL instruction */
	public Element process (Element instruction) throws TemplateException {
					
		//local variables	   
		
		URL url;
		Element result = null;	
		XMLData xml = null;
		InputStream schema = null;
		Properties params = new Properties();
		HttpURLConnection con = null;
		int reconnectCount = 0;
		int reconnectDelay = 0;
		boolean errcatch = false;
		
		//code description
		
		if (!"jct:url".equals(instruction.getTagName())) throw new TemplateException("URL Instruction - it's not a <jct:url> instruction.");
		try {
			//параметр обработки ошибок
			errcatch = "true".equals(instruction.getAttribute("error-catching")) ? true : false;
			//считаем в табличку все заданные в шаблоне параметры соединения
			NodeList arglist = instruction.getElementsByTagName("jct:param");
			for (int i = 0; i < arglist.getLength(); i++) {				
				params.setProperty(((Element)arglist.item(i)).getAttribute("id"), ((Element)arglist.item(i)).getAttribute("value"));								
			}		
			//вычленим хост и файл и создадим объект URL			
			if (params.containsKey("host")) {								
				url = new URL(params.getProperty("host"));
				params.remove("host");
			}
			else url = new URL(instruction.getAttribute("host"));			
			if (params.containsKey("file")) {											
				url = new URL(url, params.getProperty("file"));
				params.remove("file");
			}
			else url = new URL(url, instruction.getAttribute("file"));				
			TemplateLogger.getLogger().debug("URL Instruction: url=" + url);
			//вычленим XSD-схему для проверки полученного ответа на соответсвие правилам
			if (params.containsKey("schema")) {
				schema = Configuration.getDataLocator().getDataAsStream(params.getProperty("schema"));		
				params.remove("schema");
			}
			else {
				schema = Configuration.getDataLocator().getDataAsStream(instruction.getAttribute("schema"));		
			}	
			//попробуем получить инфо о количестве возможных попыток соединения с сервером данных
			try {
				if (params.containsKey("reconnectCount")) {
					reconnectCount = Integer.parseInt(params.getProperty("reconnectCount"));
					params.remove("reconnectCount");
				}
				else reconnectCount = Integer.parseInt(instruction.getAttribute("reconnectCount"));
			}
			catch (Exception errx) {};
			reconnectCount++;
			TemplateLogger.getLogger().debug("URL Instruction: reconnectCount=" + reconnectCount);
			//попробуем получить инфо о задержке в мск между попытками соединения
			try {
				if (params.containsKey("reconnectDelay")) {
					reconnectDelay = Integer.parseInt(params.getProperty("reconnectDelay"));
					params.remove("reconnectDelay");
				}
				else reconnectDelay = Integer.parseInt(instruction.getAttribute("reconnectDelay"));
			}
			catch (Exception errx) {};	
			TemplateLogger.getLogger().debug("URL Instruction: reconnectDelay=" + reconnectDelay);
			//осуществляем коннект к серверу данных			
			while (reconnectCount > 0) {
				try {
					con = (HttpURLConnection)url.openConnection();							
					Enumeration e = params.keys();
					while (e.hasMoreElements()) {
						String id = (String)e.nextElement();
						String value = params.getProperty(id);
						con.setRequestProperty(id, value);
						TemplateLogger.getLogger().debug("URL Instruction: " + id + "=" + value);
					}									
					con.setRequestMethod("GET");
					con.setConnectTimeout(0);
					String method = instruction.getAttribute("method");
					if (method != null && "".equals(method.trim())) con.setRequestMethod(method.toUpperCase());
					con.connect();
					xml = new XMLData(con.getInputStream(), schema);
					con.disconnect();
					if (xml != null && xml.getAsXMLDOM() != null) {						
						Element $result = xml.getAsXMLDOM().getDocumentElement();
						result = (Element)instruction.getOwnerDocument().importNode($result, true);
						Node parent = instruction.getParentNode();
						parent.removeChild(instruction);
						parent.appendChild(result);		
					}
					else throw new IOException();	
					break;										
				}
				catch (Exception errx) {											
					reconnectCount--;
					if (reconnectCount == 0) {
						if (errcatch) {
							//ловим ошибку и вместо исключения возвращаем её в виде XML-структуры
							result = this.getError(instruction.getOwnerDocument(), errx.getClass().getName() + ":" + errx.getMessage());
							TemplateLogger.getLogger().warn("URL Instruction: error=" + errx.getClass().getName() + ":" + errx.getMessage());
						}
						else {
							//бросаем исключение дальше
							TemplateLogger.getLogger().error("URL Instruction: error=" + errx.getClass().getName() + ":" + errx.getMessage());
							throw new TemplateException(errx);
						}																	
					}
					else if (reconnectDelay > 0) {
						try {
							Thread.sleep(reconnectDelay);
						}
						catch (Exception errxx) {};
					}					
				}
				finally {
					if (con != null) {
						con.disconnect(); 
						con = null;
					}
				}								
			}								
		}
		catch (MalformedURLException err) {	
			if (errcatch) {
				//ловим ошибку и вместо исключения возвращаем её в виде XML-структуры
				result = this.getError(instruction.getOwnerDocument(), err.getClass().getName() + ":" + err.getMessage());
				TemplateLogger.getLogger().warn("URL Instruction: error=" + err.getClass().getName() + ":" + err.getMessage());
			}
			else {
				//бросаем исключение дальше
				TemplateLogger.getLogger().error("URL Instruction: error=" + err.getClass().getName() + ":" + err.getMessage());
				throw new TemplateException(err);
			}						
		}		
		catch (Exception err) {					
			throw new TemplateException(err);
		}		
		return result;
		
	}
	
	/* Forms error result if url query not successful */
	private Element getError (Document doc, String details) {
		
		//code description
		
		Element error = doc.createElement("error");
		error.setAttribute("details", details);
		return error;
		
	}
	
}
