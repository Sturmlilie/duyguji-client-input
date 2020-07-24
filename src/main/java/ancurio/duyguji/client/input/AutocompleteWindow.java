package ancurio.duyguji.client.input;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class AutocompleteWindow extends DrawableHelper {
    private final TextRenderer textRenderer;
    private int bgColor;
    private int bgSelectionColor;

    public enum Position {
        ABOVE,
        BELOW
    }

    public AutocompleteWindow(final TextRenderer textRenderer, final int bgColor, final int bgSelectionColor) {
        this.textRenderer = textRenderer;
        this.bgColor = bgColor;
        this.bgSelectionColor = bgSelectionColor;
    }

    public final static class Data {
        private List<Map.Entry<String, String>> suggestions;
        private int selectionIndex = -1;

        // How many entries are visible at once in a scrolled view?
        private static final int SCROLL_VIEW_SIZE = 8;
        // Shift between highest to lowest visible index
        private static final int SCROLL_IDX_SHIFT = SCROLL_VIEW_SIZE-1;
        // Lowest visible index in a scrolled view
        private int scrollBase = 0;

        public static final Data EMPTY = new Data(Collections.emptyList());

        public Data(List<Map.Entry<String, String>> suggestions) {
            this.suggestions = suggestions;
        }

        public boolean isEmpty() {
            return suggestions.isEmpty();
        }

        public void moveSelection(final int delta) {
            if (suggestions.size() == 0) {
                return;
            }

            selectionIndex += (delta + suggestions.size());
            selectionIndex %= suggestions.size();

            // Scrolling downwards, past the lower scroll view bound
            if (delta > 0 && selectionIndex >= scrollBase + SCROLL_VIEW_SIZE) {
                scrollBase = selectionIndex - SCROLL_IDX_SHIFT;
            // Scrolling upwards, past the upper scroll view bound
            } else if (delta < 0 && selectionIndex < scrollBase) {
                scrollBase = selectionIndex;
            // Scrolling downwards, past the lower limit and wrapping around
            } else if (delta > 0 && selectionIndex < scrollBase) {
                scrollBase = selectionIndex;
            // Scrolling upwards, past the upper limit and wrapping around
            } else if (delta < 0 && selectionIndex >= scrollBase + SCROLL_VIEW_SIZE) {
                scrollBase = selectionIndex - SCROLL_IDX_SHIFT;
            }
        }

        public void initSelectionIndex(final Position pos) {
            // BELOW is not implemented yet
            assert pos == Position.ABOVE;
            selectionIndex = maxSelectionIndex();
            scrollBase = Math.max(selectionIndex - SCROLL_IDX_SHIFT, 0);
        }

        public String selectedSymbol() {
            return suggestions.get(selectionIndex).getValue();
        }

        public boolean isScrolling() {
            return suggestions.size() > SCROLL_VIEW_SIZE;
        }

        public int viewedEntryCount() {
            return Math.min(suggestions.size(), SCROLL_VIEW_SIZE);
        }

        public int scrolledSelectionIndex() {
            return selectionIndex - scrollBase;
        }

        public int maxSelectionIndex() {
            return suggestions.size() - 1;
        }

        public boolean canScrollUpwards() {
            return scrollBase > 0;
        }

        public boolean canScrollDownwards() {
            return scrollBase + SCROLL_IDX_SHIFT < maxSelectionIndex();
        }
    };

    public void render(final MatrixStack matrices, final Data data, final int windowX, final int windowYLower) {
        if (data.suggestions.size() == 0) {
            return;
        }

        final int textHeight = textRenderer.fontHeight;
        final int verticalPadding = 1;
        final int horizontalPadding = 2;
        final int symbolAreaPadding = 4;

        final int scrollBarHeight = 6;
        // Offset by which the normal, unscrolled view is shifted in
        // y direction when scrolling is available
        final int scrollYOffset = data.isScrolling() ? scrollBarHeight : 0;

        int maxSymbolWidth = 10;
        int maxMnemonicWidth = 0;

        for (final Map.Entry<String, String> entry : data.suggestions) {
            final String mnemonic = entry.getKey();
            final String symbol = entry.getValue();
            maxMnemonicWidth = Math.max(maxMnemonicWidth, textRenderer.getWidth(mnemonic));
            maxSymbolWidth = Math.max(maxSymbolWidth, textRenderer.getWidth(symbol));
        }

        // ______________________
        // |0|1+2|     3      |0|  <- entryWidth
        //
        // 0: horizontalPadding
        // 1: maxSymbolWidth
        // 2: symbolAreaPadding
        // 3: maxMnemonicWidth
        //
        // ----------------------|  |
        //    verticalPadding    |  |
        // ----------------------|  |
        //                       |  |
        //       textHeight      |  |< entryHeight
        //                       |  |
        // ----------------------|  |
        //    verticalPadding    |  |
        // ----------------------|  |
        //
        //
        // ________________________
        //                         | <-- windowY (absolute position)
        //     scrollYOffset (▲)   |
        // ------------------------|
        //  entryHeight (entry 0)  |
        // ------------------------|
        //           ...           |
        // ------------------------|
        // entryHeight (entry n-1) |
        // ------------------------|
        //     scrollYOffset (▼)   |
        //                         | <-- windowYLower
        // ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾

        final int symbolAreaWidth = symbolAreaPadding + maxSymbolWidth;
        final int entryWidth = horizontalPadding*2 + symbolAreaWidth + maxMnemonicWidth;
        final int entryHeight = verticalPadding*2 + textHeight;
        final int count = data.viewedEntryCount();
        final int windowY = windowYLower - count*entryHeight - scrollYOffset*2;

        final int indicatorColor = 0xFFFFFFFF;

        // Scroll indicators
        if (data.canScrollUpwards()) {
            final String indicator = "▲";
            final int indicatorWidth = textRenderer.getWidth(indicator);
            final float xOffset = (entryWidth - indicatorWidth) / 2.0f;
            textRenderer.draw(matrices, indicator, windowX + xOffset, windowY, indicatorColor);
        }

        if (data.canScrollDownwards()) {
            final String indicator = "▼";
            final int indicatorWidth = textRenderer.getWidth(indicator);
            final float xOffset = (entryWidth - indicatorWidth) / 2.0f;
            textRenderer.draw(matrices, indicator, windowX + xOffset, windowYLower - scrollBarHeight, indicatorColor);
        }

        // Background
        fill(matrices, windowX, windowY, windowX + entryWidth, windowYLower, bgColor);

        // Selection
        final int y0 = windowY + data.scrolledSelectionIndex()*entryHeight + scrollYOffset;
        fill(matrices, windowX, y0 + entryHeight, windowX + entryWidth, y0, bgSelectionColor);

        final int symbolX = windowX + horizontalPadding;
        final int mnemonicX = symbolX + symbolAreaWidth;
        final int symbolColor = 0xFFFFFFFF;
        final int mnemonicColor = symbolColor;

        // Suggestions
        for (int i = 0; i < count; ++i) {
            final Map.Entry<String, String> entry = data.suggestions.get(i + data.scrollBase);

            final String symbol = entry.getValue();
            final String mnemonic = entry.getKey();
            final int y = windowY + i*entryHeight + verticalPadding + scrollYOffset;
            final int symbolWidth = textRenderer.getWidth(symbol);
            final float symbolOffset = (symbolAreaWidth - symbolWidth) / 2.0f;

            textRenderer.draw(matrices, symbol, symbolX + symbolOffset, y+1, symbolColor);
            textRenderer.draw(matrices, mnemonic, mnemonicX, y, mnemonicColor);
        }
    }
}
