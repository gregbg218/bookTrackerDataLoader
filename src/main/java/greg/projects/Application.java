package greg.projects;


import greg.projects.author.Author;
import greg.projects.author.AuthorRepository;
import greg.projects.connection.DataStaxAstraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class Application {

    @Autowired
    AuthorRepository authorRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void start()
    {
        Author author = new Author();
        author.setId("id");
        author.setName("name");
        author.setPersonalName("personalName");
        authorRepository.save(author);
    }




}
