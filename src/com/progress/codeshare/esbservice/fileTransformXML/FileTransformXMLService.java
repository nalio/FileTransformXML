package com.progress.codeshare.esbservice.fileTransformXML;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.sonicsw.xq.XQConstants;
import com.sonicsw.xq.XQEnvelope;
import com.sonicsw.xq.XQInitContext;
import com.sonicsw.xq.XQLog;
import com.sonicsw.xq.XQMessage;
import com.sonicsw.xq.XQMessageException;
import com.sonicsw.xq.XQParameterInfo;
import com.sonicsw.xq.XQParameters;
import com.sonicsw.xq.XQPart;
import com.sonicsw.xq.XQServiceContext;
import com.sonicsw.xq.XQServiceEx;
import com.sonicsw.xq.XQServiceException;

public class FileTransformXMLService implements XQServiceEx {

	// This is the XQLog (the container's logging mechanism).
	private XQLog m_xqLog = null;

	// This is the the log prefix that helps identify this service during
	// logging
	private String m_logPrefix = "";

	// These hold version information.
	private static int s_major = 5;

	private static int s_minor = 3;

	private static int s_buildNumber = 0;

	private static final String PARAM_NAME_FILE_NAME = "FILE_NAME";

	private static final String PARAM_NAME_FILE_TYPE = "FILE_TYPE";

	private static final String PARAM_NAME_FILE_DIRECTORY = "FILE_DIRECTORY";

	private static final String PARAM_NAME_FILE_SEPARATOR = "FILE_SEPARATOR";

	private static final String PARAM_NAME_FILE_ENCONDING = "FILE_ENCONDING";

	private static final String PARAM_NAME_MAX_EXECUTION = "MAX_EXECUTION";

	private static final String PARAM_NAME_INTERVAL_EXECUTION = "INTERVAL_EXECUTION";

	private String m_fileEncoding = "UTF-8";

	private long m_intervalExecution = 60000;

	private int m_maxExecution = 5;

	FileInputStream fis = null;

	/**
	 * Constructor for a FileTransformXMLService
	 */
	public FileTransformXMLService() {
	}

	/**
	 * Initialize the XQService by processing its initialization parameters.
	 * 
	 * <p>
	 * This method implements a required XQService method.
	 * 
	 * @param initialContext
	 *            The Initial Service Context provides access to:<br>
	 *            <ul>
	 *            <li>The configuration parameters for this instance of the
	 *            FileTransformXMLService.</li>
	 *            <li>The XQLog for this instance of the
	 *            FileTransformXMLService.</li>
	 *            </ul>
	 * @exception XQServiceException
	 *                Used in the event of some error.
	 */
	public void init(XQInitContext initialContext) throws XQServiceException {
		XQParameters params = initialContext.getParameters();
		m_xqLog = initialContext.getLog();
		setLogPrefix(params);
		m_xqLog.logInformation(m_logPrefix + " Initializing ...");

		writeStartupMessage(params);
		writeParameters(params);
		// perform initilization work.
		m_fileEncoding = params.getParameter(PARAM_NAME_FILE_ENCONDING,
				XQConstants.PARAM_STRING, m_fileEncoding);
		m_intervalExecution = params.getLongParameter(
				PARAM_NAME_INTERVAL_EXECUTION, XQConstants.PARAM_STRING);
		m_maxExecution = params.getIntParameter(PARAM_NAME_MAX_EXECUTION,
				XQConstants.PARAM_STRING);
		m_xqLog.logInformation(m_logPrefix + " Initialized ...");
	}

	/**
	 * Handle the arrival of XQMessages in the INBOX.
	 * 
	 * <p>
	 * This method implement a required XQService method.
	 * 
	 * @param ctx
	 *            The service context.
	 * @exception XQServiceException
	 *                Thrown in the event of a processing error.
	 */
	public void service(XQServiceContext ctx) throws XQServiceException {
		m_xqLog.logInformation(m_logPrefix + "Service processing...");

		if (ctx == null)
			throw new XQServiceException("Service Context cannot be null.");
		else {
			fileTransformXMLServiceContext(ctx);
		}

		m_xqLog.logInformation(m_logPrefix + "Service processed...");
	}

	/**
	 * Clean up and get ready to destroy the service.
	 * 
	 * <p>
	 * This method implement a required XQService method.
	 */
	public void destroy() {
		m_xqLog.logInformation(m_logPrefix + "Destroying...");
		m_xqLog.logInformation(m_logPrefix + "Destroyed...");
	}

	/**
	 * Called by the container on container start.
	 * 
	 * <p>
	 * This method implement a required XQServiceEx method.
	 */
	public void start() {
		m_xqLog.logInformation(m_logPrefix + "Starting...");
		m_xqLog.logInformation(m_logPrefix + "Started...");
	}

	/**
	 * Called by the container on container stop.
	 * 
	 * <p>
	 * This method implement a required XQServiceEx method.
	 */
	public void stop() {
		m_xqLog.logInformation(m_logPrefix + "Stopping...");
		m_xqLog.logInformation(m_logPrefix + "Stopped...");
	}

