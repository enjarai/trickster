```json
{
  "title": "Inventory Tricks",
  "icon": "minecraft:bundle",
  "category": "trickster:tricks"
}
```

Tricks that pull information from and interact with the caster's inventory.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:other_hand,title=Juggling Delusion|>

-> item

---

Returns the type of item in the caster's other hand.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:import,title=Assistance Ploy|>

item -> any

---

Searches the caster's inventory for a specific item. 
The first item found with an inscribed spell will be cast and the result returned.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:import_hat,title=Cranium Ploy|>

number -> any

---

Grabs the spell from a scroll in a specific slot in the caster's [Top Hat](^trickster:top_hat), casts it, and returns the result.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:stack_to_item,title=Fence's Distortion|>

stack -> item

---

Returns the type of item that the given stack contains.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_inventory_stack,title=Intrusive Distortion|>

number, [vector] -> stack

---

Returns the item stack at the given index in either the inventory of the caster, or the block at the given position.