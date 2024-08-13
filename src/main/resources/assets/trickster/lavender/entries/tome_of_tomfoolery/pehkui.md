```json
{
  "title": "Scale Transfiguration",
  "icon": "minecraft:cookie",
  "category": "trickster:tricks",
  "fabric:load_conditions": {
    "condition": "fabric:any_mods_loaded",
    "values": [
      "pehkui"
    ]
  }
}
```

In the end, everything is relative, even size! As such, you can change the relative size of something relatively easily.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:get_scale,title=Distortion of Occupation|>

entity -> number

---

Returns the scale of the given entity.

;;;;;

<|glyph@trickster:templates|trick-id=trickster:set_scale,title=Ploy of Occupation|>

entity, number -> boolean

<|cost-rule@trickster:templates|formula=abs(currentScale - newScale)^2 * 10kG + newScale * 50kG|>

Changes the scale of the given entity. Entities cannot be scaled below 0.0625 or above 5 times their usual size.
