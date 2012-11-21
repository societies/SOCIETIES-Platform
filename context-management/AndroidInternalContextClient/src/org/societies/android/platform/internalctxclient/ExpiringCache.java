package org.societies.android.platform.internalctxclient;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;

class ExpiringCache<K, V> {
    private long millisUntilExpiration;
    private Map<K, Entry<K,V>>  map;
    // Clear out old entries every few queries
    private int queryCount;
    private int queryOverflow = 300;
    private int MAX_ENTRIES = 200;
    
    static class Entry<K, V> extends WeakReference<V> {
        private long   timestamp;
        private V val;
        
        Entry(long timestamp, V val) {
        	super(val);
            this.timestamp = timestamp;
            this.val = val;
        }
        
        long   timestamp()                  { return timestamp;           }
        void   setTimestamp(long timestamp) { this.timestamp = timestamp; }
        
        V val()                        { return val;                 }
        void   setVal(V val)           { this.val = val;             }
    }
    
    ExpiringCache() {
        this(30000);
    }
    
    @SuppressWarnings("serial")
	ExpiringCache(long millisUntilExpiration) {
        this.millisUntilExpiration = millisUntilExpiration;
        map = new LinkedHashMap<K, Entry<K,V>>() {
            @SuppressWarnings("rawtypes")
			protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_ENTRIES;
            }
        };
    }
    
    synchronized V get(K key) {
        if (++queryCount >= queryOverflow) {
            cleanup();
        }
        Entry<K, V> entry = entryFor(key);
        if (entry != null) {
            return entry.val();
        }
        return entry == null ? null : entry.get();
    }
    
    synchronized V put(K key, V val) {
        if (++queryCount >= queryOverflow) {
            cleanup();
        }
        Entry<K, V> entry = entryFor(key);
        if (entry != null) {
            entry.setTimestamp(System.currentTimeMillis());
            entry.setVal(val);
        } else {
            entry = map.put(key, new Entry<K, V>(System.currentTimeMillis(), val));
        }
        return entry == null ? null : entry.get();
    }
    
    synchronized void clear() {
        map.clear();
    }
    
    private Entry<K, V> entryFor(K key) {
        Entry<K, V> entry = map.get(key);
        if (entry != null) {
            long delta = System.currentTimeMillis() - entry.timestamp();
            if (delta < 0 || delta >= millisUntilExpiration) {
                map.remove(key);
                entry = null;
            }
        }
        return entry;
    }
    
    private void cleanup() {
        Set<K> keySet = map.keySet();
        // Avoid ConcurrentModificationExceptions
        K[] keys = (K[]) new String[keySet.size()];
        int i = 0;
        for (Iterator<K> iter = keySet.iterator(); iter.hasNext(); ) {
            K key = iter.next();
            keys[i++] = key;
        }
        for (int j = 0; j < keys.length; j++) {
            entryFor(keys[j]);
        }
        queryCount = 0;
    }
    
    synchronized Set<K> keySet() {
    	return map.keySet();
    }
}