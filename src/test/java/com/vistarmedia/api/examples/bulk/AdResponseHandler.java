package com.vistarmedia.api.examples.bulk;

import java.text.DecimalFormat;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.vistarmedia.api.result.AdResponseResult;
import com.vistarmedia.api.result.ErrorResult;

public class AdResponseHandler implements Runnable {
  private BlockingQueue<Future<AdResponseResult>> resultFutures = new ArrayBlockingQueue<Future<AdResponseResult>>(
                                                                    10000);

  private boolean                                 shutdownState = false;
  private long                                    numSuccess    = 0;
  private long                                    numErrors     = 0;
  private Long                                    lastTick      = null;

  public void handle(Future<AdResponseResult> resultFuture) {
    resultFutures.add(resultFuture);
  }

  public void stop() throws InterruptedException {
    shutdownState = true;
  }

  public void run() {
    while (resultFutures.size() > 0 || !shutdownState) {
      try {
        AdResponseResult result = resultFutures.take().get(800,
            TimeUnit.MILLISECONDS);
        if (result.isSuccess()) {
          result.getResult();
          numSuccess += 1;

          if (numSuccess % 100 == 0) {
            Long now = System.currentTimeMillis();
            System.out.println("---");
            System.out.println("Num Succes: " + numSuccess);
            System.out.println("Num Errors: " + numErrors);
            if (lastTick != null) {
              Long requests = 100L;
              Double seconds = (now - lastTick) / 1000d;
              System.out.print("Reqs/sec:   ");
              System.out.println(new DecimalFormat("#.##").format(requests
                  / seconds));
            }
            lastTick = now;
          }
        } else {
          handleError(result.getError());
          numErrors += 1;
        }
      } catch (Exception e) {
        numErrors += 1;
      }
    }
    System.out.println("--- (final)");
    System.out.println("Num Succes: " + numSuccess);
    System.out.println("Num Errors: " + numErrors);
  }

  private void handleError(ErrorResult error) {
    System.err.println(String.format("Error (%s): %s", error.getCode(),
        error.getMessage()));
  }

}
