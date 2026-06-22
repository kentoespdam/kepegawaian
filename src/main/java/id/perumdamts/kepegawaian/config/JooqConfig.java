package id.perumdamts.kepegawaian.config;

import org.jooq.conf.Settings;
import org.springframework.boot.jooq.autoconfigure.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JooqConfig {

    @Bean
    public DefaultConfigurationCustomizer renderGroupConcatMaxLenSessionVariable() {
        return c -> {
            var s = c.settings();
            if (s == null) s = new Settings();
            c.setSettings(s.withRenderGroupConcatMaxLenSessionVariable(false));
        };
    }
}
