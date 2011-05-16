package com.progress.codeshare.esbservice.fileTransformXML;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FileTransformXML {
	/*
	 * FileTransformXML
	 */
	private FileInputStream fis = null;

	public static void main(String[] args) {
		FileTransformXML imp = new FileTransformXML();
		ArrayList<String> texto = new ArrayList<String>();
		String value = new String();

		String diretorio = "C:\\Temp\\";
		String file = "Header.txt";
		String type = "txt";

		texto = imp.GetXml(imp.Cath(type, diretorio + file), ",", "UTF-16",
				60000, 5);

		for (int i = 0; i < texto.size(); i++) {
			// System.out.println((String)(texto.get(i)).toString());
			value = (String) (texto.get(i)).toString();
			System.out.println(value);
		}
	}

	/*
	 * Metodo responsavel pelo retorno do XML do arquivo.
	 */
	protected ArrayList<String> GetXml(String caminho, String separador,
			String enconding, long interval, int maxExecution) {

		BufferedReader intxt = null;
		InputStreamReader isr = null;
		ArrayList<String> linha = null;

		try {
			String campos = "";
			ArrayList<String> array = new ArrayList<String>();
			linha = new ArrayList<String>();

			int contador = 0;

			while ((!getExistsFile(caminho)) && (contador < maxExecution)) {
				try {
					Thread.sleep(interval);
					System.out.println("Não achou o arquivo = " + caminho
							+ " executou quantidade " + contador);
					contador++;

				} catch (Exception erro) {
					System.out.println("Exception of timer sleep");
				}
			}

			fis = new FileInputStream(caminho);
			isr = new InputStreamReader(fis, Charset.forName(enconding));
			intxt = new BufferedReader(isr);

			campos = intxt.readLine();

			while ((campos = intxt.readLine()) != null)
				array.add(campos);

			if (array.size() > 0) {
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

			System.out.println("GetXml =" + e.toString());

		}

		return linha;
	}

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

	public String Cath(String extensao, String diretorio) {
		// File dir = new File(diretorio);
		/*
		 * int i=0; String arquivos[] = dir.list(); for ( i=0 ; i <
		 * arquivos.length;i++) if (arquivos[i].endsWith(extensao)) break;
		 */
		return diretorio;
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

	/* convert from UTF-8 encoded HTML-Pages -> internal Java String Format */
	public static String convertFromUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("ASCII"));

		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
		return out;
	}

	public String getAddHeaderMessage() {
		String headerXML = new String("");
		String versionXML = new String("");
		versionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		headerXML = "<HEADER></HEADER>";

		return versionXML + headerXML.replaceAll("\"", "");

	}

}
