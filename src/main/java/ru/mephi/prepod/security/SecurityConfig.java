package ru.mephi.prepod.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .usersByUsernameQuery("SELECT username, password, TRUE enabled FROM users WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT u.username, a.authority " +
                                            "FROM users u " +
                                            "JOIN users_roles ur ON u.id = ur.user_id " +
                                            "JOIN roles r on ur.role_id = r.id " +
                                            "JOIN roles_authorities ra on r.id = ra.role_id " +
                                            "JOIN authorities a on ra.authority_name = a.authority " +
                                            "WHERE u.username = ?")
                .passwordEncoder(new BCryptPasswordEncoder(10))
                .dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security",
                        "/swagger-ui.html", "/webjars/**")
                .permitAll()
                .antMatchers("/**")
                .authenticated()
                .and()
                .httpBasic();
    }
}
