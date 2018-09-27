package br.com.cnabbradesco;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.GZipException;
import br.com.bradesco.webta.security.exception.ParameterException;
import br.com.cnabbradesco.entities.CNAB;
import br.com.cnabbradesco.utils.Application;
import br.com.cnabbradesco.utils.ResourceManager;

public class Init {

	public static void main(String[] args) {
		// Verificação de arquivos de configuração
		try {
			if (!(new File(Application.DIR + "/config.properties").exists())) {
				System.out.println("\n"
						+ "Arquivo \"config.properties\" não encontrado. Criando novo template como \"config.properties.example\" no mesmo contexto.");
				ResourceManager.extract(Application.RES + "/config.properties.example", Application.DIR);
			}
			if (!(new File(Application.DIR + "/log4j2.properties").exists())) {
				System.out.println(
						"Arquivo \"log4j2.properties\" não encontrado. Criando novo template como \"log4j2.properties.example\" no mesmo contexto."
								+ "\n");
				ResourceManager.extract(Application.RES + "/log4j2.properties.example", Application.DIR);
			}
		} catch (Exception e) {
			System.out.println("\n"
					+ "Erro ao copiar os recursos da aplicação. Verifique se o presente diretório tem permissão de escrita.");
			help();
			System.exit(0);
		}

		// Validações de argumentos
		if (args.length != 3 || args[0] == "--help") {
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
			is = new FileInputStream(Application.DIR + "/config.properties");
			p.load(is);
			is.close();

			// Configura um novo objeto CNAB
			cb = new CNAB();
			cb.setArquivo(arquivo);
			cb.setSenha(p.getProperty("cnabbradesco.criptografia.senha"));
			cb.setToken(p.getProperty("cnabbradesco.criptografia.token"));
		} catch (IOException | CryptoException e) {
			Application.LOG.warn("Arquivo de configuração não encontrado.");
			Application.LOG.error(e.getMessage());
			help();
			System.exit(0);
		}

		// Tratando os argumentos de inicialização
		switch (opcao) {
		case "--help":
			help();
			break;
		case "-d":
			try {
				cb.descriptografar(destino);
			} catch (IOException | CryptoException | ParameterException e) {
				Application.LOG.error(e.getMessage());
			}
			break;
		case "-c":
			try {
				cb.criptografar(destino);
			} catch (IOException | CryptoException | ParameterException | GZipException e) {
				Application.LOG.error(e.getMessage());
			}
			break;
		default:
			help();
			break;
		}
	}

	private static void help() {
		System.out.println("\n" + "CNABBradesco - Manipulador de arquivos CNAB do Banco Bradesco");
		System.out.println("\n" + "Uso: java -jar cnabbradesco.jar [OPÇÃO] \"ARQUIVO\" \"DIRETORIO_DESTINO\"");
		System.out.println("\n" + "Opções de comandos:");
		System.out.println("  -d   Descriptografa um arquivo CNAB remessa (.RET)");
		System.out.println("  -c   Criptografa um arquivo CNAB retorno (.REM)");
		System.out.println("\n" + "Pré-requisitos de uso:");
		System.out.println("   1 - Arquivo \"config.properties\":");
		System.out.println("       Deve ser criado no mesmo contexto de execução desta biblioteca, dentro da pasta \""
				+ Application.DIR + "/\";");
		System.out.println(
				"       Deve conter as propriedades \"cnabbradesco.criptografia.token\" e \"cnabbradesco.criptografia.senha\" contendo o diretório do arquivo chave (.bin) e a senha de acesso ao WEBTA, respectivamente (vide \"config.properties.example\");");
		System.out.println("   2 - Arquivo \"log4j2.properties\":");
		System.out.println("       Deve ser criado no mesmo contexto de execução desta biblioteca, dentro da pasta \""
				+ Application.DIR + "/\" seguindo como exemplo o arquivo \"log4j2.properties.example\";");
		System.out.println(
				"       A propridedade \"property.logs\" deve ser configurada com o diretório que irá conter os logs de aplicação.");
	}

}
