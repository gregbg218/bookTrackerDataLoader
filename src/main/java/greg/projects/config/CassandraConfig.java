package greg.projects.config;

import greg.projects.connection.DataStaxAstraProperties;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;


@Configuration
public class CassandraConfig {
    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();

        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }
}
