```json
{
  "title": "Inventory Manipulation",
  "icon": "minecraft:chest",
  "category": "trickster:ploys"
}
```

Listed here are ploys that interact with inventory slots or other inventory features.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:drop_stack_from_slot,title=Benevolent Ploy|>

slot, vector, [number] -> entity

---

Drops items from the given slot at the given vector, and returns their entity. Optionally, the count of items can be specified.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:set_hat,title=Cranial Shift Ploy|>

number -> boolean

---

Sets the selected slot in the caster's [Top Hat](^trickster:basics/top_hat), returning a boolean based on success.
