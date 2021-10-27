package edu.whimc.journey.common.config;

import org.jetbrains.annotations.NotNull;

/**
 * A setting designed to store strings as its settings.
 */
public class StringSetting extends Setting<String> {
  StringSetting(@NotNull String path, @NotNull String defaultValue) {
    super(path, defaultValue, String.class);
  }

  @Override
  public String parseValue(@NotNull String string) {
    return string;
  }

  @Override
  @NotNull
  public String printValue() {
    return getValue();
  }
}