	/**
	 * Clean up and get ready to destroy the service.
	 * 
	 */
	protected void setLogPrefix(XQParameters params) {
		String serviceName = params.getParameter(
				XQConstants.PARAM_SERVICE_NAME, XQConstants.PARAM_STRING);
		m_logPrefix = "[ " + serviceName + " ]";
	}

	/**
	 * Provide access to the service implemented version.
	 * 
	 */
	protected String getVersion() {
		return s_major + "." + s_minor + ". build " + s_buildNumber;
	}

	/**
	 * Writes a standard service startup message to the log.
	 */
	protected void writeStartupMessage(XQParameters params) {
		final StringBuffer buffer = new StringBuffer();

		String serviceTypeName = params.getParameter(
				XQConstants.SERVICE_PARAM_SERVICE_TYPE,
				XQConstants.PARAM_STRING);
		buffer.append("\n\n");
		buffer.append("\t\t " + serviceTypeName + "\n ");

		buffer.append("\t\t Version ");
		buffer.append(" " + getVersion());
		buffer.append("\n");

		buffer
				.append("\t\t Copyright (c) 2008, Progress Sonic Software Corporation.");
		buffer.append("\n");

		buffer.append("\t\t All rights reserved. ");
		buffer.append("\n");

		m_xqLog.logInformation(buffer.toString());
	}

	/**
	 * Writes parameters to log.
	 */
	protected void writeParameters(XQParameters params) {

		final Map map = params.getAllInfo();
		final Iterator iter = map.values().iterator();

		while (iter.hasNext()) {
			final XQParameterInfo info = (XQParameterInfo) iter.next();

			if (info.getType() == XQConstants.PARAM_XML) {
				m_xqLog.logInformation(m_logPrefix + "Parameter Name =  "
						+ info.getName());
			} else if (info.getType() == XQConstants.PARAM_STRING) {
				m_xqLog.logInformation(m_logPrefix + "Parameter Name = "
						+ info.getName());
			}

			if (info.getRef() != null) {
				m_xqLog.logInformation(m_logPrefix + "Parameter Reference = "
						+ info.getRef());

				// If this is too verbose
				// /then a simple change from logInformation to logDebug
				// will ensure file content is not displayed
				// unless the logging level is set to debug for the ESB
				// Container.
				m_xqLog.logInformation(m_logPrefix
						+ "----Parameter Value Start--------");
				m_xqLog.logInformation("\n" + info.getValue() + "\n");
				m_xqLog.logInformation(m_logPrefix
						+ "----Parameter Value End--------");
			} else {
				m_xqLog.logInformation(m_logPrefix + "Parameter Value = "
						+ info.getValue());
			}
		}
	}

	private void fileTransformXMLServiceContext(XQServiceContext ctx)
			throws XQServiceException {

		String fileXML = new String();

		try {
			final XQParameters params = ctx.getParameters();
			String fileDirectory = params.getParameter(
					PARAM_NAME_FILE_DIRECTORY, XQConstants.PARAM_STRING);

			String fileName = params.getParameter(PARAM_NAME_FILE_NAME,
					XQConstants.PARAM_STRING);

			final String fileType = params.getParameter(PARAM_NAME_FILE_TYPE,
					XQConstants.PARAM_STRING);

			final String separator = params.getParameter(
					PARAM_NAME_FILE_SEPARATOR, XQConstants.PARAM_STRING);

			while (ctx.hasNextIncoming()) {
				XQEnvelope env = ctx.getNextIncoming();
				XQMessage msg = env.getMessage();
				fileName = getHeaderValue(fileName, msg);

				ArrayList<String> value = this.GetXml(this.Cath(fileType,
						fileDirectory + fileName), separator, m_fileEncoding,
						m_intervalExecution, m_maxExecution);

				if (!fileDirectory.endsWith("/")) {
					fileDirectory = fileDirectory + "/";
				}

				// m_xqLog.logInformation((String) (value.get(0)).toString());

				for (int i = 0; i < value.size(); i++) {
					msg.removeAllParts();
					fileXML = (String) (value.get(i)).toString();
					XQPart part = msg.createPart();
					part.setContent(fileXML, "text;xml");
					msg.addPart(part);
					env.setMessage(msg);
					// m_xqLog.logInformation(fileXML);
					Iterator addressIterator = env.getAddresses();
					if (addressIterator.hasNext()) {
						ctx.addOutgoing(env);
					}
				}

			}

		} catch (final Exception e) {
			throw new XQServiceException(e);
		}

	}

