import org.spockframework.runtime.model.parallel.ExecutionMode

runner {
    parallel {
        enabled true
        defaultExecutionMode = ExecutionMode.SAME_THREAD
    }
}