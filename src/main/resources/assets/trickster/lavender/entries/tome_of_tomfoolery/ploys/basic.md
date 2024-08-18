```json
{
  "title": "Basic Tricks",
  "icon": "minecraft:brick",
  "category": "trickster:tricks"
}
```

Here listed are the most basic but useful general purpose tricks.
Any aspiring magician is recommended to learn these.

;;;;;

<|page-title@lavender:book_components|title=Note: Inscribed Spells|>A spell can be inscribed onto any item that a player can hold in their inventory.
If inscribed on a block, the spell will be removed if the block is placed.


Some items may have additional interactions with inscribed spells, 
[Wands](^trickster:basics/wand) for example will cast the spell automatically when right-clicked.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:read_spell,title=Notulist's Delusion|>

-> spell | void

---

If the item in the caster's other hand contains an inscribed spell, returns the spell.
If not, returns void.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:write_spell,title=Notulist's Ploy|>

spell | void -> boolean

---

Inscribes a spell onto the item held in the caster's other hand.
Returns true or false depending on success.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:write_closed_spell,title=Proprietary Notulist's Ploy|>

spell -> boolean

---

Same as Notulist's Ploy, but the spell cannot be read.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:reveal,title=Showcase Stratagem|>

any... -> any

---

Shows all given values as a chat message to the caster and returns the first.

;;;;;

<|page-title@lavender:book_components|title=Note: The Crow Mind|>The Crow Mind, not to be confused with other black bird related minds, 
lets spells store and retrieve any one fragment, **persistently**, between casts.


This can be used for many things, such as counters, 
marking locations, and selecting targets.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:read_crow_mind,title=Crow Mind Delusion|>

-> any

---

Returns the value currently stored in the caster's Crow Mind.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:write_crow_mind,title=Crow Mind Ploy|>

any ->

---

Stores the supplied value in the caster's Crow Mind, overwriting any value that might already be present.

;;;;;

<|page-title@lavender:book_components|title=Note: Casting Cost|>After receiving multiple complaints at Tomfoolery Inc. HQ about the balance of this mod,
we've decided to properly implement spell casting costs.


However, player freedom and choice is also very important to us.
As such, this system operates on an opt-in basis.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:cost,title=Cost Ploy|>

->

---

Consumes one amethyst shard from the caster's inventory. Will cause a blunder if none are available.