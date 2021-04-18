package tr.edu.duzce.mf.bm470.captcha.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import tr.edu.duzce.mf.bm470.captcha.security.CustomSuccessHandler;

@EnableWebSecurity
@Configuration
@Order(1)
public class UserSecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService customUserDetailsService;

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .antMatcher("/user/**")
                .authorizeRequests().anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/loginUser")
                .loginProcessingUrl("/user/process_login")
                .permitAll()
                .successHandler(customSuccessHandler)
                .failureUrl("/loginUser?error=true")
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/loginUser?logout=true").permitAll();
    }
}
