/*
 * Copyright (c) 2013-2014, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or
 * without fee is hereby granted, provided that the above copyright notice and this permission
 * notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO
 * THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT
 * SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
 * ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF
 * CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

package scotty.database;

import almost.functional.Consumer;
import almost.functional.Function;
import almost.functional.Optional;
import almost.functional.utils.Preconditions;
import scotty.database.parser.Similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static almost.functional.utils.ArrayIterable.newIterable;
import static almost.functional.utils.Iterables.forEach;
import static almost.functional.utils.Iterables.transform;
import static scotty.util.ScottyUtilities.join;

/**
 * A Context is a set of attributes, that can inherit attributes from its parent and contain sub-contexts within it.
 */
public class Context implements Comparable<Context>, Similarity<Context> {
    private static final AtomicInteger NEXT = new AtomicInteger(1);
    private final Map<String, Value> map = new HashMap<>();
    private final Context container;
    private final Map<String, Context> contained = new HashMap<>();
    private final int age;

    public Context() {
        this(null, null);
    }

    public Context(Context container) {
        this.container = container;
        this.age = Context.nextAge();
    }

    public Context(String attributes) {
        this(null, attributes);
    }

    public Context(Context container, String attributes) {
        this(container);
        put(attributes);
    }

    /**
     * Get the map of key/Values attributes of this context.
     *
     * @return the attribute map.
     */
    public final Map<String, Value> getMap() {
        return map;
    }

    /**
     * Get the map of sub-contexts withing this context.
     *
     * @return the map of contained contexts.
     */
    public final Map<String, Context> getContained() {
        return contained;
    }

    /**
     * Get the context this context is contained in.
     *
     * @return the containing context.
     */
    public final Context getContainer() {
        return container;
    }

    /**
     * Put a key/Value pair into this context.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(String key, Value value) {
        map.put(key, value);
    }


    /**
     * Add a key/string to the context as a key/Value.
     *
     * @param key   the key
     * @param value the string value
     */
    public void put(String key, String value) {
        map.put(key, new Value(value));
    }

    /**
     * Parse a string representation of a set of attributes and assign them to this context.
     * The string representation takes the form:
     * <blockquote>
     * attributes ::= attribute | attributes , attribute   <br>
     * attribute ::= <i>label</i> "=" values               <br>
     * values ::= <i>value</i> | values "|" <i>value</i>   <br>
     * </blockquote>
     * Some examples:
     * <blockquote>
     * env=prod <br>
     * env=prod|qa,mode=rw <br>
     * company=Acme\, Inc. <br>
     * </blockquote>
     * The "=" and "|" can be escaped with a \ to allow them to be included in values.
     *
     * @param attributes an attribute string representation
     */
    public void put(String attributes) {

        if (attributes == null) {
            return;
        }

        final String[] assignments = attributes.trim().split("(?<!\\\\),");
        forEach(newIterable(assignments), new Consumer<String>(){
            @Override
            public void accept(String assignment) {
                String[] labelValue = assignment.split("(?<!\\\\)=");
                if (labelValue.length == 2) {
                    final Value value = new Value();
                    final String[] values = labelValue[1].trim().split("(?<!\\\\)\\|");
                    forEach(newIterable(values), new Consumer<String>() {
                        @Override
                        public void accept(String str) {
                            value.add(str.replaceAll("\\\\,", ",").replaceAll("\\\\\\x7c", "|"));
                        }
                    });
                    forEach(newIterable(values), new Consumer<String>() {
                        @Override
                        public void accept(String str) {
                            value.add(str.replaceAll("\\\\,", ",").replaceAll("\\\\\\x7c", "|"));
                        }
                    });
                    put(labelValue[0].trim(), value);
                }
            }
        });

        forEach(newIterable(assignments), new Consumer<String>() {
            @Override
            public void accept(String assignment) {
                String[] labelValue = assignment.split("(?<!\\\\)=");
                if (labelValue.length == 2) {
                    final Value value = new Value();
                    final String[] values = labelValue[1].trim().split("(?<!\\\\)\\|");
                    forEach(newIterable(values), new Consumer<String>() {
                        @Override
                        public void accept(String str) {
                            value.add(str.replaceAll("\\\\,", ",").replaceAll("\\\\\\x7c", "|"));
                        }
                    });
                    put(labelValue[0].trim(), value);
                }
            }
        });

    }

