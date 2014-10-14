package org.elbeesee.supercsv;

/**
 * 
 *  Super CSV library.  
 *  @author Tom Geudens. 2014/10/13.
 *     
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;


/**
 * Processing Imports.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Map;

import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * CSV Freemarker Accessor.
 * Applies a freemarker template to every row of a csv input file 
 * and writes the result to an output file. The first row of the 
 * input file should contain the column names (which can then be 
 * used in the freemarker template).
 * 
 * @param infile
 *            input file:/ resource
 * @param outfile
 *            output file:/ resource
 * @param template
 *            freemarker template, must resolve to 
 *            res:/resources/freemarker/[template].freemarker
 */

public class CSVFreemarkerAccessor extends StandardAccessorImpl {
	public CSVFreemarkerAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("infile", null, null,new Class[] { String.class }));
		this.declareArgument(new SourcedArgumentMetaImpl("outfile", null,null, new Class[] { String.class }));
		this.declareArgument(new SourcedArgumentMetaImpl("template", null,null, new Class[] { String.class }));
		this.declareSourceRepresentation(String.class);		
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		String aInfile = null;
		try {
			aInfile = aContext.source("arg:infile", String.class);
		}
		catch (Exception e){
		  throw new Exception("the request does not have a valid - infile - argument");
		}
		if (! aInfile.startsWith("file:/")) {
		  throw new Exception("the request does not have a valid - infile - argument");
		}

		String aOutfile = null;
		try {
			aOutfile = aContext.source("arg:outfile", String.class);
		}
		catch (Exception e){
		  throw new Exception("the request does not have a valid - outfile - argument");
		}
		if (! aOutfile.startsWith("file:/")) {
		  throw new Exception("the request does not have a valid - outfile - argument");
		}
		
		String aTemplate = null;
		try {
			aTemplate = aContext.source("arg:template", String.class);
		}
		catch (Exception e){
		  throw new Exception("the request does not have a valid - template - argument");
		}
		
		ICsvMapReader vInReader = null;
		BufferedWriter vOutWriter = null;
		
		try {
			File vOutFile = new File(new URI(aOutfile));
			vOutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vOutFile), "UTF8"));
			vInReader = new CsvMapReader(new FileReader((new URI(aInfile)).getRawPath()), CsvPreference.STANDARD_PREFERENCE);
			
            final String[] vHeader = vInReader.getHeader(true);		
			Map<String, String> vCsvMap;
			vCsvMap = vInReader.read(vHeader);
			
			while(vCsvMap != null) {
				INKFRequest freemarkerrequest = aContext.createRequest("active:freemarker");
				freemarkerrequest.addArgument("operator", "res:/resources/freemarker/" + aTemplate + ".freemarker");
				for (Map.Entry<String,String> vCsvEntry : vCsvMap.entrySet()) {
					freemarkerrequest.addArgumentByValue(vCsvEntry.getKey().toUpperCase(), vCsvEntry.getValue());
				}
				freemarkerrequest.setRepresentationClass(String.class);				
				String vOut = (String)aContext.issueRequest(freemarkerrequest);
				vOutWriter.append(vOut).append("\r\n");
				vCsvMap = vInReader.read(vHeader);
			}
		}
		finally {
			if (vInReader != null) {
				vInReader.close();
			}
			if (vOutWriter != null) {
				vOutWriter.flush();
				vOutWriter.close();
			}
		}
		
		INKFResponse vResponse = aContext.createResponseFrom(aInfile + " is processed");
		vResponse.setMimeType("text/plain");
		vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
	}
}
