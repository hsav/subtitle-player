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

package com.lowbudget.subtitles;

import com.lowbudget.subtitles.model.Clock;
import com.lowbudget.subtitles.model.Player;
import com.lowbudget.subtitles.model.Settings;
import com.lowbudget.subtitles.ui.Actions;
import com.lowbudget.subtitles.ui.MainWindow;
import com.lowbudget.subtitles.ui.timer.SwingClockTimerFactory;
import javax.swing.SwingUtilities;

public class Application {

  public static void main(String[] args) {

    SwingUtilities.invokeLater(
        () -> {
          // create the clock that drives the player
          Clock clock = new Clock(SwingClockTimerFactory.newFactory());

          // player model
          Player model = new Player(clock, Settings.loadFromFileOrDefault());

          Actions actions = new Actions(model);

          // player window
          MainWindow window = new MainWindow(model, actions);
          window.pack();
          window.setLocationRelativeTo(null);
          window.setVisible(true);
        });
  }
}
