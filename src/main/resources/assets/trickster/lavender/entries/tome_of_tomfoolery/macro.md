```json
{
  "title": "Macros",
  "icon": "minecraft:feather"
}
```
Macros allow you to create your own Revisions to aid with spell scribing. 
When drawing a pattern, the associated macro is fetched from the combined maps of all worn [Rings](^trickster:items/ring), and will be evaluated for up to 1/20th of a second.

;;;;;

Macros take in the part of the spell that the pattern was drawn in as the first argument. 
The macro must then return a new spell. This spell replaces the spell the pattern was drawn in.
