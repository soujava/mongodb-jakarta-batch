package expert.os.demos.ecommerce.batch;

import jakarta.annotation.PostConstruct;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.operations.NoSuchJobException;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.batch.runtime.BatchStatus;
import jakarta.batch.runtime.JobExecution;
import jakarta.batch.runtime.JobInstance;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@ApplicationScoped
public class CustomerSegmentationService {

    public static final String JOB_NAME = "customer-segmentation";
    private static final String DEFAULT_THRESHOLDS = "/segmentation-thresholds.json";

    private volatile SegmentationThresholds currentThresholds;

    @PostConstruct
    void initialize() {
        currentThresholds = loadDefaultThresholds();
    }

    public SegmentationThresholds currentThresholds() {
        return currentThresholds;
    }

    public long start(SegmentationThresholds thresholds) {
        if (isRunning()) {
            throw new IllegalStateException("A customer segmentation batch is already running");
        }

        Properties parameters = new Properties();
        parameters.setProperty(SegmentationThresholds.JOB_PARAMETER, thresholds.toJson());

        long executionId = jobOperator().start(JOB_NAME, parameters);
        currentThresholds = thresholds;
        return executionId;
    }

    public Optional<BatchStatus> latestStatus() {
        JobOperator operator = jobOperator();

        try {
            List<JobInstance> instances = operator.getJobInstances(JOB_NAME, 0, 1);
            if (instances.isEmpty()) {
                return Optional.empty();
            }

            return operator.getJobExecutions(instances.getFirst()).stream()
                    .max(Comparator.comparing(JobExecution::getCreateTime))
                    .map(JobExecution::getBatchStatus);
        } catch (NoSuchJobException exception) {
            return Optional.empty();
        }
    }

    public boolean isRunning() {
        try {
            return !jobOperator().getRunningExecutions(JOB_NAME).isEmpty();
        } catch (NoSuchJobException exception) {
            return false;
        }
    }

    private SegmentationThresholds loadDefaultThresholds() {
        try (InputStream stream =
                     CustomerSegmentationService.class.getResourceAsStream(DEFAULT_THRESHOLDS)) {

            if (stream == null) {
                throw new IllegalStateException("Resource not found: " + DEFAULT_THRESHOLDS);
            }

            String json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return SegmentationThresholds.fromJson(json);
        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "Unable to load default segmentation thresholds", exception);
        }
    }

    private JobOperator jobOperator() {
        return BatchRuntime.getJobOperator();
    }
}
