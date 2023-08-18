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

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "player")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings implements Serializable {

  // TODO: add setting to display duration left instead of elapsed

  private String fontName;
  private int fontStyle;
  private int fontSize;

  private int opacity;

  private String lastOpenFolder;
  
  public static Settings defaultSettings() {
    return new Settings("Arial", Font.PLAIN, 24, 0, ".");
  }

  public static Settings loadFromFileOrDefault() {
    File configFile = new File(".", "config.xml");

    try (InputStream in = Files.newInputStream(configFile.toPath())) {
      JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      Settings settings = (Settings) jaxbUnmarshaller.unmarshal(in);
      log.info("Loaded settings from config file: {}", configFile.getAbsolutePath());
      return settings;
    } catch (Exception e) {
      log.info(
          "Could not read settings from config file: {}. Returning default settings",
          configFile.getAbsolutePath());
    }
    return defaultSettings();
  }

  public static void saveToFile(Settings settings) {
    File configFile = new File(".", "config.xml");

    try (OutputStream out = Files.newOutputStream(configFile.toPath())) {
      JAXBContext jaxbContext = JAXBContext.newInstance(Settings.class);
      Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
      jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      jaxbMarshaller.marshal(settings, out);

      log.info("Saved settings to config file: {}", configFile.getAbsolutePath());
    } catch (Exception e) {
      // this is called on application shutdown, so we can only log the exception
      log.info("Could not save settings to config file: {}", configFile.getAbsolutePath(), e);
    }
  }
}
