# Trickster

[<img alt="discord-plural" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg">](https://discord.gg/WcYsDDQtyR)
[<img alt="kofi-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/donate/kofi-singular_vector.svg">](https://ko-fi.com/enjarai)
[<img alt="fabric-api" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg">](https://modrinth.com/mod/fabric-api)
[<img alt="fabric-api" height="56" src="https://enjarai.dev/static/requires_cicada_cozy.svg">](https://modrinth.com/mod/cicada)

[![](https://i.enjarai.dev/u/P9BQxO.png)](https://modfest.net/carnival)

[//]: # ([<img alt="fabric-api" height="56" src="https://i.enjarai.dev/u/fHOUib.png">]&#40;https://modrinth.com/mod/owo-lib&#41;)

Trickster is a magician-themed esolang-based magic mod loosely inspired by [Hex Casting](https://modrinth.com/mod/hex-casting).
It lets you draw infinitely nested spell circles and glyphs to combine tricks and create magical effects as simple or complex as you want.

![A simple spell](https://i.enjarai.dev/u/sPUWj0.png)

## So what can I make?

Yes.

The spells you can scribe into scrolls are made in a turing-complete tree-like structure,
offering complete freedom in creating the results you want.
Spells can do literally anything you can imagine*!

<sub>*Assumes your imagination is limited by the scope of available glyphs.
Library inc. takes no responsibility for overactive imagination or other forms of undue creativity.</sub>

## That's a pretty bold statement isnt it?

Yes it is, but we don't think its unfounded.

For one, spells will not stop casting until they are complete, which can, in fact, be never!
Spells run concurrently with the rest of the game and can be saved to and loaded from disk at any moment, just like everything else in Minecraft. This means you can leave a spell running, log out, come back two days later, and have it continue where you left off.

Secondly, spells can be infinitely* big and complex. While there *is* a limit to the speed spells execute at (to avoid straining the server) the spell itself can be as convoluted or as simple as you want!

And last but not least, while mana is a resource required by all world-altering effects, it is possible to theoretically infinitely scale the amount of mana available to you. Mana is intended more as a balancing measure than a limiter on your potential.

<sub>*Spell circles can't quite be infinitely nested yet due to floating point precision errors very deep into the editing screen, we plan to resolve this issue in the near future.</sub>

## So how do I begin?

Well, we've made that part pretty easy.

All glyphs, tricks, and ways of spell-scribing are neatly documented in the Tome of Tomfoolery™,
the go-to guide for being up to no good. Making use of the excellent [Lavender](https://modrinth.com/mod/lavender),
the Tome™ provides a state-of-the-art guidebook experience for all of your magical needs.

![Documentation](https://i.enjarai.dev/u/Edmujx.png)

## What else can I expect?

As any good magic mod should, we provide a handful of tools and trinkets to facilitate spellcasting.

For example, the Top Hat can hold a bunch of spell scrolls and easily select any of them for quick access:

![Spellcasting from a hat](https://i.enjarai.dev/u/MBojDM.png)

## Functional as in Functional Programming

For the more programming-minded, the spellcasting system in this mod can be described as tree-based functional programming, most closely resembling something like Haskell or Clojure. This means a few things:

- Data is immutable.
- Recursion is not only a thing, but the main way to achieve loop-like behaviour.
    - Which also means infinite recursion of course.
- Data can only move up the tree from one node to the next.
    - Though there are ways to split your tree, and pass around and manipulate other trees as data, effectively overcoming this limitation.

This combines with other functional paradigms to create a very clean, but powerful scripting language.

## Credits

Many of the item textures are courtesy of @midnightcartridge on Discord.

Thanks to @crephan as well for help with the Top Hat model.