# Async Tasks Simplified
 
Async Tasks Simplified is a simple and powerful class designed for use in Java and Android. Async Tasks Simplified allows you to run slow or time-consuming code in another background thread, such as network request code, file processing, etc. Async Tasks Simplified was based on but not copied from Android's AsyncTask class, which is now marked Obsolete by Google. Async Tasks Simplified was born with the idea of bringing a simple, clean and intuitive code to do background tasks.

# How it works?

Async Tasks Simplified works much like the standard Android AsyncTask class. After a few years the AsyncTask API was marked Obsolete by Google and the Async Tasks Simplified was created to be a simple replacement for the AsyncTask class. Written in Java and focused for use in Java, Async Tasks Simplified uses the Concurrent class with Java's Executors and Threads and allows you to create a very clean and simple code where you establish what code you want to be executed in UI/Main Thread and which code you want to run in Background (on another Thread).

As Async Tasks Simplified gives you the option to run your code in normal Threads or in native Java Executors, you are able to choose for example if your background code should run in Serial with other Async Tasks in a single Thread, or if your Async Task should run in parallel with other Async Tasks in a Thread Pool.

# How to use?

It's quite simple! You just clone this repository and then copy the code from the "AsyncTaskSimplified.java" file found inside the "Async-Tasks-Simplified-Source" folder of this repository and then put it in your project. You can create your own AAR using this code, your own class, etc.

Below is an example code using the Async Task Simplified class.

```java
new AsyncTaskSimplified(context, new AsyncTaskSimplified.Listener() {
        @Override
        public void onStartTask_RunMainThread(Context context, String[] startParameters) {
            //Run before background code, this will run on UI/Main Thread
        }

        @Override
        public String[] onExecuteTask_RunBackground(Context context, String[] startParameters, AsyncTaskSimplified.ThreadTools threadTools) {
            //Here is the code that should be executed in Background, this will run on a new Background Thread and return some result
            return new String[0];
        }

        @Override
        public void onNewProgress_RunMainThread(Context context, String progressOfBackground) {
            //Run on call "threadTools.ReportNewProgress()" from background code, this will run on UI/Main Thread
        }

        @Override
        public void onDoneTask_RunMainThread(Context context, String[] resultOfBackground) {
            //Run after background code, this will run on UI/Main Thread
        }
    }, new String[]{"Parameter 1", "Parameter 2", "Parameter 3"}).Execute(AsyncTaskSimplified.ExecutionMode.ExecutorServiceThreadSingle);
```

Remember that each method of this class has a description that can be seen in the auto complete of your IDE (like Android Studio) or in the class itself, so you can read the descriptions of the methods to better understand how each one works and its details.

# Support projects like this

If you liked this Class and found it useful for your projects, please consider making a donation (if possible). This would make it even more possible for me to create and continue to maintain projects like this, but if you cannot make a donation, it is still a pleasure for you to use it! Thanks! 😀

<br>

<p align="center">
    <a href="https://www.paypal.com/donate/?hosted_button_id=MVDJY3AXLL8T2" target="_blank">
        <img src="Async-Tasks-Simplified-Source/Resources/paypal-donate.png" alt="Donate" />
    </a>
</p>

<br>

<p align="center">
Created with ❤ by Marcos Tomaz
</p>