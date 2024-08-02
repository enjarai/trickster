```json
{
  "title": "Long Spells",
  "icon": "minecraft:clock",
  "category": "trickster:basics"
}
```

Spells do not all execute instantly. Unless cast through a mirror, a player's spell executes a certain amount of circles per second. It may run forever, 
provided its caster remains alive, the spell does not blunder, and it never runs out of circles to execute.

;;;;;

To cast a spell capable of running long, a spell slot is required, even if the spell completes within one twentieth of a second. 
Without an empty spell slot, no spells can be cast except through a mirror. Spell slots may be viewed from the caster's inventory.

;;;;;

Spell slots have the following states: inactive, inactive and blundered (red), active and okay (green), active and at maximum executions per second (orange), 
active but delayed (white).

;;;;;

Patterns that execute spell fragments create sub-spells. A spell may not have a sub-spell more than 255 spells deep, 
and will blunder if such a thing is attempted. 


If a spell-executing pattern is the final pattern run in the current spell, this limit is ignored.