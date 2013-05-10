package scotty.database;

import scotty.database.parser.Elements;
import scotty.database.parser.InstanceQuery;
import scotty.database.parser.NamedContext;
import scotty.database.parser.Retrieval;

import java.util.*;

/**
 * A type in a SCoTTY database.
 */
public class Type extends NamedContext implements Retrieval<Instance>, InstanceQuery {
	private final Map<String, Instance> instances = new HashMap<>();

	public Type(String name) {
		super(null, name);
	}

	public Map<String, Instance> getInstances() {
		return instances;
	}

	public String query(String attributeName, Context context) {

		if (context.containsKey(Elements.INSTANCE)) {
			String iName = context.get(Elements.INSTANCE);
			Instance instance = getInstances().get(iName);
			return instance == null ? null : instance.getContext().query(attributeName, context);
		}

		for (Instance instance : getInstances().values()) {
			String value = instance.getContext().query(attributeName, context);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	@Override
	protected String getElementType() {
		return Elements.TYPE;
	}

	@Override
	public Instance get(String label) {
		return instances.get(label);
	}

	@Override
	public String attr(String label) {
		String[] part = label.split("\\.");
		Instance instance = get(part[0]);
		return instance == null ? null : instance.attr(part[1]);
	}

	@Override
	public Instance find(Context context) {
		List<Instance> matches = match(context);
		return matches.size() > 0 ? matches.get(0) : null;
	}

	@Override
	public List<Instance> match(Context context) {
		List<ScoredMatch> results = new LinkedList<>();
		for (Instance instance : instances.values()) {
			int score = Context.similarity(instance.getContext(), context);
			if (score > 0) {
				ScoredMatch scoredMatch = new ScoredMatch(instance, score);
				results.add(scoredMatch);
			}
		}
		Collections.sort(results);
		List<Instance> ordered = new ArrayList<>(results.size());
		for (ScoredMatch e : results) {
			ordered.add(e.instance);
		}
		return ordered;
	}

	private static class ScoredMatch implements Comparable {
		final Integer score;
		final Instance instance;

		private ScoredMatch(Instance instance, Integer score) {
			this.instance = instance;
			this.score = score;
		}

		@Override
		public int compareTo(Object o) {
			return ((ScoredMatch) o).score.compareTo(score);
		}
	}
}
