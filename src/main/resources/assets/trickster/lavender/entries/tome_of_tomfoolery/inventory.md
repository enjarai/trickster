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

<|page-title@lavender:book_components|title=Note: Slot References|>Item slots may be referenced by spells.
Creating such a reference comes at no cost. However, using the reference in a way that moves the items within the slot, will incur a move cost.
This cost is equivalent to 32 + (distance * 0.8), per moved item. Slot references will always point to a block position, or use the *current caster at the time of move*.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:other_hand_slot,title=Catch Delusion|>

-> slot

---

Returns a slot reference of the caster's other hand.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_inventory_slot,title=Intrusive Distortion|>

number, [vector] -> slot

---

Returns the item slot at the given index in either the inventory of the caster, or the block at the given position.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:drop_stack_from_slot,title=Benevolent Distortion|>

slot, vector, [number] -> entity

---

Drops items from the given slot at the given vector, and returns their entity. Optionally, the count of items can be specified.

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

;;;;;

<|glyph@trickster:templates|trick-id=trickster:check_hat,title=Cranium Delusion|>

-> number

---

Returns the selected slot in the caster's [Top Hat](^trickster:top_hat).