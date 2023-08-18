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

package com.lowbudget.subtitles.ui.timer;

import com.lowbudget.subtitles.model.ClockTimer;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class SwingClockTimerFactory implements ClockTimer.Factory {

  /**
   * Every 40 ms i.e. a 25 frame rate / second
   */
  public static final int DEFAULT_TIMER_INTERVAL = 40;

  public static ClockTimer.Factory newFactory() {
    return new SwingClockTimerFactory();
  }

  @Override
  public ClockTimer createTimer() {
    return new SwingClockTimer(DEFAULT_TIMER_INTERVAL);
  }

  private static class SwingClockTimer implements ClockTimer {

    private final Timer timer;

    private ClockTimer.Listener timerListener;

    private long lastTick;

    public SwingClockTimer(int interval) {
      this.timer = new Timer(interval, this::onTimer);
    }

    private void onTimer(ActionEvent actionEvent) {
      // we cannot trust that the elapsed duration is equal to the timer's interval
      // the timer has some small delay which accumulates and eventually gets out of sync
      // So we keep track of the last tick to calculate the elapsed duration ourselves
      long now = System.currentTimeMillis();
      long elapsed = now - lastTick;
      timerListener.onTimer(elapsed);
      this.lastTick = now;
    }

    @Override
    public void start() {
      timer.start();
      this.lastTick = System.currentTimeMillis();
    }

    @Override
    public void stop() {
      timer.stop();
    }

    @Override
    public boolean isRunning() {
      return timer.isRunning();
    }

    @Override
    public void setListener(ClockTimer.Listener listener) {
      this.timerListener = listener;
    }

  }
}
