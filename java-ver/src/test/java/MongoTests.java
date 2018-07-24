import com.prvz.url_checker.config.MongoConfig;
import com.prvz.url_checker.model.CheckTask;
import com.prvz.url_checker.repository.CheckTaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.OffsetDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataMongoTest
@ContextConfiguration(classes = {MongoConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MongoTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoOperations mongoOps;

    @Autowired
    private CheckTaskRepository taskRepository;

    @Test
    public void test() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            mongoOps.save(new CheckTask(OffsetDateTime.now(), null, 10, Collections.singletonList("http://abc.com" + i)));
            Thread.sleep(10);
        }
        assertThat(taskRepository.findTopByOrderByCreateDateDesc().orElse(null))
                .isNotNull()
                .matches(checkTask ->
                        checkTask.getUrls().get(0).endsWith("99"));

        CheckTask lastTask = new CheckTask(OffsetDateTime.now(), null, 10, Collections.singletonList("http://abc.com"));
        lastTask = taskRepository.save(lastTask);
        assertThat(taskRepository.findTopByOrderByCreateDateDesc().orElse(null))
                .isNotNull()
                .isEqualTo(lastTask);
    }


}
