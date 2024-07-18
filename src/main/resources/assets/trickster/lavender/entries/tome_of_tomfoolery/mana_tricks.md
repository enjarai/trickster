```json
{
  "title": "All That Moves",
  "icon": "minecraft:end_crystal",
  "category": "trickster:tricks"
}
```

Within everything, there is mana. 
Everything that moves, from the traveling merchants to the undead that haunt the night, 
all have their own reserves of mana, though most cannot wield it. They move rocks with their flesh 
and themselves with their limbs. There are thousands of teragandalfs of mana just *sitting there*. 
But you have the power to tap into them.

;;;;;

There are two primary patterns for interacting with the mana of other creatures,
Distortion of Authority and Conduit's ploy.


In many cases, you'll want to use them in conjunction to prevent a premature end to the life of yourself or your target.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_mana,title=Distortion of Authority|>

entity | vector -> number

---

Given an entity or mana holding block position, returns its current mana.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:leech_mana,title=Conduit's Ploy|>

entity, number ->

<|cost-rule@trickster:templates|formula=When used:
  amountUsed / (casterHealth / targetHealth) * 1kG|>

Links the given entity to provide up to the given number's worth of mana towards this spell.

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

