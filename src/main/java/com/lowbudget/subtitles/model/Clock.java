/*
 * MIT License
 *
 * Copyright (c) 2023 Charalampos Savvidis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.lowbudget.subtitles.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Clock {

  public int getElapsedDuration() {
    return this.elapsedDuration;
  }

  public int getTotalDuration() {
    return this.totalDuration;
  }

  public interface Listener {
    void onTick(long elapsedMillis, boolean lastTick);
  }

  private final Listeners<Listener> clockListeners = new Listeners<>();

  private int elapsedDuration;

  private int totalDuration;

  private final ClockTimer timer;

  public Clock(ClockTimer.Factory timerFactory) {
    this.timer = timerFactory.createTimer();
    this.timer.setListener(this::doTick);
  }

  /*package*/ void addListener(Listener listener) {
    this.clockListeners.add(listener);
  }

  public void start() {
    if (!timer.isRunning()) {
      log.debug("Clock started");
      timer.start();
    }
  }

  public void stop() {
    if (timer.isRunning()) {
      timer.stop();
      log.debug("Clock stopped");
    }
  }

  public boolean isRunning() {
    return this.timer.isRunning();
  }

  /*package*/ void init(int totalDuration) {
    this.totalDuration = totalDuration;
    this.elapsedDuration = 0;
  }

  /*package*/ void setElapsedDuration(int elapsed) {
    this.elapsedDuration = elapsed;
    if (elapsedDuration > totalDuration) {
      elapsedDuration = totalDuration;
      stop();
    }
  }

  private void doTick(long elapsedMillis) {
    this.elapsedDuration += elapsedMillis;

    boolean lastTick = false;
    if (this.elapsedDuration > totalDuration) {
      this.elapsedDuration = totalDuration;
    }
    log.trace("Tick: clock elapsed duration: {}", this.elapsedDuration);
    if (this.elapsedDuration >= totalDuration) {
      stop();
      lastTick = true;
    }
    final boolean isLastTick = lastTick;
    clockListeners.fireEvent(l -> l.onTick(elapsedMillis, isLastTick));
  }
}
