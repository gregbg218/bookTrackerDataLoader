package greg.projects;


import greg.projects.author.Author;
import greg.projects.author.AuthorRepository;
import greg.projects.book.Book;
import greg.projects.book.BookRepository;
import greg.projects.connection.DataStaxAstraProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class Application {

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Value("${datadump.location.author}")
    private String authorDumpLocation;

    @Value("${datadump.location.works}")
    private String worksDumpLocation;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private void initAuthors()
    {
        Path path = Paths.get(authorDumpLocation);
        try (Stream<String> lines = Files.lines(path)) {
            List<Author> batch = new ArrayList<>(1000); // List to hold the current batch of records

            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/", ""));

                    batch.add(author); // Add the record to the current batch

                    if (batch.size() == 50) {
                        // Save the current batch
                        System.out.println("Saving authors...");
                        authorRepository.saveAll(batch);
                        batch.clear(); // Clear the batch for the next set of records
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // Save any remaining records in the last batch
            if (!batch.isEmpty()) {
                System.out.println("Saving authors...");
                authorRepository.saveAll(batch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void initWorks()
    {
        Path path = Paths.get(worksDumpLocation);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(path)) {
            List<Book> batch = new ArrayList<>(1000); // List to hold the current batch of records

            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Book book = new Book();
                    book.setId(jsonObject.optString("key").replace("/works/",""));

                    book.setName(jsonObject.optString("title"));

                    JSONObject descriptionObj = jsonObject.optJSONObject("description");
                    if(descriptionObj!=null)
                    {
                        book.setDescription(descriptionObj.optString("value"));
                    }

                    JSONObject publishedDateObj = jsonObject.optJSONObject("created");
                    if(publishedDateObj!=null)
                    {
                        String dateStr= publishedDateObj.optString("value");
                        book.setPublishedDate(LocalDate.parse(dateStr,dateFormat));
                    }

                    JSONArray coversJSONArray= jsonObject.optJSONArray("covers");
                    if(coversJSONArray!=null)
                    {
                        List<String> coverIds= new ArrayList<>();
                        for(int i=0;i<coversJSONArray.length();i++)
                        {
                            coverIds.add(coversJSONArray.optString(i));
                        }
                        book.setCoverIds(coverIds);
                    }

                    JSONArray authorsJSONArray= jsonObject.optJSONArray("authors");
                    if(authorsJSONArray!=null) {
                        List<String> authorIds = new ArrayList<>();
                        for(int i=0;i<authorsJSONArray.length();i++)
                        {
                            String authorId = authorsJSONArray.getJSONObject(i).getJSONObject("author").getString("key").replace("/authors/","");
                            authorIds.add(authorId);
                        }
                        book.setAuthorIds(authorIds);

                        List<String> authorNames= authorIds.stream().map(id->authorRepository.findById(id)).map(optionalAuthor ->
                        {
                            if(!optionalAuthor.isPresent())
                                return "Unkown Author";
                            return optionalAuthor.get().getName();
                        }
                        ).collect(Collectors.toList());
                        book.setAuthorNames(authorNames);
                    }


                    batch.add(book); // Add the record to the current batch

                    if (batch.size() == 50) {
                        // Save the current batch
                        System.out.println("Saving books...");
                        bookRepository.saveAll(batch);
                        batch.clear(); // Clear the batch for the next set of records
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // Save any remaining records in the last batch
            if (!batch.isEmpty()) {
                System.out.println("Saving books...");
                bookRepository.saveAll(batch);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @PostConstruct
    public void start()
    {
        initAuthors();
        initWorks();
    }




}
