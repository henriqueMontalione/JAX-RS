package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.modelo.Projeto;

public class ClientTest {

	private HttpServer server;
	private WebTarget target;
	private Client client;

	@Before
	public void startaServidor() {
		server = Servidor.inicializaServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8080");
	}

	@After
	public void mataServidor() {
		server.stop();
	}

	@Test
	public void testaQueAConexaoComOServidorFunciona() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://www.mocky.io");
		String conteudo = target.path("/v2/52aaf5deee7ba8c70329fb7d").request().get(String.class);
		// System.out.println(conteudo);
		Assert.assertTrue(conteudo.contains("<rua>Rua Vergueiro 3185"));
	}

	@Test
	public void testaQueBuscarUmCarrinhotrazOCarinhoEsperado() {
		Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());

	}

	@Test
	public void testaQueSuportaNovosCarrinhos() {
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(314, "Microfone", 37, 1));
		carrinho.setRua("Rua Vergueiro 3185");
		carrinho.setCidade("Sao Paulo");
		Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

		Response response = target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
		Assert.assertEquals("Microfone", carrinhoCarregado.getProdutos().get(0).getNome());
	}

	@Test
	public void testaQueAConexaoComOServidorFuncionaNoPathDeProjetos() {
		Projeto projeto = target.path("/projetos/1").request().get(Projeto.class);
		Assert.assertEquals(1L, projeto.getId(), 0);
	}

	@Test
	public void testaPostProjeto() {
		/*
		 * Carrinho carrinho = new Carrinho(); carrinho.adiciona(new Produto(314L,
		 * "Tablet", 999, 1)); carrinho.setRua("Rua Vergueiro");
		 * carrinho.setCidade("Sao Paulo"); String xml = carrinho.toXML();
		 * Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		 * 
		 * Response response = target.path("/carrinhos").request().post(entity);
		 * Assert.assertEquals(201, response.getStatus()); String location =
		 * response.getHeaderString("Location"); String conteudo =
		 * client.target(location).request().get(String.class);
		 * Assert.assertTrue(conteudo.contains("Tablet"));
		 */

		Projeto projeto = new Projeto();
		projeto.setNome("ProjetoTeste");
		projeto.setAnoDeInicio(2017);
		Entity<Projeto> entity = Entity.entity(projeto, MediaType.APPLICATION_XML);
		
		Response response = target.path("/projetos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("Location");
		Projeto projetoCarregado = client.target(location).request().get(Projeto.class);
		Assert.assertEquals("ProjetoTeste", projetoCarregado.getNome());
	}
}