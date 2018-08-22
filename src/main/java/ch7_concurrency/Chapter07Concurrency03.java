package ch7_concurrency;

import java.util.*;
import java.util.concurrent.*;

public class Chapter07Concurrency03 {
    public static void main(String[] args) {
//        demo1_Executor1();
//        demo1_Executor2();
//        demo2_submitRunnableGetFuture();
//        demo3_submitRunnableGetResult();
//
//        demo4_submitCallableGetFuture();
//        demo5_submitCallableGetResult();

        demo6_invokeAllCallables();
        demo7_invokeAnyCallables();
        demo8_submitCallables();
    }

    private static void demo8_submitCallables() {
        System.out.println("demo8=======================================");
        final List<Future<Result>> futures = Collections.synchronizedList(new ArrayList<>());
        List<CallableWorker<Result>> callables = createListOfCallables(1);
        int poolSize = 3;
        ExecutorService execService = Executors.newFixedThreadPool(poolSize);
        for (CallableWorker<Result> callable : callables) {
            try {
                Future<Result> future = execService.submit(callable);
                futures.add(future);
            } catch (Exception e) {
                System.out.println("Caught around execService.submit(" + callable.getName() + "): "
                        + e.getClass().getName());
            }
        }
        shutdownAndCancelTasks(execService, 2, futures);
        printResults(futures, 2);
    }

    private static void demo7_invokeAnyCallables() {
        List<CallableWorker<Result>> callables = createListOfCallables(2);

        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        invokeAnyCallables(execService, 1, callables);

        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        invokeAnyCallables(execService, 1, callables);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        invokeAnyCallables(execService, 1, callables);
    }

    private static void invokeAnyCallables(ExecutorService execService, int shutdownDelaySec, List<CallableWorker<Result>> callables) {
        Result result = null;
        try {
            result = execService.invokeAny(callables, shutdownDelaySec, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("Caught around execService.invokeAny(): " + ex.getClass().getName());
        }
        shutdownAndCancelTasks(execService, shutdownDelaySec, new ArrayList<>());
        if (result == null) {
            System.out.println("No result from execService.invokeAny()");
        } else {
            System.out.println("Worker " + result.getWorkerName() + " slept "
                    + result.getSleepSec() + " sec. Result = " + result.getResult());
        }
    }

    private static void shutdownAndCancelTasks(ExecutorService execService, int shutdownDelaySec, List<Future<Chapter07Concurrency03.Result>> futures) {
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

    private static void demo6_invokeAllCallables() {
        List<CallableWorker<Result>> callables = createListOfCallables(1);
        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        invokeAllCallables(execService, 6, callables);

        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        invokeAllCallables(execService, 3, callables);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        invokeAllCallables(execService, 1, callables);
    }

    private static void invokeAllCallables(ExecutorService execService, int shutdownDelaySec, List<CallableWorker<Result>> callables) {
        List<Future<Result>> futures = new ArrayList<>();
        try {
            futures = execService.invokeAll(callables, shutdownDelaySec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Caught around execService.invokeAll(): " + e.getClass().getName());
        }
        try {
            execService.shutdown();
            System.out.println("Waiting for " + shutdownDelaySec + " sec before terminating all tasks...");
            execService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Caught around execService.awaitTermination(): " + e.getClass().getName());
        } finally {
            if (!execService.isTerminated()) {
                System.out.println("Terminating remaining running tasks...");
                for (Future<Result> future : futures) {
                    if (!future.isDone() && !future.isCancelled()) {
                        try {
                            System.out.println("Cancelling task " + future.get(shutdownDelaySec, TimeUnit.SECONDS).getWorkerName() + "...");
                            future.cancel(true);
                        } catch (Exception ex) {
                            System.out.println("Caught while cancelling task: " + ex.getClass().getName());
                        }
                    }
                }
            }
            System.out.println("Calling execService.shutdownNow()...");
            execService.shutdownNow();
        }
        printResults(futures, shutdownDelaySec);
    }

    private static List<CallableWorker<Result>> createListOfCallables(int nSec) {
        return List.of(new CallableWorkerImpl("One", nSec),
                new CallableWorkerImpl("Two", 2 * nSec),
                new CallableWorkerImpl("Three", 3 * nSec));
    }

    private static void demo5_submitCallableGetResult() {
        CallableWorker callable = new CallableWorkerImpl("One", 2);

        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        submitCallableGetResult(execService, 1, callable);

        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        submitCallableGetResult(execService, 1, callable);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        submitCallableGetResult(execService, 1, callable);
    }

    private static void submitCallableGetResult(ExecutorService execService, int shutdownDelaySec, CallableWorker<Result> callable) {
        Result result = null;
        try {
            result = execService.submit(callable).get(shutdownDelaySec, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Caught around execService.submit(" + callable.getName() + ").get(): "
                    + e.getClass().getName());
        }
        shutdownAndCancelTask(execService, shutdownDelaySec, new ArrayList<>());
        System.out.println("Worker " + callable.getName() + " slept " + callable.getSleepSec() + " sec. Result = " + (result == null ? null : result.getResult()));
    }

    private static void demo4_submitCallableGetFuture() {
        CallableWorker callable = new CallableWorkerImpl("One", 2);
        System.out.println("demo4==============================");
        System.out.println();
        System.out.println("Executors.newSingleThreadExecutor():");
        ExecutorService execService = Executors.newSingleThreadExecutor();
        submitCallableGetFuture(execService, 1, callable);

        System.out.println();
        System.out.println("Executors.newCachedThreadPool():");
        execService = Executors.newCachedThreadPool();
        submitCallableGetFuture(execService, 1, callable);

        System.out.println();
        int poolSize = 3;
        System.out.println("Executors.newFixedThreadPool(" + poolSize + "):");
        execService = Executors.newFixedThreadPool(poolSize);
        submitCallableGetFuture(execService, 1, callable);

    }

    private static void submitCallableGetFuture(ExecutorService execService, int shutdownDelaySec, CallableWorker callable) {
        List<Future<Result>> futures = new ArrayList<>();
        try {
            Future<Result> future = execService.submit(callable);
            futures.add(future);
        } catch (Exception e) {
            System.out.println("Caught around execService.submit(" + callable.getName() + "): " + e.getClass().getName());
        }
        shutdownAndCancelTask(execService, shutdownDelaySec, futures);
    }

    private interface CallableWorker<Result> extends Callable<Chapter07Concurrency03.Result> {
        default String getName() {
            return "Anonymous";
        }

        default int getSleepSec() {
            return 1;
        }
    }

    private static class CallableWorkerImpl implements CallableWorker<Result> {
        private String name;
        private int sleepSec;

        public CallableWorkerImpl(String name, int sleepSec) {
            this.name = name;
            this.sleepSec = sleepSec;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getSleepSec() {
            return sleepSec;
        }

        @Override
        public Result call() throws Exception {
            try {
                Thread.sleep(sleepSec * 1000);
            } catch (InterruptedException e) {
                System.out.println("Caught in CallableWorkerImpl: " + e.getClass().getName());
            }
            return new Result(name, sleepSec, 42);
        }
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
        System.out.println("shutdownDelaySec = " + shutdownDelaySec + ", threadSleepsSec = " + threadSleepsSec);
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
