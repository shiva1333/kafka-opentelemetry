package io.opentelemetry.example.jaeger;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;


public final class JaegerExample {

  private final Tracer tracer;

  public JaegerExample(OpenTelemetry openTelemetry) {
    tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample");
  }

  private void myWonderfulUseCase() throws InterruptedException {
    // Generate a span
    Span rootSpan = this.tracer.spanBuilder("rootSpan").startSpan();

    try (Scope scope = rootSpan.makeCurrent()) {
      // Call a method that creates a child span
      System.out.println("Running parent span");
      doWork(Context.current());
    } finally {
      // End the root span
      rootSpan.end();
    }
  }

  private void doWork(Context parentContext) throws InterruptedException {
    SpanBuilder spanBuilder = tracer.spanBuilder("childSpan").setParent(parentContext);
    Span childSpan = spanBuilder.startSpan();
    try (Scope scope = childSpan.makeCurrent()) {
      Thread.sleep(1000);
      System.out.println("Doing some work in the child span...");
    } finally {
      childSpan.end();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    // Parsing the input
//    if (args.length < 1) {
//      System.out.println("Missing [endpoint]");
//      System.exit(1);
//    }
    String jaegerEndpoint = "http://localhost:4317";

    // it is important to initialize your SDK as early as possible in your application's lifecycle
    OpenTelemetry openTelemetry = ExampleConfiguration.initOpenTelemetry(jaegerEndpoint);

    // Start the example
    JaegerExample example = new JaegerExample(openTelemetry);
    // generate a few sample spans
    for (int i = 0; i < 10; i++) {
      example.myWonderfulUseCase();
    }

    System.out.println("Bye");
  }
}

