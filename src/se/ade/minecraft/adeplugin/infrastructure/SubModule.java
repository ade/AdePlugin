package se.ade.minecraft.adeplugin.infrastructure;

import se.ade.minecraft.adeplugin.AdePlugin;

/**
 * Adrian Nilsson
 * Created 2014-01-01 18:15
 */
public interface SubModule {
    public void onEnable(AdePlugin plugin);
    public void onDisable();
}
