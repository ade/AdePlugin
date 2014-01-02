AdePlugin
=========

a minecraft (craftbukkit) plugin


Features
========

Warp Stones
-----------
A mechanism for creating teleporters in game without using text-commands. Two special warp blocks are created via a crafting recipe. To bind a source warp to a destination, a "signature" system is used, where four adjacent wool blocks define a warp destination.

A warp source or destination looks from above like:
```
 W
WTW
 W
```
Where W is Wool, and T is a special teleporter block, with a stone pressure plate on top. The color of the wool around defines the destination. If multiple destinations exist, a random one is selected. This makes this feature unsafe with untrusted players, as warps can be hijacked.

Warp block recipe (4 Redstone, 4 Gold Ingot, Ender Pearl):

![Warp teleporter recipe](/images/warp_source_recipe.png)

Warp destination block recipe (4 Redstone, 4 Coal, Ender Pearl):

![Warp destination recipe](/images/warp_destination_recipe.png)

Warp placement example:

![Warp teleporter recipe](/images/warp_example.png)
