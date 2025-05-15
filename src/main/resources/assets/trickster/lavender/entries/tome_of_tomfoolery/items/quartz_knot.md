```json
{
  "title": "Quartz Knot",
  "icon": "trickster:quartz_knot",
  "category": "trickster:items",
  "required_advancements": [
    "trickster:quartz_knot"
  ],
  "ordinal": 13
}
```

Aha, a Knot based on Nether Quartz crystals! It seems more than just gems can be turned into mana containers.


This one however, has a rather abysmal capacity, on par with its Amethyst sibling.
Its natural recharge rate is also barely at the levels of an Emerald Knot. Not terrible, but definitely not great.

;;;;;

It seems to have one redeeming quality though. 
Augmenting a [Ploy of Receipt](!trickster:ploys/message#3) with a slot as a second argument, it is possible to listen
for messages from an item.


Most items do not react at all, but trying the same on this knot, it responds!

;;;;;

When a Quartz Knot is queried in this way, it replies with an ever-incrementing number, 
starting at zero at its initial creation, and counting up by about twenty every second.


A message can also be sent into the Knot using a similar method to resynchronize it, 
and offset its counter by a given number.

;;;;;

The applications for this are certain to be numerous...
