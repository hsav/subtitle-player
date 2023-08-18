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
import com.lowbudget.subtitles.model.Settings;
import com.lowbudget.subtitles.model.SubtitleList;
import com.lowbudget.subtitles.model.SubtitleLoader;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class Actions {

  private final Action playAction;

  private final Action stopAction;

  private final Action loadAction;

  private final Action settingsAction;

  public Actions(final Player player) {
    this.playAction = createPlayAction(player);
    this.stopAction = createStopAction(player);
    this.loadAction = createLoadAction(player);
    this.settingsAction = createSettingsAction(player);

    player.addListener(new ModelListener());
    update(player);
  }

  public void update(Player player) {
    this.playAction.setEnabled(!player.isPlaying() && player.hasSubtitles());
    this.stopAction.setEnabled(player.isPlaying());
  }

  private class ModelListener implements Player.Listener {

    @Override
    public void onSubtitlesLoaded(Player player) {
      update(player);
    }

    @Override
    public void onClockTick(Player player) {
      update(player);
    }

    @Override
    public void onStarted(Player player) {
      update(player);
    }

    @Override
    public void onStopped(Player player) {
      update(player);
    }

    @Override
    public void onSettingsChanged(Player player) {
      update(player);
    }
  }

  private static Action createPlayAction(final Player player) {
    Icon icon = UIUtils.loadIcon("play-button.png");
    return new AbstractAction("", icon) {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.start();
      }
    };
  }

  private static Action createStopAction(Player player) {
    Icon icon = UIUtils.loadIcon("stop-button.png");
    return new AbstractAction("", icon) {
      @Override
      public void actionPerformed(ActionEvent e) {
        player.stop();
      }
    };
  }

  private static Action createLoadAction(Player player) {
    return new AbstractAction("Load...") {

      @Override
      public void actionPerformed(ActionEvent e) {

        final JFileChooser fc = new JFileChooser();
        File lastFolder =
            player.getLastFolder() != null ? new File(player.getLastFolder()) : new File(".");
        fc.setCurrentDirectory(lastFolder);
        fc.setFileFilter(SUBTITLE_FILTER);

        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          if (file != null && file.exists() && file.isFile()) {
            log.info("Loading subtitles from file: {}", file);
            SubtitleList subtitles = SubtitleLoader.load(file);
            player.loadSubtitles(subtitles);
            player.setLastFolder(file.getParentFile().getAbsolutePath());
          } else {
            log.error("Invalid file specified: {}", file);
          }
        }
      }
    };
  }

  private static Action createSettingsAction(Player player) {
    return new AbstractAction("Settings...") {

      @Override
      public void actionPerformed(ActionEvent e) {

        Settings settings = player.getSettings();
        SettingsDialog dialog = new SettingsDialog(settings);

        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
          Settings newSettings = dialog.getNewSettings();
          player.setSettings(newSettings);
        }
      }
    };
  }

  private static final FileFilter SUBTITLE_FILTER =
      new FileFilter() {
        @Override
        public boolean accept(File f) {
          if (f.isDirectory()) {
            return true;
          }
          Set<String> allowed = new HashSet<>();
          allowed.add("srt");
          return allowed.contains(UIUtils.getExtension(f));
        }

        @Override
        public String getDescription() {
          return "SRT files";
        }
      };
}
