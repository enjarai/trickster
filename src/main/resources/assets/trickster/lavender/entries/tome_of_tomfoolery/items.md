```json
{
  "title": "Item Handling",
  "icon": "minecraft:stick",
  "category": "trickster:tricks"
}
```

This entry contains tricks that manipulate items. 

;;;;;

<|glyph@trickster:templates|trick-id=trickster:stack_to_item,title=Fence's Distortion|>

stack -> item

---

Returns the type of item that the given stack contains.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:move_stack,title=Pick-Pocket's Ploy|>

stack, entity ->

---

Move the given stack to the given entity's inventory. Only works on players.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_inventory_stack,title=Intrusive Distortion|>

entity, number -> stack

---

Returns the item stack at the given index in the inventory of the given entity. Only works on players.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_armour_stack,title=Disrobing Distortion|>

entity, number -> stack

---

Returns the armour item stack at the given index on the given entity.
