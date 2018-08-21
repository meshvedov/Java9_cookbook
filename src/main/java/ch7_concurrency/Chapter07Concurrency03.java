package ch7_concurrency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Chapter07Concurrency03 {
    public static void main(String[] args) {
        demo1_Executor1();
        demo1_Executor2();
        demo2_submitRunnableGetFuture();
        demo3_submitRunnableGetResult();

        demo4_submitCallableGetFuture();
    }

    private static void demo3_submitRunnableGetResult() {
        System.out.println();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Future<Result>> futures = new ArrayList<>();
        try {
            Future<Result> future = executorService.submit(new RunnableWorkerImpl("One", 2), new Result("One", 2, 42));
            futures.add(future);
            Future<Result> future1 = executorService.submit(new RunnableWorkerImpl("Two", 4),
                    new Result("Two", 4, 43));
            futures.add(future1);
            Future<Result> future2 = executorService.submit(new RunnableWorkerImpl("Three", 6),
                    new Result("Three", 6, 44));
            futures.add(future2);
        } catch (Exception e) {
            System.out.println("Caught around execService.submit(One): " + e.getClass().getName());
        }
        System.out.println("Checking status of the tasks...");
        Set<String> set = new HashSet<>();
        while (set.size() < 3) {
            for (Future<Result> future : futures) {
                if (future.isDone()) {
                    try {
                        String name = future.get(1, TimeUnit.SECONDS).getWorkerName();
                        if (!set.contains(name)) {
                            System.out.println("Task " + name + " is done.");
                            set.add(name);
                        }
                    } catch (Exception e) {
                        System.out.println("Caught around future.get(): " + e.getClass().getName());
                    }
                }
            }
        }
        shutdownAndCancelTask(executorService, 2, futures);
        printResults(futures, 2);
    }

    private static void printResults(List<Future<Result>> futures, int timeoutSec) {
        System.out.println("Results from futures:");
        if (futures == null || futures.size() == 0) {
            System.out.println("No results. Futures" + (futures == null ? " = null" : ".size()=0"));
        } else {
            for (Future<Result> future : futures) {
                try {
                    if (future.isCancelled()) {
                        System.out.println("Worker is cancelled.");
                    } else {
                        Result result = future.get(timeoutSec, TimeUnit.SECONDS);
                        System.out.println("Worker " + result.getWorkerName() + " slept "
                                + result.getSleepSec() + " sec. Result = " + result.getResult());
                    }
                } catch (Exception ex) {
                    System.out.println("Caught while getting result: " + ex.getClass().getName());
                }
            }
        }
    }

    private static void shutdownAndCancelTask(ExecutorService execService, int shutdownDelaySec, List<Future<Result>> futures) {
        try {
            execService.shutdown();
            System.out.println("Waiting for " + shutdownDelaySec + " sec before shutting down service...");
            execService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("Caught around execService.awaitTermination(): " + ex.getClass().getName());
        } finally {
            if (!execService.isTerminated()) {
                System.out.println("Terminating remaining running tasks...");
                for (Future<Result> future : futures) {
                    if (future.isDone() && !future.isCancelled()) {
                        System.out.println("Cancelling task...");
                        future.cancel(true);
                    }
                }
            }
            System.out.println("Calling execService.shutdownNow()...");
            List<Runnable> l = execService.shutdownNow();
            System.out.println(l.size() + " tasks were waiting to be executed. Service stopped.");
        }
    }

    private static class Result {
        private int sleepSec, result;
        private String workerName;

        public Result(String workerName, int sleptSec, int result) {
            this.workerName = workerName;
            this.sleepSec = sleptSec;
            this.result = result;
        }

        public String getWorkerName() {
            return this.workerName;
        }

        public int getSleepSec() {
            return this.sleepSec;
        }

        public int getResult() {
            return this.result;
        }
    }

    private static void demo2_submitRunnableGetFuture() {
        RunnableWorker runnable = new RunnableWorkerImpl("One", 2);
        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        submitRunnable(execService, 1, runnable);

        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        submitRunnable(execService, 1, runnable);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        submitRunnable(execService, 1, runnable);
    }

    private static void submitRunnable(ExecutorService executorService, int shutdownDelaySec, RunnableWorker runnableWorker) {
        String name = runnableWorker.getName();
        Future future = null;
        try {
            future = executorService.submit(runnableWorker);
        } catch (Exception e) {
            System.out.println("Caught around execService.submit(" + name + "): " + e.getClass().getName());
        }

        shutdownAndCancelTask(executorService, shutdownDelaySec, name, future);

        if (future == null) {
            System.out.println("Future of Worker " + name + " is null");
        } else {
            try {
                System.out.println("Worker " + name
                        + (future.isCancelled() ? " canceled" : " result: "
                        + future.get(shutdownDelaySec, TimeUnit.SECONDS)));
            } catch (Exception ex) {
                System.out.println("Caught while getting result: " + ex.getClass().getName());
            }
        }
    }

    private interface RunnableWorker extends Runnable {
        default String getName() {
            return "Anonymous";
        }
    }

    private static class RunnableWorkerImpl implements RunnableWorker {
        private String name;
        private int sleepSec;

        public RunnableWorkerImpl(String name, int sleepSec) {
            this.name = name;
            this.sleepSec = sleepSec;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void run() {
            try {
                System.out.println("Worker " + name + " is sleeping for " + sleepSec + " sec...");
                Thread.sleep(sleepSec * 1000);
                System.out.println("Worker " + name + " completed the job.");
            } catch (Exception ex) {
                System.out.println("Worker " + name + " interrupted: " + ex.getClass().getName());
            }
        }
    }

    private static void demo1_Executor1() {
        System.out.println("demo1 --------------------");
        int shutdownDelaySec = 1;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable runnable = () -> System.out.println("Worker One did the job");
        executorService.execute(runnable);
        runnable = () -> System.out.println("Worker Two did the job");
        Future future = executorService.submit(runnable);
        try {
            executorService.shutdown();
            executorService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Caught around executeService.awaitTermination(): " + e.getClass().getName());
        } finally {
            if (!executorService.isTerminated()) {
                if (future != null && !future.isDone() && !future.isCancelled()) {
                    System.out.println("Cancelling the task...");
                    future.cancel(true);
                }
            }
            List<Runnable> list = executorService.shutdownNow();
            System.out.println(list.size() + " tasks were waiting to be executed. Service stopped.");
        }
    }

    private static void shutdownAndCancelTask(ExecutorService executorService, int shutdownDelaySec, String name, Future future) {
        try {
            executorService.shutdown();
            System.out.println("Waiting for " + shutdownDelaySec + " sec before shutting down service...");
            executorService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Caught around execService.awaitTermination(): " + e.getClass().getName());
        } finally {
            if (!executorService.isTerminated()) {
                System.out.println("Terminating remaining running task...");
                if (future != null && !future.isDone() && !future.isCancelled()) {
                    System.out.println("Cancelling task " + name + "...");
                    future.cancel(true);
                }
            }
            System.out.println("Calling execService.shutdownNow()...");
            List<Runnable> l = executorService.shutdownNow();
            System.out.println(l.size() + " tasks were waiting to be executed. Service stopped.");
        }
    }

    private static void executeAndSubmit(ExecutorService execService, int shutdownDelaySec, int threadSleepsSec) {
        System.out.println("shutdownDelaySec = " + shutdownDelaySec + ", threadSleepsSec = " +threadSleepsSec);
        Runnable runnable = () -> {
            try {
                Thread.sleep(threadSleepsSec * 1000);
                System.out.println("Worker One did the job.");
            } catch (Exception ex) {
                System.out.println("Caught around One Thread.sleep(): " + ex.getClass().getName());
            }
        };
        execService.execute(runnable);
        runnable = () -> {
            try {
                Thread.sleep(threadSleepsSec * 1000);
                System.out.println("Worker Two did the job.");
            } catch (Exception ex) {
                System.out.println("Caught around Two Thread.sleep(): " + ex.getClass().getName());
            }
        };
        Future future = execService.submit(runnable);
        shutdownAndCancelTask(execService, shutdownDelaySec, "Two", future);
    }

    private static void demo1_Executor2() {

        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        executeAndSubmit(execService, 3, 1);


        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        executeAndSubmit(execService, 3, 1);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        executeAndSubmit(execService, 3, 1);

    }
}
