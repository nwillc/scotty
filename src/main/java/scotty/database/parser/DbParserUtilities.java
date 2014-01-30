/*
 * (c) Copyright Selerity, Inc. 2009-2013. All rights reserved. This source code is confidential
 * and proprietary information of Selerity Inc. and may be used only by a recipient designated
 * by and for the purposes permitted by Selerity Inc. in writing.  Reproduction of, dissemination
 * of, modifications to or creation of derivative works from this source code, whether in source
 * or binary forms, by any means and in any form or manner, is expressly prohibited, except with
 * the prior written permission of Selerity Inc..  THIS CODE AND INFORMATION ARE PROVIDED "AS IS"
 * WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE. This notice may not be
 * removed from the software by any user thereof.
 */

package scotty.database.parser;

import scotty.database.Context;
import scotty.database.Database;
import scotty.database.Instance;
import scotty.database.Type;
import scotty.util.Consumer;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static scotty.util.Iterables.forEach;

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
                    results.addAll(rankedQuery(child,criteria));
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
        List<Context> types = new LinkedList<>(database.getContained().values());
        Collections.sort(types);
        for (Context type : types) {
            printStream.format("Type: %s\n", ((Type) type).getName());
            List<Context> instances = new LinkedList<>(type.getContained().values());
            Collections.sort(instances);
            for (Context instance : instances) {
                if (context == null) {
                    printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
                } else {
                    final float similarity = instance.similarity(context);
                    if (similarity == 0.0) {
                        continue;
                    }
                    printStream.format("\tInstance: %s\n", ((Instance) instance).getName());
                    printStream.format("\tSimilarity Score: %f\n", similarity);
                }
                List<String> attrNames = new LinkedList<>(instance.keySet());
                Collections.sort(attrNames);
                for (String key : attrNames) {
                    printStream.format("\t\t%20s: %s\n", key, instance.get(key));
                }
            }
        }
    }


}
