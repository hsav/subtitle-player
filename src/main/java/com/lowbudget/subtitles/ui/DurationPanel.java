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

package com.lowbudget.subtitles.ui;

import static com.lowbudget.subtitles.ui.UIUtils.format;

import com.lowbudget.subtitles.model.Player;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import lombok.RequiredArgsConstructor;

public class DurationPanel extends JPanel {

  private final JSlider slider;

  private final JLabel durationLabel;

  private boolean programmaticChange = true;

  private final JButton playButton;
  private final JButton stopButton;

  public DurationPanel(Player player, Actions actions) {
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

    // this component has 3 components laid out in a row

    // 1. a play and stop button from which only one is visible at a time
    this.playButton = addButton(actions.getPlayAction());
    this.stopButton = addButton(actions.getStopAction());
    stopButton.setVisible(false);

    // leave some horizontal space between the visible button and the slider
    add(Box.createHorizontalStrut(5));

    // 2. a slider that represents the duration elapsed
    this.slider = new JSlider(0, 0, 0);
    add(slider);
    slider.addChangeListener(new SliderChangeListener(player));

    // 3. a label that displays the duration elapsed in an hours/minutes/seconds format
    this.durationLabel = new JLabel("00:00:00");
    add(durationLabel);

    player.addListener(new ModelListener());
  }

  private JButton addButton(Action action) {
    JButton aButton = new JButton();
    aButton.setAction(action);
    add(aButton);
    return aButton;
  }

  @RequiredArgsConstructor
  private class SliderChangeListener implements ChangeListener {
    private final Player player;

    @Override
    public void stateChanged(ChangeEvent e) {
      // if the flag is set, the value was changed programmatically.
      // reset it on every tick.
      if (programmaticChange) {
        programmaticChange = false;
        return;
      }
      // the flag is not set - this means it is a user generated event
      if (player.hasSubtitles()) {
        player.seek(slider.getValue());
      }
    }
  }

  private class ModelListener extends Player.Adapter {

    @Override
    public void onSubtitlesLoaded(Player player) {
      slider.setValue(0);
      slider.setMaximum(player.getTotalDuration());
    }

    @Override
    public void onClockTick(Player player) {
      // ignore all change events for the slider since they are caused by setting the value
      // programmatically
      // we only want to respond when user is changing the value i.e. in a seek operation
      if (!player.isSeekOperation()) {
        programmaticChange = true;
      }

      // this will cause the slider's change listener to fire.
      // if this is not user generated we want to ignore the change event
      slider.setValue(player.getElapsedDuration());

      durationLabel.setText(format(player.getElapsedDuration()));
    }

    @Override
    public void onStarted(Player player) {
      playButton.setVisible(false);
      stopButton.setVisible(true);
    }

    @Override
    public void onStopped(Player player) {
      playButton.setVisible(true);
      stopButton.setVisible(false);
    }
  }

}
