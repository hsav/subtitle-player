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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SubtitleLoader {

  public static final Pattern SEPARATOR = Pattern.compile("-->");
  private static final Pattern TIME_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2},\\d{3}");

  private SubtitleLoader() {
    // not allow instantiation
  }

  @SneakyThrows
  public static SubtitleList load(File file) {
    FileInputStream in = new FileInputStream(file);
    return load(in);
  }

  public static SubtitleList load(InputStream input) {

    DateTimeFormatter localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss,SSS");

    List<Subtitle> list = new ArrayList<>();

    try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name())) {
      int id;
      String start = null;
      String end = null;

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        log.trace("Parsed line: {}", line);

        id = Integer.parseInt(line.trim());

        if (scanner.hasNextLine()) {
          start = scanner.next(TIME_PATTERN);

          scanner.next(SEPARATOR);

          end = scanner.next(TIME_PATTERN);

          scanner.nextLine(); // read until end of line
        }

        String text = parseTextLine(scanner);

        Subtitle subtitle =
            new Subtitle(
                id, toMillis(start, localTimeFormatter), toMillis(end, localTimeFormatter), text);
        log.debug("Loaded subtitle: {}", subtitle);
        list.add(subtitle);
      }
    }

    return new SubtitleList(list);
  }

  private static int toMillis(String timeString, DateTimeFormatter localTimeFormatter) {
    LocalTime time = LocalTime.parse(timeString, localTimeFormatter);
    return (int) TimeUnit.NANOSECONDS.toMillis(time.toNanoOfDay());
  }

  private static String parseTextLine(Scanner scanner) {
    String line;
    StringBuilder text = new StringBuilder();
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      if (StringUtils.isNotEmpty(line)) {
        line = line.replace("\r\n", "").replace("\n", "");
        text.append(line)
            .append(" ")
            .append("\n");
      } else {
        break;
      }
    }
    return text.toString().trim();
  }
}
