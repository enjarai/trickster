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

;;;;;

<|trick@trickster:templates|trick-id=trickster:drop_stack_from_slot|>

Drops items from the given slot at a position, returning their entity. Optionally, an amount can be specified.

;;;;;

<|trick@trickster:templates|trick-id=trickster:move_resource|>

Moves resources from one storage into another.

;;;;;

A number can be provided to limit the amount transfered, and a resource type or list of such can be provided as a filter.

;;;;;

<|trick@trickster:templates|trick-id=trickster:swap_slot|>

Swaps the item stacks within the given slots.

;;;;;

<|trick@trickster:templates|trick-id=trickster:set_hat|>

Sets the selected slot in the caster's [Hat](^trickster:items/top_hat), returning a boolean based on success.
