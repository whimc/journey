package edu.whimc.indicator.search;

import edu.whimc.indicator.api.search.TwoLevelBreadthFirstSearch;
import edu.whimc.indicator.path.SpigotLocatable;
import edu.whimc.indicator.search.mode.JumpMode;
import edu.whimc.indicator.search.mode.WalkMode;
import org.bukkit.World;

public class IndicatorSearch extends TwoLevelBreadthFirstSearch<SpigotLocatable, World> {

  public IndicatorSearch() {
    registerMode(new WalkMode());
    registerMode(new JumpMode());
  }

}
