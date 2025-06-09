```json
{
  "title": "Raycasting",
  "icon": "minecraft:spectral_arrow",
  "category": "trickster:delusions_ingresses",
  "additional_search_terms": [
    "Pinpoint Ingress",
    "Bearing Ingress",
    "Mark Ingress"
  ]
}
```

Raycasting tricks take either an entity or a position and a direction, and will return what the entity is looking at, 
or what the vectors are pointing to.


Raycasts which target blocks may optionally be made to hit fluids if their last argument is true.

;;;;;

<|trick@trickster:templates|trick-id=trickster:raycast|>

Returns the block that is hit.

;;;;;

<|trick@trickster:templates|trick-id=trickster:raycast_side|>

Returns a vector representing the side of the block that is hit.

;;;;;

<|trick@trickster:templates|trick-id=trickster:raycast_entity|>

Returns the entity that is hit.
