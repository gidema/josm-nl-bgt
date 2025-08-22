package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class TaskRunner {
    static final int NTHREADS = 10;

    public static TaskStatus runTasks(List<FutureTask<TaskStatus>> tasks) {
        // Currently, tasks are allowed to be null. Make sure we handle only non-null tasks
        List<FutureTask<TaskStatus>> realTasks = tasks.stream().filter(Objects::nonNull).collect(Collectors.toList());
        ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
        realTasks.forEach(executor::submit);
        try {
            while (true) {
                boolean allDone = true;
                for (FutureTask<TaskStatus> task : realTasks) {
                    allDone &= task.isDone();
                }
                if (allDone) {
                    executor.shutdown();
                    List<TaskStatus> taskStatuses = new ArrayList<>(realTasks.size());
                    for (FutureTask<TaskStatus> task : realTasks) {
                        TaskStatus taskStatus;
                        try {
                            taskStatus = task.get();
                        }
                        catch (ExecutionException e) {
                            taskStatus = new TaskStatus(null, null, e);
                        }
                        taskStatuses.add(taskStatus);
                    }
                    return new TaskStatus(taskStatuses);
                }
                Thread.sleep(500L, 0);
            }
        }
        catch (InterruptedException e) {
            executor.shutdownNow();
            realTasks.forEach(task -> task.cancel(true));
            return new TaskStatus(true);
        }
        catch (Exception e) {
            return new TaskStatus(null, e.getMessage(), e);
        }
    }

}
