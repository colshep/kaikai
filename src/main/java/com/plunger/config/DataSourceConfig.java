package com.plunger.config;

import com.plunger.config.datasource.PlungerDataBaseProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.plunger.mapper.*")
public class DataSourceConfig {

    // 创建数据源
    @Bean(name = "plunger")
    public DataSource plungerDataSource(PlungerDataBaseProperties prop) {
        DataSource build = DataSourceBuilder.create()
                .driverClassName(prop.getDriverClassName())
                .url(prop.getUrl())
                .username(prop.getUsername())
                .password(prop.getPassword())
                .build();
        return build;
    }

    @Bean(name = "plungerJdbcTemplate")
    public JdbcTemplate plungerJdbcTemplate(@Qualifier("plunger") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
