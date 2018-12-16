package medvedev.ilya.monitor.pipeline;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Pipeline<Context> implements Step<Context> {
    public final List<Step<Context>> steps;

    public void run(Context context) {
        steps.forEach(step -> {
            try {
                step.run(context);
            } catch (Exception e) {
                throw new PipelineException(e);
            }
        });
    }

    public static <Context> Pipeline<Context> create(Step<Context> step) {
        if (step instanceof Pipeline) {
            return ((Pipeline<Context>) step);
        }

        return new Pipeline<>(Collections.singletonList(step));
    }

    public Pipeline<Context> andThen(Step<Context> step) {
        List<Step<Context>> steps = Stream.of(this, Pipeline.create(step))
                .map(pipeline -> pipeline.steps)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return new Pipeline<>(steps);
    }
}
