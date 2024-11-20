```json
{
  "title": "Raycasting",
  "icon": "minecraft:spectral_arrow",
  "category": "trickster:delusions_ingresses"
}
```

Raycasting tricks take either an entity or a position and a direction, and will return what the entity is looking at, 
or what the vectors are pointing to.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:raycast,title=Archer's Ingress|>

entity, [boolean] -> vector |

vector, vector, [boolean] -> vector

---

Returns the block that is hit.
If boolean is true, fluids are included in the raycast

;;;;;

<|glyph@trickster:templates|trick-id=trickster:raycast_side,title=Architect's Ingress|>

entity, [boolean] -> vector |

vector, vector, [boolean] -> vector

---

Returns a vector representing the side of the block that is hit.
If boolean is true, fluids are included in the raycast

;;;;;

<|glyph@trickster:templates|trick-id=trickster:raycast_entity,title=Scout's Ingress|>

entity -> entity |

vector, vector -> entity

---

Returns the entity that is hit.
