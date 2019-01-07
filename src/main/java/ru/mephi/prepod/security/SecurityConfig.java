package ru.mephi.prepod.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    @Autowired
    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery("SELECT username, password, TRUE enabled FROM users WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT u.username, ur.role_id authority FROM users u " +
                                            "JOIN users_roles ur ON u.id = ur.user_id " +
                                            "WHERE u.username = ?")
                .passwordEncoder(new BCryptPasswordEncoder(10))
                .dataSource(dataSource);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] roles = new String[] { Role.ADMIN.name(), Role.HEAD_OF_DEPARTMENT.name(), Role.PROFESSOR.name() };
        http
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
