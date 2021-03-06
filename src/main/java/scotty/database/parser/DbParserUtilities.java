/*
 * Copyright (c) 2015, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 */

package scotty.database.parser;

import almost.functional.Consumer;
import scotty.database.Context;
import scotty.database.Database;
import scotty.database.Instance;
import scotty.database.Type;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static almost.functional.utils.Iterables.forEach;

/**
 * Utility methods used by the database portions of Scotty.
 */
public final class DbParserUtilities {

    private DbParserUtilities() {
    }

    /**
     * Query a context for all the contexts within it that match a criteria.
     *
     * @param context  The context to query.
     * @param criteria The criteria to match.
     * @return the list of matches in ranked order
     */
    public static List<Context> query(Context context, Context criteria) {
        final List<Context> contexts = new LinkedList<>();
        forEach(rankedQuery(context, criteria), new Consumer<Ranked<Context>>() {
            @Override
            public void accept(Ranked<Context> ranked) {
                contexts.add(ranked.getData());
            }
        });
        return contexts;
    }

    /**
     * Query a context for all the contexts within it that match a criteria.
     *
     * @param context  The context to query
     * @param criteria the criteria to match
     * @return the results, with their associated ranks in ranked order
     */
    public static List<Ranked<Context>> rankedQuery(Context context, final Context criteria) {
        final List<Ranked<Context>> results = new LinkedList<>();

        if (context.isContainer()) {
            forEach(context.getContained().values(), new Consumer<Context>() {
                @Override
                public void accept(Context child) {
                    results.addAll(rankedQuery(child, criteria));
                }
            });
        } else {
            float rank = context.similarity(criteria);
            if (rank > Similarity.NOT_SIMILAR) {
                results.add(new Ranked<>(rank, context.getAge(), context));
            }
        }

        Collections.sort(results);
        return results;
    }

    /**
     * Print out the contents of a database, flattening all the inheritance to the instance level.
     *
     * @param database    the database
     * @param printStream the output stream
     */
    public static void print(final Database database, final PrintStream printStream) {
        print(database, printStream, null);
    }

    /**
     * Print out the contents of a database, flattening all the inheritance to the instance level and
     * showing the similarity score with the context passed in.  Zero similarity matches are discarded.
     *
     * @param database    the database
     * @param printStream the output stream
     * @param context     the Context to compare instances to.
     */
    public static void print(final Database database, final PrintStream printStream, final Context context) {
        if (context != null) {
            printStream.println("Context:");
            List<String> attrNames = new LinkedList<>(context.keySet());
            Collections.sort(attrNames);
            forEach(attrNames, new Consumer<String>() {
                @Override
                public void accept(String key) {
                    printStream.format("%20s: %s\n", key, context.get(key));
                }
            });
        }
        final List<Context> types = new LinkedList<>(database.getContained().values());
        Collections.sort(types);
        forEach(types, new Consumer<Context>() {
            @Override
            public void accept(Context type) {
                printStream.format("Type: %s\n", ((Type) type).getName());
                final List<Context> instances = new LinkedList<>(type.getContained().values());
                Collections.sort(instances);
                forEach(instances, new Consumer<Context>() {
                    @Override
                    public void accept(final Context instance) {
                        if (context == null) {
                            printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
                        } else {
                            final float similarity = instance.similarity(context);
                            if (similarity != 0.0f) {
                                printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
                                printStream.format("\tSimilarity Score: %f\n", similarity);
                            }
                        }
                        final List<String> attrNames = new LinkedList<>(instance.keySet());
                        Collections.sort(attrNames);
                        forEach(attrNames, new Consumer<String>() {
                            @Override
                            public void accept(String key) {
                                printStream.format("\t\t%20s: %s\n", key, instance.get(key));
                            }
                        });
                    }
                });
            }
        });
    }
}
