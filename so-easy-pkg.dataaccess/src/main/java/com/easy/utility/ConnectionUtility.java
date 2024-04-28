package com.easy.utility;

import com.alibaba.druid.pool.DruidDataSource;
import com.easy.config.ApplicationContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ConnectionUtility {
    @Autowired
    private ApplicationContextProvider appcontext;
    @Autowired
    private DataSource datasource;

    public Connection getConnection() throws SQLException {
        Connection conn = datasource.getConnection();
        return conn;
    }

    public DruidDataSource getDuridConnection(String beanName) {
        DruidDataSource druiddatasource = (DruidDataSource) ApplicationContextProvider.getBean(beanName);
        return druiddatasource;
    }
}
