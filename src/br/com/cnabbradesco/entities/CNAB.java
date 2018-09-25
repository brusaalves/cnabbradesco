package br.com.cnabbradesco.entities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import br.com.bradesco.webta.security.crypto.WEBTACryptoUtil;
import br.com.bradesco.webta.security.crypto.WEBTAInputStream;
import br.com.bradesco.webta.security.crypto.WEBTAOutputStream;
import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.GZipException;
import br.com.bradesco.webta.security.exception.ParameterException;
import br.com.cnabbradesco.utils.Application;

/**
 * 
 * @author bruno.alves <brunosalves3@gmail.com>
 */
public class CNAB extends Application {

	private String senha;
	private byte[] token;
	private LinkedHashMap<String, String> arquivo;

	/**
	 * 
	 * @param arquivoDescriptografado
	 * @param diretorioDestino
	 */
	public void criptografar(String destino) throws IOException, CryptoException, ParameterException, GZipException {
		LOG.info("Criptografando o arquivo \"" + this.arquivo.get("localizacao") + this.arquivo.get("nome")
				+ "\" para \"" + destino + this.arquivo.get("nome") + "\"");

		// Declaração de variáveis
		WEBTAOutputStream wos = new WEBTAOutputStream(this.arquivo.get("nome"), destino, this.token);
		byte[] bufCripto = new byte[8192];
		int len = 0;
		FileInputStream fis = new FileInputStream(this.arquivo.get("localizacao") + this.arquivo.get("nome"));

		// Escrita de conteúdo descriptografado
		while ((len = fis.read(bufCripto)) > 0) {
			wos.write(bufCripto, 0, len);
			wos.flush();
		}

		// Liberação de memória
		fis.close();
		wos.close();
	}

	/**
	 * 
	 * @param token
	 * @param senha
	 * @param destino
	 * 
	 * @throws IOException
	 * @throws CryptoException
	 * @throws ParameterException
	 */
	public void descriptografar(String destino) throws IOException, CryptoException, ParameterException {
		LOG.info("Descriptografando o arquivo \"" + this.arquivo.get("localizacao") + this.arquivo.get("nome")
				+ "\" para \"" + destino + this.arquivo.get("nome") + "\".");

		// Declaração de variáveis
		WEBTAInputStream wis = new WEBTAInputStream(this.arquivo.get("nome"), this.arquivo.get("localizacao"),
				this.token);
		byte[] bufDecripto = new byte[WEBTAInputStream.BUF_SIZE];
		int len = 0;
		FileOutputStream fos = new FileOutputStream(destino + this.arquivo.get("nome"));
		BufferedOutputStream bos = new BufferedOutputStream(fos);

		// Escrita de conteúdo descriptografado
		while ((len = wis.read(bufDecripto)) > 0) {
			bos.write(bufDecripto, 0, len);
			bos.flush();
		}

		// Liberação de memória
		wis.close();
		bos.close();
		fos.close();
	}

	// Getters
	public String getSenha() {
		return this.senha;
	}

	public byte[] getToken() {
		return this.token;
	}

	public LinkedHashMap<String, String> getArquivo() {
		return this.arquivo;
	}

	// Setters
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public void setToken(String token) throws IOException, CryptoException {
		if (!isValidFileOrDirectory(token)) {
			System.out.println("Arquivo token não encontrado.");
			System.exit(0);
		}
		this.token = WEBTACryptoUtil.decodeKeyFile(new File(token), this.senha);
	}

	public void setArquivo(String arquivo) {
		if (!isValidFileOrDirectory(arquivo)) {
			System.out.println("Arquivo token não encontrado.");
			System.exit(0);
		}
		String[] aux = arquivo.split("/");
		this.arquivo = new LinkedHashMap<String, String>();
		this.arquivo.put("nome", aux[aux.length - 1]);
		this.arquivo.put("localizacao", arquivo.replaceAll(this.arquivo.get("nome"), ""));
	}

	/**
	 * 
	 * @param path
	 * @return boolean
	 */
	protected boolean isValidFileOrDirectory(String path) {
		return new File(path).exists();
	}
}
