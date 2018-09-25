package br.com.cnabbradesco;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.GZipException;
import br.com.bradesco.webta.security.exception.ParameterException;
import br.com.cnabbradesco.entities.CNAB;
import br.com.cnabbradesco.utils.Application;

public class Init extends Application {

	public static void main(String[] args) {
		// Validações de argumentos
		if (args.length != 3) {
			help();
			System.exit(0);
		}

		// Triagem de argumentos e declaração de variáveis
		Properties p = new Properties();
		InputStream is = null;
		CNAB cb = null;
		String opcao = args[0];
		String arquivo = args[1];
		String destino = args[2];

		try {
			// Carrega as configurações da aplicação (arquivo: config.properties)
			is = new FileInputStream(DIR + "/res/config.properties");
			p.load(is);
			is.close();

			// Configura um novo objeto CNAB
			cb = new CNAB();
			cb.setArquivo(arquivo);
			cb.setSenha(p.getProperty("cnabbradesco.criptografia.senha"));
			cb.setToken(p.getProperty("cnabbradesco.criptografia.token"));
		} catch (IOException | CryptoException e) {
			LOG.warn("Arquivo de configuração não encontrado.");
			LOG.error(e.getMessage());
			opcao = "--init";
		}

		// Tratando os argumentos de inicialização
		switch (opcao) {
		case "--init":
			LOG.warn("Iniciando configuração da aplicação CNABBradesco.");
			break;
		case "--help":
			help();
			break;
		case "-d":
			try {
				cb.descriptografar(destino);
			} catch (IOException | CryptoException | ParameterException e) {
				LOG.error(e.getMessage());
			}
			break;
		case "-c":
			try {
				cb.criptografar(destino);
			} catch (IOException | CryptoException | ParameterException | GZipException e) {
				LOG.error(e.getMessage());
			}
			break;
		default:
			help();
			break;
		}
	}

	private static void help() {
		System.out.println("CNABBradesco - Manipulador de arquivos CNAB do Banco Bradesco" + "\n");
		System.out.println("Uso: java jar cnabbradesco.jar [OPÇÃO] \"ARQUIVO\" \"DIRETORIO_DESTINO\"" + "\n");
		System.out.println("Opções de comandos:");
		System.out.println("  -d   Descriptografa um arquivo CNAB");
		System.out.println("  -c   Criptografa um arquivo CNAB");
	}

}
