ParSeq Retry
============

Sometimes, especially when network calls are involved, tasks could fail for random reasons and in many cases simple retry would fix the problem. ParSeq Retry provides a flexible mechanism for configuring a retry behavior, allowing your application to be more resilient to intermittent failures.

Examples
========

The most simple example provides basic retry functionality:

```java
import static com.linkedin.parseq.retry.RetriableTask.withRetryPolicy;

Task<String> task1 = withRetryPolicy(RetryPolicy.attempts(3, 0), () -> Task.value("Hello, World!"));
Task<String> task2 = withRetryPolicy(RetryPolicy.duration(5000, 0), () -> Task.value("Hello, World!"));
```

It's possible for the task generator to take the current attempt number (zero-based): 

```java
Task<String> task = withRetryPolicy(RetryPolicy.attempts(3, 0), attempt -> Task.value("Current attempt: " + attempt));
```

It's also recommended to specify the operation name, so that it can be used for logging and naming of ParSeq tasks:

```java
Task<String> task = withRetryPolicy("sampleOperation", RetryPolicy.attempts(3, 0), () -> Task.value("Hello, World!"));
```

Instead of using predefined ```RetryPolicy``` helpers it's possible to use a builder class to achieve the same effect:

```java
RetryPolicy retryPolicy = new RetryPolicyBuilder()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .build();
```

Error classification
===============================

Not every task failure is intermittent and not every task result is valid. Retry policy can be configured to do error classification:

```java
Function<Throwable, ErrorClassification> errorClassifier = error -> error instanceof TimeoutException ? ErrorClassification.RECOVERABLE : ErrorClassification.FATAL;
RetryPolicy retryPolicy = new RetryPolicyBuilder()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .setErrorClassifier(errorClassifier)
    .build();
Task<Integer> task = withRetryPolicy(retryPolicy, () -> Task.value(Random.nextInt(10)));
```

There is also a ```ErrorClassification.SILENTLY_RECOVERABLE``` value which suppresses logging during retry operations. It does not suppress logging of fatal failures.

Termination policies
====================

To configure the number of retry attempts there are a few termination policies available:

```java
RetryPolicy retryPolicy = new RetryPolicyBuilder()
    .setTerminationPolicy(TerminationPolicy.limitDuration(1000))
    .build();
Task<String> task = withRetryPolicy(retryPolicy, () -> Task.value("Hello, World!"));
```

The ```limitDuration``` policy would limit the total duration of the task (including all retries) to the provided number of milliseconds. Be careful: if the task fails fast, that could mean a lot of retries!

Other available termination policies include ```requireBoth```, ```requireEither```, ```alwaysTerminate``` and ```neverTerminate```. It is possible to configure your own by implementing ```TerminationPolicy``` interface.

NOTE: When building a retry policy, there should be always some termination policy specified, otherwise exception will be thrown.

Backoff policies
================

Simple retry policy from the examples above would retry failed tasks immediately. Sometimes it's a good idea to have some delay between retry attempts. To implement some delay between retries, the number of milliseconds should be passed to the policy:

```java
Task<String> task = withRetryPolicy(RetryPolicy.attempts(3, 1000), () -> Task.value("Hello, World!"));
```

NOTE: Having non-zero backoff value specifies delay between completing previous attempt and scheduling new attempt. The counter starts after the task completes, not when it starts. For example, if your task takes exactly 500ms to complete and you have backoff time of 1000s then requests would come approximately every 1500ms.

Simple constant backoff is not always the best approach and variable delays would produce higher success ratios. It's possible to configure backoff policies:

```java
RetryPolicy retryPolicy = new RetryPolicyBuilder()
    .setTerminationPolicy(TerminationPolicy.limitAttempts(3))
    .setBackoffPolicy(BackoffPolicy.constant(1000))
    .build();
Task<String> task = withRetryPolicy(retryPolicy, () -> Task.value("Hello, World!"));
```

There are several backoff policies available: ```constant```, ```linear```, ```exponential```, ```fibonacci```, ```randomized```, ```selected```. It's also possible to create your own by implementing ```BackoffPolicy``` interface.