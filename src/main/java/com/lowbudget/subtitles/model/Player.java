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

import java.util.function.Consumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Player {

  private final Listeners<Listener> listeners = new Listeners<>();

  private final Clock clock;

  private SubtitleList subtitles;

  @Getter private Subtitle subtitle;

  @Getter private boolean seekOperation;

  @Getter private Settings settings;

  public Player(Clock clock, Settings settings) {
    this.clock = clock;
    this.settings = settings;
    this.clock.addListener((elapsed, isLastTick) -> onClockTick(isLastTick));
  }

  private void onClockTick(boolean isLastTick) {
    updateCurrentSubtitle();
    if (isLastTick) {
      fireEvent(l -> l.onStopped(this));
    }
  }

  public void seek(int duration) {
    log.debug("Seeking to duration: {}", duration);

    // we do not need to check the clock state if the clock is running.
    // if the seek operation is at the end, the clock will detect this and stop on next tick
    clock.setElapsedDuration(duration);

    // update this flag before firing the tick event and reset afterwards
    this.seekOperation = true;
    updateCurrentSubtitle();
    this.seekOperation = false;
  }

  public void setSettings(Settings newSettings) {
    this.settings = newSettings;
    fireEvent(l -> l.onSettingsChanged(this));
  }

  public boolean isPlaying() {
    return clock.isRunning();
  }

  private void updateCurrentSubtitle() {
    this.subtitle = subtitles.findSubtitle(clock.getElapsedDuration());
    fireEvent(l -> l.onClockTick(this));
  }

  public void addListener(Listener listener) {
    this.listeners.add(listener);
  }

  public void start() {
    clock.start();
    fireEvent(l -> l.onStarted(this));
  }

  public void stop() {
    clock.stop();
    fireEvent(l -> l.onStopped(this));
  }

  public void loadSubtitles(SubtitleList subtitles) {
    log.debug("Setting new list of subtitles (Total: {})", subtitles.size());
    this.subtitles = subtitles;
    clock.stop();
    clock.init(subtitles.getTotalDuration());
    fireEvent(l -> l.onSubtitlesLoaded(this));
  }

  public void close() {
    log.info("Closing player");
    listeners.clear();
    clock.stop();
    Settings.saveToFile(settings);
  }

  public boolean hasSubtitles() {
    return this.subtitles != null;
  }

  public int getTotalDuration() {
    return this.clock.getTotalDuration();
  }

  public int getElapsedDuration() {
    return this.clock.getElapsedDuration();
  }

  public String getLastFolder() {
    return settings.getLastOpenFolder();
  }

  public void setLastFolder(String path) {
    settings.setLastOpenFolder(path);
  }

  private void fireEvent(Consumer<Listener> action) {
    this.listeners.fireEvent(action);
  }

  public interface Listener {

    void onSubtitlesLoaded(Player player);

    void onClockTick(Player player);

    void onStarted(Player player);

    void onStopped(Player player);

    void onSettingsChanged(Player player);
  }

  public static class Adapter implements Listener {

    @Override
    public void onSubtitlesLoaded(Player player) {
      // to be implemented by descendants if needed
    }

    @Override
    public void onClockTick(Player player) {
      // to be implemented by descendants if needed
    }

    @Override
    public void onStarted(Player player) {
      // to be implemented by descendants if needed
    }

    @Override
    public void onStopped(Player player) {
      // to be implemented by descendants if needed
    }

    @Override
    public void onSettingsChanged(Player player) {
      // to be implemented by descendants if needed
    }
  }
}
