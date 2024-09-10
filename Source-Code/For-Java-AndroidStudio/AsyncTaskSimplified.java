package xyz.windsoft.asynctaskssimplified;

import android.os.Handler;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskSimplified {

    /*
    / This class make possible to do background tasks in other thread
    / in a easy and simplified way.
    */

    //Listeners interface
    public interface Listener{
        void onStartTask_RunMainThread(Context context, String[] startParameters);
        String[] onExecuteTask_RunBackground(Context context, String[] startParameters, ThreadTools threadTools);
        void onNewProgress_RunMainThread(Context context, String progressOfBackground);
        void onDoneTask_RunMainThread(Context context, String[] resultOfBackground);
    }

    //Enums of this class
    public enum ExecutionMode{
        ExecutorServiceThreadSingle,
        ExecutorServiceThreadPool,
        NewDefaultThread
    }

    //Auxiliary classes
    public class ThreadTools{
        //This class will be passed into the event "onExecuteTask_RunBackground" in parameters and will be used to help the user to do useful things

        //Private variables
        private AsyncTaskSimplified parentAsyncTaskSimplified = null;

        //Core methods
        public ThreadTools(AsyncTaskSimplified parentAsyncTaskSimplified){
            //Initialize this class
            this.parentAsyncTaskSimplified = parentAsyncTaskSimplified;
        }

        /**
         * Allows the "onExecuteTask_RunBackground" event (which runs on another Background Thread)
         * to report progress and then the "onNewProgress_RunMainThread" (which runs on UI/Main Thread) event will be called
         * to update something in the UI or current Main Thread.
         *
         * @param newProgress The new progress information from Background Thread.
         */
        public void ReportNewProgress(String newProgress){
            //Call the parent async task simplified object to report progress
            parentAsyncTaskSimplified.ReportProgress(newProgress);
        }

        /**
         * This method will make the Background Thread that is running the code contained in the "onExecuteTask_RunBackground"
         * event sleep for a few milliseconds defined in the parameter.
         *
         * @param timeOfSleepInMs The time the Thread will sleep (in milliseconds).
         */
        public void MakeThreadSleep(long timeOfSleepInMs){
            //Meke the Thread that called this method sleep
            try { Thread.sleep(timeOfSleepInMs); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    //Cache variables
    private boolean isAlreadyRunning = false;

    //Private variables
    private Context callerContext = null;
    private String[] startParameters = null;
    private Listener listener = null;

    /**
     * Initialize the "Async Task Simplified" object to do any background tasks!
     *
     * <br><br><b>WARN:</b> THIS CLASS MUST ONLY BE STARTED FROM THE UI/MAIN THREAD!
     *
     * @param context The context that this Async Task Simplified is being called.
     * @param listener The listener to run the code of Task. The listener has a total of 4 events. Each
     *                 event represents a phase of the Async Task's processing.
     *                 <br><br>
     *                 onStartTask_RunMainThread - The code for this event runs on the UI/Main Thread. The code for
     *                 this event is executed before the Async Task starts. In the parameter of this event you find
     *                 the String array that contains the parameters you provided when starting the Async Task.
     *                 <br><br>
     *                 onExecuteTask_RunBackground - The code for this event runs on the Background Thread. In the parameter of
     *                 this event you find the String array that contains the parameters you provided when starting the Async Task. The code
     *                 inside this event will be executed in another Thread, in a background Thread that is not the
     *                 UI/Main Thread. From within this event it is not possible to do anything that would only be
     *                 possible from the UI/Main Thread, such as controlling the UI, Activity, manipulating UI
     *                 components and so on. Here inside this event is where you should put all the heavy or time
     *                 consuming code that you would like to run in the background. This event also brings you an object
     *                 called "ThreadTools" that you can use to report progress. Whenever you call the "ThreadTools"
     *                 object to report progress, the next method ("onNewProgress_RunMainThread") will be called and you
     *                 can for example update things in the UI and etc. At the end of this event's code, you can return a
     *                 String Array that will be the result of your code runned by this event, in addition, this array
     *                 response will be sent to the "onDoneTask_RunMainThread" event as a parameter as well.
     *                 <br><br>
     *                 onNewProgress_RunMainThread - The code for this event runs on the UI/Main Thread. The code inside this
     *                 event will be executed in the UI/Main Thread and this event will always be called when you use the
     *                 "ThreadTools" object contained in the "onExecuteTask_RunBackground" event to call ReportNewProgress() method, that
     *                 will run in the background. You can use this method together with "ThreadTools" to communicate with the UI or
     *                 update it as the background task moves.
     *                 <br><br>
     *                 onDoneTask_RunMainThread - The code for this event runs on the UI/Main Thread. The code inside this
     *                 event will be executed shortly after the code contained in the "onExecuteTask_RunBackground" event
     *                 completes. In addition, this event takes as a parameter a String array resulting from the processing
     *                 done after the execution of the event code "onExecuteTask_RunBackground".
     * @param startParameters The parameters array that will be used by the Task. Parameters will be available for
     *                        "onStartTask_RunMainThread" and "onExecuteTask_RunBackground" events as well.
     */
    public AsyncTaskSimplified(Context context, Listener listener, String[] startParameters){
        //If is already running, stop this method
        if(isAlreadyRunning == true)
            return;

        //Fill this class
        this.callerContext = context;
        this.listener = listener;
        this.startParameters = startParameters;
    }

    /**
     * This method causes the task to start running immediately based on the chosen mode.
     *
     * <br><br><b>WARN:</b> THIS METHOD MUST ONLY BE RUN FROM THE UI/MAIN THREAD!
     *
     * @param executionMode The mode the Task will be executed.
     *                      <br><br>
     *                      The "ExecutorServiceThreadSingle" will run the task on a single core and
     *                      queue it to be executed with other tasks that also have a "ExecutorServiceThreadSingle"
     *                      mode and in addition the task will be executed using Java's Executor Service.
     *                      <br><br>
     *                      The "ExecutorServiceThreadPool" will use Java's Executors Service, however, it
     *                      will execute its task in a Thread Pool that support a Cache for application
     *                      acceleration and its task will also be executed in parallel with other tasks
     *                      that also use the "ExecutorServiceThreadPool" mode.
     *                      <br><br>
     *                      The "NewDefaultThread" will run your task in Java's common "Thread"
     *                      class and will not be queued as it will run in parallel with other
     *                      tasks that also use "NewDefaultThread" mode.
     */
    public void Execute(ExecutionMode executionMode){
        //If is already running, stop this method
        if(isAlreadyRunning == true)
            return;

        //Inform that is running now
        isAlreadyRunning = true;

        //Call the "onStartTask_RunMainThread" event on Main Thread
        listener.onStartTask_RunMainThread(callerContext, startParameters);

        //If is the execution mode "ExecutorServiceThreadSingle" (Serial)
        if(executionMode == ExecutionMode.ExecutorServiceThreadSingle)
            ((ExecutorService) Executors.newSingleThreadExecutor()).execute(() -> {

                //Call the "onExecuteTask_RunBackground" event in Background Thread
                String[] resultOfBackground = listener.onExecuteTask_RunBackground(callerContext, startParameters, new ThreadTools(this));
                //Call the "onDoneTask_RunMainThread" event on Main Thread
                new Handler(callerContext.getMainLooper()).post(() -> {
                    listener.onDoneTask_RunMainThread(callerContext, resultOfBackground);
                });

            });

        //If is the execution mode "ExecutorServiceThreadPool" (Parallel)
        if(executionMode == ExecutionMode.ExecutorServiceThreadPool)
            ((ExecutorService) Executors.newCachedThreadPool()).execute(() -> {

                //Call the "onExecuteTask_RunBackground" event in Background Thread
                String[] resultOfBackground = listener.onExecuteTask_RunBackground(callerContext, startParameters, new ThreadTools(this));
                //Call the "onDoneTask_RunMainThread" event on Main Thread
                new Handler(callerContext.getMainLooper()).post(() -> {
                    listener.onDoneTask_RunMainThread(callerContext, resultOfBackground);
                });

            });

        //If is the execution mode "NewDefaultThread" (Parallel)
        if(executionMode == ExecutionMode.NewDefaultThread)
            new Thread(() -> {

                //Call the "onExecuteTask_RunBackground" event in Background Thread
                String[] resultOfBackground = listener.onExecuteTask_RunBackground(callerContext, startParameters, new ThreadTools(this));
                //Call the "onDoneTask_RunMainThread" event on Main Thread
                new Handler(callerContext.getMainLooper()).post(() -> {
                    listener.onDoneTask_RunMainThread(callerContext, resultOfBackground);
                });

            }).start();
    }

    /**
     * <b>WARN:</b> This method must not be used because it is a method of internal use of the Async Task Simplified class.
     */
    public void ReportProgress(String newProgress){
        //If is not running, stop this method
        if(isAlreadyRunning == false)
            return;

        //Call the "onNewProgress_RunMainThread" event on Main Thread
        new Handler(callerContext.getMainLooper()).post(() -> {
            listener.onNewProgress_RunMainThread(callerContext, newProgress);
        });
    }
}
