package com.ares.mq;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Queue;

import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
@ImportResource("classpath:beans.xml")
public class DemoApplication {

	@Autowired
	private ApplicationContext context;

	@Bean
	public Queue queue() {
		return new ActiveMQQueue("sample.queue");
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	// @Bean
	// public BrokerService broker() throws Exception {
	// BrokerService broker = new BrokerService();
	// broker.setUseJmx(true);
	// broker.addConnector("tcp://localhost:61616");

	// return broker;
	// }

	@Bean // (initMethod = "start", destroyMethod = "stop")
	public BrokerService brokerService() throws Exception {
		BrokerService brokerService = new BrokerService();
		// brokerService.setPersistent(false);
		brokerService.setPersistenceAdapter(new MemoryPersistenceAdapter());
		brokerService.setUseJmx(false);
		// brokerService.addConnector("vm://localhost:0");
		brokerService.addConnector("tcp://localhost:61616");
		brokerService.setBrokerName("broker");
		brokerService.setUseShutdownHook(false);

		// enable authentication
		List<AuthenticationUser> users = new ArrayList<>();
		// username and password to use to connect to the broker.
		// This user has users privilege (able to browse, consume, produce, list
		// destinations)
		users.add(new AuthenticationUser("user", "password", "users"));
		SimpleAuthenticationPlugin plugin = new SimpleAuthenticationPlugin(users);
		BrokerPlugin[] plugins = new BrokerPlugin[] { plugin };
		brokerService.setPlugins(plugins);

		return brokerService;
	}

	@Bean
	public ApplicationRunner runner(JmsTemplate template) {
		return args -> template.convertAndSend("foo", "AMessage");
	}

	@JmsListener(destination = "foo")
	public void listen(String in) {
		System.out.println(in);
	}
}
