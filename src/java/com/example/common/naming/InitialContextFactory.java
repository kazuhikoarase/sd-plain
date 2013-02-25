package com.example.common.naming;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;

import com.example.common.sql.DataSourceConfig;

/**
 * InitialContextFactory
 * @author Kazuhiko Arase
 */
public class InitialContextFactory 
implements javax.naming.spi.InitialContextFactory {

	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {

		Context initCtx = new SimpleContext();
		Context envCtx = initCtx.createSubcontext("java:comp/env");

		try {
			for (String dataSourceName : DataSourceConfig.getDataSourceNames() ) {
				DataSourceConfig.DataSource dataSource = DataSourceConfig.get(dataSourceName);
				envCtx.bind(dataSourceName, new SimpleDataSource(
					dataSource.getDriverClassName(),
					dataSource.getUrl(),
					dataSource.getUsername(),
					dataSource.getPassword()
				) );
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}

		return initCtx;
	}		
}