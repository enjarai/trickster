```json
{
  "title": "Interspell Communication",
  "icon": "minecraft:feather",
  "category": "trickster:ploys",
  "additional_search_terms": [
    "Dispatch Ploy",
    "Ploy of Receipt"
  ]
}
```

Utilizing the following tricks, spells may communicate with each other.

;;;;;

<|ploy@trickster:templates|trick-id=trickster:message_send,cost=max(0G\, range - 16G)|>

Sends the given fragment to all spells within 16 blocks. Range may be extended by the given number at the cost of mana.

;;;;;

<|trick@trickster:templates|trick-id=trickster:message_listen|>

Returns all messages received on the tick after they were received. Must be provided with a timeout after which to return anyway.
