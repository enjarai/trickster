```json
{
  "title": "Inventory Tricks",
  "icon": "minecraft:bundle",
  "category": "trickster:tricks"
}
```

Tricks that pull information from and interact with the caster's inventory.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_item_in_slot,title=Fence's Distortion|>

slot -> item

---

Returns the type of item that the given slot contains.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:other_hand,title=Juggling Delusion|>

-> item

---

Returns the type of item in the caster's other hand.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_inventory_slot,title=Intrusive Distortion|>

number, [vector] -> slot

---

Returns the item slot at the given index in either the inventory of the caster, or the block at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:import,title=Assistance Stratagem|>

item, any... -> any

---

Searches the caster's inventory for a specific item. 
The first item found with an inscribed spell will be cast with the provided arguments and the result returned.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:import_hat,title=Cranium Stratagem|>

number, any... -> any

---

Grabs the spell from the specified slot in the caster's [Top Hat](^trickster:top_hat), casts it with the provided arguments, and returns the result.