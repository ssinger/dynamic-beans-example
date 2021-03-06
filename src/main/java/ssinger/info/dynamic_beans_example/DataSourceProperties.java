package ssinger.info.dynamic_beans_example;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 
 * DataSourceProperties stores the properties for a particular datasource.
 * In a normal, non-dynamic bean program these properties could come from
 * @ConfigurationProperties but this won't where we want to support a dynamic
 * prefix.
 * 
 * The properties are 
 * 
 * prefix1.datasource.driver=
 * prefix1.datasource.url=
 * prefix1.datasource.username=
 * prefix1.datasource.password=
 * 
 * prefix2.datasource.driver=
 * prefix2.datasource.url=
 * prefix2.datasource.username=
 * prefix2.datasource.password=
 * 
 * .
 * .
 * .
 * prefixn.datasource.driver=
 * prefixn.datasource.url=
 * prefixn.datasource.username=
 * prefixn.datasource.password=
 * 
 * Each instance of this class stores the properties for a prefix.
 * 
 */
public class DataSourceProperties {

	private String driver;
	private String url;
	private String username;
	private String password;
	private String prefix;
	private Boolean primary=false;
	
	private ConfigurableEnvironment environment;
	
	private static String propertyBase="datasource";
	
	public DataSourceProperties(ConfigurableEnvironment environment,
			String prefix) {
		this.prefix = prefix;
		this.environment = environment;
		driver = getProperty("driver");
		url = getProperty("url");
		username = getProperty("username");
		password = getProperty("password");
		primary = getProperty("primary",Boolean.class);
		
	}
	
	public static boolean isUrlProperty(String property) {
		if(property.endsWith(propertyBase + ".url")) {
			return true;
		}
		return false;
	}
	
		
	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPrefix() {
		return prefix;
	}

	public ConfigurableEnvironment getEnvironment() {
		return environment;
	}

	public static String getPropertyBase() {
		return propertyBase;
	}
	public Boolean getPrimary() {
		return primary;
	}
	private String getProperty(String property) {
		return getProperty(property,String.class);
	}
	private<T> T getProperty(String property,Class<T> type) {
		
		T value = environment.getProperty(prefix + "." + propertyBase + "." + property,type);
		if(value == null) {
			throw new IllegalStateException(prefix + "." + propertyBase + "." + property +" is not found" );
		}
		return value;
	}
}
