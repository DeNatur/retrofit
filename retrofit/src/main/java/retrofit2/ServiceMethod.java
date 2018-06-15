/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import okhttp3.ResponseBody;

/** Adapts an invocation of an interface method into an HTTP call. */
final class ServiceMethod<ResponseT, ReturnT> {
  static <ResponseT, ReturnT> ServiceMethod<ResponseT, ReturnT> parseAnnotations(Retrofit retrofit,
      Method method) {
    return new ServiceMethod.Builder<ResponseT, ReturnT>(retrofit, method).build();
  }

  private final RequestFactory requestFactory;
  private final okhttp3.Call.Factory callFactory;
  private final CallAdapter<ResponseT, ReturnT> callAdapter;
  private final Converter<ResponseBody, ResponseT> responseConverter;

  ServiceMethod(Builder<ResponseT, ReturnT> builder) {
    requestFactory = builder.requestFactory;
    callFactory = builder.retrofit.callFactory();
    callAdapter = builder.callAdapter;
    responseConverter = builder.responseConverter;
  }

  /** Builds an HTTP request from method arguments. */
  okhttp3.Call toCall(@Nullable Object[] args) throws IOException {
    return callFactory.newCall(requestFactory.create(args));
  }

  ReturnT invoke(@Nullable Object[] args) {
    return callAdapter.adapt(new OkHttpCall<>(this, args));
  }

  /** Builds a method return value from an HTTP response body. */
  ResponseT toResponse(ResponseBody body) throws IOException {
    return responseConverter.convert(body);
  }

  /**
   * Inspects the annotations on an interface method to construct a reusable service method. This
   * requires potentially-expensive reflection so it is best to build each service method only once
   * and reuse it. Builders cannot be reused.
   */
  private static final class Builder<ResponseT, ReturnT> {
    final Retrofit retrofit;
    final Method method;

    RequestFactory requestFactory;
    Type responseType;
    Converter<ResponseBody, ResponseT> responseConverter;
    CallAdapter<ResponseT, ReturnT> callAdapter;

    Builder(Retrofit retrofit, Method method) {
      this.retrofit = retrofit;
      this.method = method;
    }

    ServiceMethod<ResponseT, ReturnT> build() {
      requestFactory = RequestFactory.parseAnnotations(retrofit, method);

      callAdapter = createCallAdapter();
      responseType = callAdapter.responseType();
      if (responseType == Response.class || responseType == okhttp3.Response.class) {
        throw methodError("'"
            + Utils.getRawType(responseType).getName()
            + "' is not a valid response body type. Did you mean ResponseBody?");
      }
      responseConverter = createResponseConverter();

      if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType)) {
        throw methodError("HEAD method must use Void as response type.");
      }

      return new ServiceMethod<>(this);
    }

    private CallAdapter<ResponseT, ReturnT> createCallAdapter() {
      Type returnType = method.getGenericReturnType();
      if (Utils.hasUnresolvableType(returnType)) {
        throw methodError(
            "Method return type must not include a type variable or wildcard: %s", returnType);
      }
      if (returnType == void.class) {
        throw methodError("Service methods cannot return void.");
      }
      Annotation[] annotations = method.getAnnotations();
      try {
        //noinspection unchecked
        return (CallAdapter<ResponseT, ReturnT>) retrofit.callAdapter(returnType, annotations);
      } catch (RuntimeException e) { // Wide exception range because factories are user code.
        throw methodError(e, "Unable to create call adapter for %s", returnType);
      }
    }

    private Converter<ResponseBody, ResponseT> createResponseConverter() {
      Annotation[] annotations = method.getAnnotations();
      try {
        return retrofit.responseBodyConverter(responseType, annotations);
      } catch (RuntimeException e) { // Wide exception range because factories are user code.
        throw methodError(e, "Unable to create converter for %s", responseType);
      }
    }

    private RuntimeException methodError(String message, Object... args) {
      return methodError(null, message, args);
    }

    private RuntimeException methodError(Throwable cause, String message, Object... args) {
      message = String.format(message, args);
      return new IllegalArgumentException(message
          + "\n    for method "
          + method.getDeclaringClass().getSimpleName()
          + "."
          + method.getName(), cause);
    }
  }
}
