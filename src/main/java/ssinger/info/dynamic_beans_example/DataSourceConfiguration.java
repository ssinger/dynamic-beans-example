package ssinger.info.dynamic_beans_example;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
public class DataSourceConfiguration {

	static private Logger logger = Logger.getLogger(DataSourceConfiguration.class);
	
	/**
	 * 
	 * Create a beanPostProcessor , @Bean for adding the dynamic beans.
	 */
	@Bean
	static BeanDefinitionRegistryPostProcessor beanPostProcessor(final ConfigurableEnvironment environment) {
		return new BeanDefinitionRegistryPostProcessor() {

			public void postProcessBeanFactory(ConfigurableListableBeanFactory arg0) throws BeansException {
				// TODO Auto-generated method stub
				
			}

			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanRegistry) throws BeansException {
				createDynamicBeans(environment,beanRegistry);
				
			}
			
		};
	}
	
	/**
	 * 
	 * @param environment The environment which properties can be extracted from.
	 * @return A map of DataSourceProperties for each prefix.
	 */
	static private Map<String,DataSourceProperties> parseProperties(ConfigurableEnvironment environment) {
		Map<String,DataSourceProperties> propertyMap = new HashMap<>();		
		for(PropertySource source : environment.getPropertySources()) {
			if(source instanceof EnumerablePropertySource ) {
				EnumerablePropertySource propertySource = (EnumerablePropertySource) source;
				for(String property : propertySource.getPropertyNames()) {
					if(DataSourceProperties.isUrlProperty(property)) {
						String prefix = extractPrefix(property);
						propertyMap.put(prefix, new DataSourceProperties(environment,prefix));
					}
				}
			}
		}
		return propertyMap;
	}
	
	static private void createDynamicBeans(ConfigurableEnvironment environment,BeanDefinitionRegistry beanRegistry) {
		Map<String,DataSourceProperties> propertyMap = parseProperties(environment);
		for(Map.Entry<String,DataSourceProperties> entry : propertyMap.entrySet()) {
			registerDynamicBean(entry.getKey(),entry.getValue(),beanRegistry);
		}
	}
	
	
	/**
	 * This function will create the dynamic bean definitions.
	 * @param prefix  The prefix for the beans we are creating.
	 * @param dsProps  The properties for the datasource
	 * @param beanRegistry  The bean registry we add the beans to
	 */
	static private void registerDynamicBean(String prefix, DataSourceProperties dsProps,BeanDefinitionRegistry beanRegistry) {	
		logger.info("Registering beans for " + prefix);
		BeanDefinition dataSourceBeanDef = BeanDefinitionBuilder.genericBeanDefinition(BasicDataSource.class)
				.addPropertyValue("url",dsProps.getUrl())
				.addPropertyValue("username", dsProps.getUsername())
				.addPropertyValue("password", dsProps.getPassword())
				.addPropertyValue("driverClassName", dsProps.getDriver())				
				.getBeanDefinition();
		if(dsProps.getPrimary()) {
			dataSourceBeanDef.setPrimary(true);
		}
		beanRegistry.registerBeanDefinition("datasource_" + prefix, dataSourceBeanDef);
		if(dsProps.getPrimary()) {
			beanRegistry.registerAlias("datasource_" + prefix, "dataSource");
		}
		BeanDefinition repositoryBeanDef = BeanDefinitionBuilder.genericBeanDefinition(Repository.class)
				.addConstructorArgReference("datasource_" + prefix)
				.getBeanDefinition();
		beanRegistry.registerBeanDefinition("repository_" + prefix, repositoryBeanDef);
		
	}
	
	
	
	static private String extractPrefix(String property) {
		int idx = property.indexOf(".");
		return property.substring(0, idx);
	}
}
