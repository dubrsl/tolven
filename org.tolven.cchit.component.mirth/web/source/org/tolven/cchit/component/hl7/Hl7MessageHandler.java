package org.tolven.cchit.component.hl7;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.tolven.mirth.Response;

import com.lowagie.text.pdf.codec.Base64.OutputStream;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.GenericParser;

public class Hl7MessageHandler {

	private static GenericParser parser;
	
	private static final String eol = "\r";  

	public static Message getMessageFromFilePath(String filePath)
			throws IOException, EncodingNotSupportedException, HL7Exception {
		return getMessage(new File(filePath));
	}

	public static Message getMessage(File file) throws IOException,
			EncodingNotSupportedException, HL7Exception {
		return getMessage(new InputStreamReader(new FileInputStream(file)));
	}

	public static Message getMessage(InputStream is) throws IOException,
			EncodingNotSupportedException, HL7Exception {
		return getMessage(new InputStreamReader(is));
	}

	public static Message getMessage(InputStreamReader isr) throws IOException,
			EncodingNotSupportedException, HL7Exception {
		BufferedReader in;
		String inputLine;
		StringBuffer message = new StringBuffer();
		in = new BufferedReader(isr);
		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			message.append(inputLine+eol);
		}
		return getMessage(message.toString());
	}

	public static Message getMessage(String messageContent)
			throws EncodingNotSupportedException, HL7Exception {
		if (parser == null) {
			parser = new GenericParser();
		}
		return parser.parse(messageContent);
	}

	public static void WriteMessage(Message message, String filePath)
			throws IOException, EncodingNotSupportedException, HL7Exception {
		WriteMessage(message, new File(filePath));
	}

	public static void WriteMessage(Message message, File file)
			throws IOException, EncodingNotSupportedException, HL7Exception {
		WriteMessage(message, new OutputStreamWriter(
				(new FileOutputStream(file))));
	}

	public static void WriteMessage(Message message, OutputStream os)
			throws IOException, EncodingNotSupportedException, HL7Exception {
		WriteMessage(message, new OutputStreamWriter(os));
	}

	public static void WriteMessage(Message message, OutputStreamWriter osr)
			throws IOException, EncodingNotSupportedException, HL7Exception {
	}
}