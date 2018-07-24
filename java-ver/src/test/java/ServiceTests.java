import com.prvz.url_checker.exception.NoTasksException;
import com.prvz.url_checker.exception.TaskAlreadyClosedException;
import com.prvz.url_checker.model.CheckTask;
import com.prvz.url_checker.model.CheckTaskHistory;
import com.prvz.url_checker.repository.CheckTaskHistoryRepository;
import com.prvz.url_checker.repository.CheckTaskRepository;
import com.prvz.url_checker.service.TaskServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class ServiceTests {

    @Mock
    private CheckTaskRepository taskRepository;

    @Mock
    private CheckTaskHistoryRepository historyRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private void mockRepoFindTopByOrderByCreateDateDesc(CheckTask task) {
        Mockito
                .when(taskRepository.findTopByOrderByCreateDateDesc())
                .thenReturn(Optional.ofNullable(task));
    }

    private CheckTask generateTask(boolean withCloseDate, List<String> urls) {
        return new CheckTask(
                OffsetDateTime.now().minusHours(1),
                withCloseDate ? OffsetDateTime.now() : null,
                1,
                urls,
                UUID.randomUUID().toString());
    }

    private CheckTask generateTask(boolean withCloseDate) {
        return generateTask(withCloseDate, Collections.singletonList("http://abc.com"));
    }

    private CheckTask generateTask() {
        return generateTask(true);
    }

    @Test
    public void testParseUrl() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = taskService.getClass().getDeclaredMethod("parseUrl", String.class);
        method.setAccessible(true);
        String url = "htwerdfsdr:Sdfsrqedf";
        try {
            method.invoke(taskService, url);
        } catch (Exception ex) {
            assertThat(ex).hasCauseInstanceOf(MalformedURLException.class);
        }
        url = "https://abc.com";
        assertThat(method.invoke(taskService, url).toString())
                .isEqualTo(url.replaceFirst("^https", "http"));

    }

    @Test
    public void testNewTask() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        CheckTask task = generateTask(false, Arrays.asList("http://ya.ru", "http://abc.com"));
        Mockito.when(taskRepository.save(task)).thenReturn(task);
        mockRepoFindTopByOrderByCreateDateDesc(task);
        List<CheckTaskHistory> history = new ArrayList<>();
        Mockito.when(historyRepository.save(any(CheckTaskHistory.class)))
                .then((Answer<CheckTaskHistory>) invocation -> {
                    history.add(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });
        taskService.newTask(task);
        Field f = taskService.getClass().getDeclaredField("currentTasks");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<ScheduledFuture<?>> futures =
                (List<ScheduledFuture<?>>) f.get(taskService);
        assertThat(futures.size()).isEqualTo(2);
        Thread.sleep(5000);
        taskService.stopLast();
        assertThat(history).allMatch(hist -> hist.getTaskId().equals(task.getId()));
        // TODO: 25.07.2018 Скорее всего вообще бесмысленный тест
        assertThat(history.size()).isEqualTo(Mockito.mockingDetails(historyRepository).getInvocations().size());
    }

    @Test
    public void testGetLastInfo() {
        CheckTask task = generateTask();
        mockRepoFindTopByOrderByCreateDateDesc(task);
        assertThat(taskService.getLastInfo()).isEqualTo(task);
    }

    @Test
    public void testFindById() {
        CheckTask task = generateTask();
        Mockito.when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        assertThat(taskService.getTaskInfo(task.getId())).isEqualTo(task);
    }

    @Test
    public void testStopLastCorrect() {
        CheckTask task = generateTask(false);
        mockRepoFindTopByOrderByCreateDateDesc(task);
        taskService.stopLast();
    }

    @Test(expected = NoTasksException.class)
    public void testStopLastWithNoTasks() {
        mockRepoFindTopByOrderByCreateDateDesc(null);
        taskService.stopLast();
    }

    @Test(expected = TaskAlreadyClosedException.class)
    public void testStopLastWithClosedTask() {
        CheckTask task = generateTask(true);
        mockRepoFindTopByOrderByCreateDateDesc(task);
        taskService.stopLast();
    }

}
