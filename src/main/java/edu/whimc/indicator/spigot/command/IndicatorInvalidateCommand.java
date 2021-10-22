package edu.whimc.indicator.spigot.command;

import edu.whimc.indicator.common.IndicatorCommon;
import edu.whimc.indicator.spigot.IndicatorSpigot;
import edu.whimc.indicator.spigot.command.common.CommandNode;
import edu.whimc.indicator.spigot.util.Format;
import edu.whimc.indicator.spigot.util.Permissions;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IndicatorInvalidateCommand extends CommandNode {

  public IndicatorInvalidateCommand(@Nullable CommandNode parent) {
    super(parent, Permissions.ADMIN_PERMISSION,
        "Clear the internal Indicator cache",
        "invalidate");
  }

  @Override
  public boolean onWrappedCommand(@NotNull CommandSender sender,
                                  @NotNull Command command,
                                  @NotNull String label,
                                  @NotNull String[] args,
                                  @NotNull Map<String, String> flags) {
    IndicatorCommon.getPathCache().clear();
    sender.spigot().sendMessage(Format.success("Cleared cache"));
    return true;
  }
}
