package nl.luminis.articles.elasticsearch.integration.testcontainers;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class ElasticsearchSpringRunner extends SpringJUnit4ClassRunner {

    public ElasticsearchSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(new JUnitExecutionListener(notifier));
        notifier.fireTestRunStarted(getDescription());
        super.run(notifier);
    }
}
