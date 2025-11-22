```json
{
  "title": "Inventory Manipulation",
  "icon": "minecraft:chest",
  "category": "trickster:ploys",
  "additional_search_terms": [
    "Ploy of Offering",
    "Swindler's Ploy",
    "Organizer's Ploy",
    "Cranial Shift Ploy"
  ]
}
```

Listed here are ploys that interact with inventory slots or other inventory features.


Creating a slot reference comes at no cost. However, using the reference in a way that moves the items inside the slot will always have a cost of (distance * amount * 0.5G).

;;;;;

<|ploy@trickster:templates|trick-id=trickster:drop_stack_from_slot,cost=distance * amount * 0.5G|>

Drops items from the given slot at a position, returning their entity. Optionally, an amount can be specified.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:swap_slot,cost=distance * amount * 0.5G|>

Swaps the item stacks within the given slots.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:move_stack,cost=distance * amount * 0.5G|>

Moves items from one slot into another, optionally limiting the amount. Can merge and split stacks.

;;;;;

<|trick@trickster:templates|trick-id=trickster:set_hat|>

Sets the selected slot in the caster's [Hat](^trickster:items/top_hat), returning a boolean based on success.
