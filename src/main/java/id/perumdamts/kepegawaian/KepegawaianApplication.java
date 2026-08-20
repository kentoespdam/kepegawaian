package id.perumdamts.kepegawaian;

import id.perumdamts.kepegawaian.config.AppwriteProperties;
import id.perumdamts.kepegawaian.config.CutiProperties;
import id.perumdamts.kepegawaian.config.PegawaiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
@EnableConfigurationProperties({AppwriteProperties.class, CutiProperties.class, PegawaiProperties.class})
public class KepegawaianApplication {

    static void main(String[] args) {
        SpringApplication.run(KepegawaianApplication.class, args);
    }

}
