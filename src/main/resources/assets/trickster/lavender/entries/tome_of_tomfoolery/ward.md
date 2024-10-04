```json
{
  "title": "Wards",
  "icon": "minecraft:shield"
}
```

Wards are defensive spells that are cast when you are the target of a ploy. 
Your ward receives the glyph that is targeting you, the caster, and a list containing the inputs the caster is passing to the glyph. 
The expected signature for a ward is the following: 

---

spell, entity | vector, any[] -> any[]