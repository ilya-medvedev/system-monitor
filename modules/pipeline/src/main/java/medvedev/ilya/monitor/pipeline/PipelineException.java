package medvedev.ilya.monitor.pipeline;

public class PipelineException extends RuntimeException {
    public PipelineException(Exception cause) {
        super(cause);
    }
}