    /**
     * Get the key set of this context, including those inherited from the containing context.
     *
     * @return the key set
     */
    public Set<String> keySet() {
        Set<String> keys = new HashSet<>(getMap().keySet());

        if (isContained()) {
            keys.addAll(getContainer().keySet());
        }

        return keys;
    }

    /**
     * Check if this context, or its containing contexts, contains a key.
     *
     * @param key the key
     * @return true if the key set contains the key
     */
    public boolean containsKey(String key) {
        return keySet().contains(key);
    }

    /**
     * Get the Value associated with a key.
     *
     *
     * @param key the key
     * @return the associated Value
     */
    public Optional<Value> getValue(String key) {
        if (map.containsKey(key)) {
            return Optional.of(map.get(key));
        }

        if (isContained()) {
            return container.getValue(key);
        }

        return Optional.empty();
    }

    /**
     * Get the string value of a Value associated with a given key.
     *
     * @param key key
     * @return string
     */
    public String get(String key) {
		return get(key, null);
    }

    /**
     * Get the string value of a Value associated with a given key and provide a default in case of null.
     *
     * @param key          the key
     * @param defaultValue the value to return if null
     * @return the string
     */
    public String get(final String key, final String defaultValue) {
        return getValue(key).transform(new Function<Value, String>() {
            @Override
            public String apply(Value value) {
                return value.toString();
            }
        }).orElse(defaultValue);
    }

    /**
     * Is this context contained in another?
     *
     * @return true if context is contained in another.
     */
    public boolean isContained() {
        return container != null;
    }

    /**
     * Does this context contain sub contexts?
     *
     * @return true if this context contains sub contexts.
     */
    public boolean isContainer() {
        return contained.size() > 0;
    }

    /**
     * Get the "age" of this context. Each instance of Context is given an increasing "age" number for use in ranking.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getSimpleName());
        sb.append("{");
        sb.append("Map{");
		sb.append(join(", ", transform(getMap().keySet(), new Function<String, String>() {
			@Override
			public String apply(String key) {
				return key + "=" + getMap().get(key);
			}
		})));
        sb.append("}");
        sb.append(", isContained=");
        sb.append(isContained());
        if (!isContainer()) {
            sb.append(", isContainer=");
            sb.append(isContainer());
        } else {
            sb.append(", Contained{");
			sb.append(join(", ", transform(getContained().keySet(), new Function<String, String>() {
				@Override
				public String apply(String key) {
					return key + "=" + getContained().get(key);
				}
			})));
            sb.append("}");
        }
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Context o) {
        Preconditions.checkNotNull(o, "Can not compare to null object.");
        Preconditions.isAssignableTo(o.getClass(), this.getClass(), "Class " + o.getClass().getName() + " must be castable to " + this.getClass().getName());
        return age - o.age;
    }

    @Override
    public float similarity(final Context b) {
        float score = NOT_SIMILAR;

        if (b == null) {
            return score;
        }

        for (final String key : b.keySet()) {
            float vScore = getValue(key).transform(new Function<Value, Float>() {
                @Override
                public Float apply(Value value) {
                   return value.similarity(b.getValue(key).get());
                }
            }).orElse(NOT_SIMILAR);
            if (vScore == NOT_SIMILAR) {
                return NOT_SIMILAR;
            }
            score += vScore;
        }
        return score;
    }

    private static int nextAge() {
        return NEXT.getAndIncrement();
    }
}