	protected ArrayList<String> GetXml(String caminho, String separador,
			String enconding, long interval, int maxExecution)
			throws XQServiceException {

		BufferedReader intxt = null;
		InputStreamReader isr = null;
		ArrayList<String> linha = null;
		int contador = 0;

		try {
			String campos = "";
			ArrayList<String> array = new ArrayList<String>();
			linha = new ArrayList<String>();

			while ((!getExistsFile(caminho)) && (contador < maxExecution)) {

				try {
					Thread.sleep(interval);
					if (!getExistsFile(caminho)) {
						// System.out.println("Não achou o arquivo = " + caminho
						// + " executou quantidade " + contador);
						contador++;
					}
				} catch (Exception erro) {
					m_xqLog.logError("Exception of timer sleep");
				}
			}

			fis = new FileInputStream(caminho);
			isr = new InputStreamReader(fis, Charset.forName(enconding));
			intxt = new BufferedReader(isr);
			campos = intxt.readLine();
			while ((campos = intxt.readLine()) != null)
				array.add(campos);

			if (array.size() >= 1) {
				for (int i = 0; i < array.size(); i++)
					linha.add(getLineTag((String) (array.get(i)).toString(),
							separador));
			} else {
				linha.add(getAddHeaderMessage());
			}

			isr.close();
			fis.close();
			intxt.close();

		} catch (IOException e) {
			// m_xqLog.logError("Tentou localizar o arquivo "+contador+" vezes
			// erro ="+e.toString());
			throw new XQServiceException("Tentou localizar o arquivo "
					+ contador + " vezes erro =" + e.toString(), e);
		}

		return linha;
	}

	/*
	 * protected String getLineTag(String texto, String separador) { String line =
	 * ""; String nxml[] = texto.split(separador); String headerXML = new
	 * String(""); String versionXML = new String("");
	 * 
	 * versionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; headerXML =
	 * "HEADER";
	 * 
	 * line = " <" + headerXML + ">"; line = line + "<Linha1>";
	 * 
	 * for (int n = 0; n < nxml.length; n++) line = line + "<Campo" + n + ">" +
	 * nxml[n] + "</Campo" + n + ">";
	 * 
	 * line = line + "</Linha1>"; line = line + "</" + headerXML + ">"; return
	 * versionXML + line.replaceAll("\"", ""); }
	 */

	public String getLineTag(String texto, String separador) {
		String line = "";
		String headerXML = new String("");
		String versionXML = new String("");
		char aspasDuplas = '\"';
		ArrayList<String> palavra = new ArrayList<String>();

		String nxml[] = texto.split(separador);
		versionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		headerXML = "HEADER";
		line = "<" + headerXML + ">";
		line = line + "<Linha1>";

		for (int i = 0; i < nxml.length; i++)
			palavra.add(nxml[i]);

		for (int n = 0; n < palavra.size(); n++)
			line = line
					+ "<Campo"
					+ n
					+ ">"
					+ palavra.get(n).replaceAll(
							Character.toString(aspasDuplas), "") + "</Campo"
					+ n + ">";

		line = line + "</Linha1>";
		line = line + "</" + headerXML + ">";

		return versionXML + line.replaceAll("\"", "");
	}

	protected String Cath(String extensao, String diretorio) {
		// File dir = new File(diretorio);
		/*
		 * int i=0; String arquivos[] = dir.list(); for ( i=0 ; i <
		 * arquivos.length;i++) if (arquivos[i].endsWith(extensao)) break;
		 */
		return diretorio;
	}

	private String getHeaderValue(String arguments, XQMessage msg)
			throws XQMessageException {

		final String PARAM_REPLACE_START = "{";
		final String PARAM_REPLACE_END = "}";
		final String PARAM_REPLACE_DEFAULT = "";

		m_xqLog.logDebug(m_logPrefix + "getHeaderValue arguments : ["
				+ arguments + "]");

		if ((this.getFindSeparator(arguments, PARAM_REPLACE_START))
				&& (this.getFindSeparator(arguments, PARAM_REPLACE_END))) {

			String propertyName = arguments.substring((arguments
					.indexOf(PARAM_REPLACE_START) + 1), arguments
					.lastIndexOf(PARAM_REPLACE_END));

			arguments = arguments.replace(PARAM_REPLACE_START,
					PARAM_REPLACE_DEFAULT).replace(PARAM_REPLACE_END,
					PARAM_REPLACE_DEFAULT);

			// / arguments = arguments.replaceAll(propertyName,
			// msg.getHeaderValue(
			// propertyName).toString());
			arguments = (String) msg.getHeaderValue(propertyName);

		}
		m_xqLog.logDebug(m_logPrefix + "getHeaderValue arguments : ["
				+ arguments + "]");

		return arguments;
	}

	/*
	 * Método responsável por verificar se o arquivo existe no diretório
	 * especificado
	 */
	public boolean getExistsFile(String caminho) {

		try {
			fis = new FileInputStream(caminho);

		} catch (IOException erro) {
			return false;

		}
		return true;
	}

	public boolean getFindSeparator(String value, String parametro) {
		int separadorInicio = value.indexOf(parametro);
		int separadorFim = value.length() - 1;

		if ((separadorInicio == 0) || (separadorInicio == separadorFim))
			return true;

		return false;
	}

	public String getAddHeaderMessage() {
		String headerXML = new String("");
		String versionXML = new String("");
		versionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		headerXML = "<HEADER></HEADER>";

		return versionXML + headerXML.replaceAll("\"", "");

	}

}