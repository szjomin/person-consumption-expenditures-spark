package com.jm.flume.interceptor;


import com.google.common.collect.Lists;

import com.jm.request.ExpendituresInfoRequest;
import com.jm.utils.JSONUtil;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PceInterceptor implements Interceptor {
  private Logger LOG = LoggerFactory.getLogger(PceInterceptor.class);

  private boolean isUserPartition = true;

  public PceInterceptor(boolean isUserPartition) {
    this.isUserPartition = isUserPartition;
  }

  @Override
  public void initialize() {
    System.out.println("PceInterceptor initialize...");
  }

  @Override
  public Event intercept(Event event) {
    System.out.println("PceInterceptor intercept...");
    try {
      if (event == null || event.getBody() == null || event.getBody().length == 0) {
        return null;
      }

      System.out.println("===================body=" + new String(event.getBody()));

      if (isUserPartition) {

        long userId = 0;

        try {
          ExpendituresInfoRequest model =
                  JSONUtil.json2Object(new String(event.getBody()), ExpendituresInfoRequest.class);
          userId = model.getUserId();
        } catch (Exception e) {
          LOG.error("event body is Invalid,body=" + event.getBody(), e);
        }

        if (userId == 0) {
          return null;
        }

        Map<String, String> headerMap = event.getHeaders();

        if (null == headerMap) {
          event.setHeaders(new HashMap());
        }

        event.getHeaders().put("key", String.valueOf(userId));
      }

      return event;
    } catch (Exception e) {
      LOG.error("intercept error,body=" + event.getBody(), e);
      return event;
    }
  }

  @Override
  public List intercept(List<Event> events) {
    System.out.println("ExpendituresInfo Iterceptor interceptlist...");
    List<Event> out = Lists.newArrayList();
    for (Event event : events) {
      Event outEvent = intercept(event);
      if (outEvent != null) {
        out.add(outEvent);
      }
    }
    return out;
  }

  @Override
  public void close() {
    System.out.println("PceIterceptor close...");
  }

  public static class PceIterceptorBuilder implements Builder {

    boolean isUserPartition = true;

    @Override
    public void configure(Context context) {
      System.out.println("PceIterceptorBuilder configure...");
      isUserPartition = context.getBoolean("isUserPartition");
    }

    @Override
    public Interceptor build() {
      System.out.println("PceIterceptorBuilder build...");
      return new PceInterceptor(isUserPartition);
    }
  }
}
