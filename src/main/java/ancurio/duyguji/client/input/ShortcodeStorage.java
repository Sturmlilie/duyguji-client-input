package ancurio.duyguji.client.input;

import ancurio.duyguji.client.input.api.Shortcode;
import ancurio.duyguji.client.input.api.ShortcodeList;
import ancurio.duyguji.client.input.api.ShortcodeListRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import org.apache.commons.collections4.trie.PatriciaTrie;

public class ShortcodeStorage implements ShortcodeListRegistry {
    private final class StorageEntry implements ShortcodeList {
        public String shortname;
        public PatriciaTrie<String> trie;
        public PatriciaTrie<String> tempTrie;

        public StorageEntry(final String shortname) {
            this.shortname = shortname;
            this.trie = new PatriciaTrie<String>();
        }

        @Override
        public void beginUpdate() {
            assert tempTrie == null;
            tempTrie = new PatriciaTrie<String>();
        }

        @Override
        public void putEntry(final String symbol, final String code) {
            assert tempTrie != null;
            tempTrie.put(":" + code.replace('-', '_') + ":", symbol);
        }

        @Override
        public void endUpdate() {
            assert tempTrie != null;
            trie = tempTrie;
            tempTrie = null;
        }
    }

    private Map<String, StorageEntry> namespaces = new HashMap<String, StorageEntry>();

    public ShortcodeStorage() {
    }

    @Override
    public ShortcodeList register(final String namespace, final String shortname) {
        if (namespaces.containsValue(namespace)) {
            throw new RuntimeException("Namespace " + namespace + " already exists");
        }

        final StorageEntry entry = new StorageEntry(shortname);
        namespaces.put(namespace, entry);

        return entry;
    }

    public Map<String, String> query(final String prefix) {
        final Map<String, String> result = new HashMap<String, String>();

        for (final Map.Entry<String, StorageEntry> namespace : namespaces.entrySet()) {
            final StorageEntry entry = namespace.getValue();

            final SortedMap<String, String> suggestions = entry.trie.prefixMap(prefix);
            for (final Map.Entry<String, String> suggestion : suggestions.entrySet()) {
                result.put(suggestion.getKey(), suggestion.getValue());
            }
        }

        return result;
    }
}
