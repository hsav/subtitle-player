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

import com.lowbudget.subtitles.model.Player;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslucentPanel extends JPanel {

  @Getter private boolean viewMode;

  private final DurationPanel durationPanel;

  public TranslucentPanel(Player player, Actions actions) {
    setOpaque(false);
    setLayout(new BorderLayout());

    // arrange all our components at the bottom
    JPanel bottom = new JPanel();
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.PAGE_AXIS));
    bottom.setOpaque(false);

    // For this panel we want to be able to turn its visibility on/off.
    this.durationPanel = new DurationPanel(player, actions);

    bottom.add(durationPanel);

    // add the label displaying the subtitle
    bottom.add(new SubtitleLabel(player));

    // the margin we will leave from the bottom
    bottom.add(Box.createVerticalStrut(10));

    add(bottom, BorderLayout.PAGE_END);

    setViewMode(true);
  }

  public void setViewMode(boolean newValue) {
    this.viewMode = newValue;

    this.durationPanel.setVisible(!this.viewMode);
  }
}
