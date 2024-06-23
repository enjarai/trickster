```json
{
  "title": "Entity Events",
  "icon": "minecraft:skeleton_skull",
  "category": "trickster:events"
}
```

This entry lists available event patterns relating to entities.


These can easily trap an unaware magician in their own 
net so to speak, **use with caution**.

;;;;;

<|pattern@trickster:templates|pattern=0\,4\,8\,6\,4\,2\,0,title=Destruction Ambush|>

{gray}(Event pattern){}

---

Responds to block breaking in its range. Return true to cancel.

;;;;;

<|pattern@trickster:templates|pattern=0\,2\,8\,6\,0,title=Creation Ambush|>

{gray}(Event pattern){}

---

Responds to block placing in its range. Return true to cancel.

;;;;;

<|pattern@trickster:templates|pattern=3\,4\,5\,8\,4,title=Motive Ambush|>

{gray}(Event pattern){}

---

Fires every 10 ticks for every entity within its range. 
Return true to prevent the entity from exerting movement input for the next 10 ticks.
