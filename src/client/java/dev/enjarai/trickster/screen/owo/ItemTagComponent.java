package dev.enjarai.trickster.screen.owo;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owo.ui.parsing.UIParsing;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import org.w3c.dom.Element;

public class ItemTagComponent extends FlowLayout {
    protected TagKey<Item> tagKey;

    public ItemTagComponent(TagKey<Item> tagKey) {
        super(Sizing.fill(100), Sizing.content(), Algorithm.LTR_TEXT);
        this.tagKey = tagKey;

        alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        Registries.ITEM.getOrCreateEntryList(tagKey).forEach(entry -> {
            var stack = entry.value().getDefaultStack();
            child(Components.item(stack)
                    .showOverlay(true)
                    .setTooltipFromStack(true));
        });
    }

    public static ItemTagComponent parse(Element element) {
        UIParsing.expectAttributes(element, "tag-id");

        var tagId = UIParsing.parseIdentifier(element.getAttributeNode("tag-id"));
        var tagKey = TagKey.of(RegistryKeys.ITEM, tagId);

        return new ItemTagComponent(tagKey);
    }
}
