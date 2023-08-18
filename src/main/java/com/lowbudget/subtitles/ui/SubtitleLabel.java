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

import static com.lowbudget.subtitles.ui.UIConstants.BLACK_SEMI_TRANSPARENT;
import static com.lowbudget.subtitles.ui.UIConstants.fromColor;

import com.lowbudget.subtitles.model.Player;
import com.lowbudget.subtitles.model.Settings;
import com.lowbudget.subtitles.model.Subtitle;
import java.awt.*;
import javax.swing.*;

// TODO: wrap text/handle change of lines. HTML is one option but occupies all the panel space
public class SubtitleLabel extends JLabel {

  public SubtitleLabel(Player player) {

    // because we use an alpha value in the background color the component needs to not be opaque
    // and additionally paint the background itself.
    // The reason is explained here:
    // https://tips4java.wordpress.com/2009/05/31/backgrounds-with-transparency/
    setOpaque(false);
    setForeground(Color.white);

    applyLabelBackgroundColor(player.getSettings());
    applyFontSettings(player.getSettings());
    setSubtitle(null);

    // enable to see the borders of the label
    // setBorder(BorderFactory.createLineBorder(Color.yellow)); //NOSONAR

    // box layout uses these to center the component
    setAlignmentX(0.5f);
    setAlignmentY(0.5f);

    // how the text is centered inside the label
    setVerticalAlignment(SwingConstants.CENTER);
    setHorizontalAlignment(SwingConstants.CENTER);

    player.addListener(new ModelListener());
  }

  private void applyLabelBackgroundColor(Settings settings) {
    setBackground(fromColor(BLACK_SEMI_TRANSPARENT, settings.getOpacity()));
  }

  private void applyFontSettings(Settings settings) {
    setFont(UIUtils.toFont(settings));
  }

  @Override
  protected void paintComponent(Graphics g) {
    // custom paintComponent to paint the background manually.
    // This is needed because we want to use a transparent background color
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }

  private void setSubtitle(Subtitle subtitle) {
    if (subtitle == null) {
      // to always keep the label's height to fit at least one line
      setText("\n");
      return;
    }
    String text = subtitle.getText().replace("\n", "<br/>");
    setText("<html>" + text + "</html>");
  }

  private class ModelListener extends Player.Adapter {
    @Override
    public void onClockTick(Player player) {
      Subtitle subtitle = player.getSubtitle();
      setSubtitle(subtitle);
    }

    @Override
    public void onSettingsChanged(Player player) {
      Settings settings = player.getSettings();
      applyFontSettings(settings);
      Color c = getBackground();
      Color newColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), settings.getOpacity());
      setBackground(newColor);
    }
  }
}
