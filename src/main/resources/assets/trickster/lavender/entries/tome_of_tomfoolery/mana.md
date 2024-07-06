```json
{
  "title": "Mana",
  "icon": "minecraft:amethyst_shard",
  "category": "trickster:basics"
}
```

Mana fuels your ploys. It's the cost of manipulating the world. Amounts of mana are measured in gandalfs. 
Any amount of mana that is lesser than one kilogandalf is minimal. You have 250 kilogandalfs of mana reserves. 
Additionally, if you run out of mana, mana will be drawn from your very life. Such mana is referred to as blood mana, 
and you have 250 kilogandalfs of blood mana. 

;;;;;

Mana regenerates over time at 0.5%/s. Maximum mana is always equivalent to maximum health times 24. 
Current blood mana is always equivalent to current health times 24.

;;;;;

<|page-title@lavender:book_components|title=All That Moves|>Within everything, there is mana. 
Everything that moves, from the traveling merchants to the undead that haunt the night, 
they all have their own reserves of mana. But most cannot wield it. They move rocks with their flesh 
and themselves with their limbs. There are thousands of teragandalfs of mana just *sitting there*. 
But you have the power to tap into their mana reserves.

;;;;;

The Conduit's Ploy is a powerful glyph that allows you to exploit the strength of other creatures, 
channeling their mana through you and into your spells.
Each creature you link will stay linked only for the duration of the spell,
and only until the link has expended its permitted amount of mana. 
You cannot relink an entity that has been unlinked in the current spell.

;;;;;

When there are mana sources linked to your spell, they will be prioritised when ploys draw mana. 
Mana links with greater limits provide a greater ratio of mana so that all sources are unlinked at once, 
unless the source died. Mana links are lossy, with a tax of 70% incurred on the mana source. 
The limit on mana links is the amount of mana drained post-tax. 
The amount of mana actually provided to your spell is mana link limit divided by 1.7.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:leech_mana,title=Conduit's Ploy|>

entity, number ->

---

Links the given entity to provide up to the given number's worth of mana towards this spell.