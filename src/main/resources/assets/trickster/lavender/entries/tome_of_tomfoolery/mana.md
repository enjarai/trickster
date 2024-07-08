```json
{
  "title": "Mana",
  "icon": "minecraft:amethyst_shard",
  "category": "trickster:basics"
}
```

Mana is what fuels your ploys. It's the cost of manipulating the world.


As is tradition, amounts of mana are measured in gandalfs, or G. 
Any amount of mana that is lesser than one kilogandalf (kG) is generally considered negligible. 
All players have a maximum mana reserve of 240 kilogandalfs.

;;;;;

Keep in mind that once you run out of mana, any deficit will be drawn from your very life. 
Using mana in this way will consume your health, and is referred to as bloodcasting.
The average player has 40 kilogandalfs of mana in their blood.


Concretely, mana regenerates over time at 0.5%/s. Maximum mana is always equivalent to maximum health times twelve. 
Current blood mana is always equivalent to current health times two.

;;;;;

<|page-title@lavender:book_components|title=All That Moves|>Within everything, there is mana. 
Everything that moves, from the traveling merchants to the undead that haunt the night, 
all have their own reserves of mana, though most cannot wield it. They move rocks with their flesh 
and themselves with their limbs. There are thousands of teragandalfs of mana just *sitting there*. 
But you have the power to tap into them.

;;;;;

The Conduit's Ploy is a powerful glyph that allows you to exploit the strength of other creatures, 
channeling their mana through you and into your spells.
Each creature you link will stay linked only for the duration of the current cast,
and only until the link has expended its permitted amount of mana. 
You cannot relink an entity that has been unlinked in the current spell.

;;;;;

When there are mana sources linked to your spell, they will be prioritised when ploys draw mana. 
Mana links with greater limits provide a greater ratio of mana so that all links are drained of the same percentage of their available mana. 
A tax is incurred on the caster prior to draining the source. 
The tax percentage is the caster's health divided by the source's health. 
If the caster is not alive, a value of 25 is used in place of health.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:leech_mana,title=Conduit's Ploy|>

entity, number ->

---

Links the given entity to provide up to the given number's worth of mana towards this spell.