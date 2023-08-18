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

import com.lowbudget.subtitles.model.Settings;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import lombok.SneakyThrows;

public class UIUtils {

  private UIUtils() {
    // not allow instantiation
  }

  public static Font toFont(Settings settings) {
    return new Font(settings.getFontName(), settings.getFontStyle(), settings.getFontSize());
  }

  /*
   * Get the extension of a file.
   */
  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }

  @SneakyThrows
  public static Icon loadIcon(String resourceName) {
    BufferedImage image =  ImageIO.read(ClassLoader.getSystemResource(resourceName));
    return new ImageIcon(image);
  }

  public static String format(int elapsed) {
    long hours = TimeUnit.MILLISECONDS.toHours(elapsed);
    elapsed -= TimeUnit.HOURS.toMillis(hours);

    long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed);
    elapsed -= TimeUnit.MINUTES.toMillis(minutes);

    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed);

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }
}
