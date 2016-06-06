package de.komoot.photon.mogicext.query;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.query.TagFilterQueryBuilder;
import org.elasticsearch.index.query.*;

import java.util.Map;
import java.util.Set;

public class PostcodeQueryBuilder implements TagFilterQueryBuilder {
	private String postcode;

	private String language;

	private PostcodeQueryBuilder(String postcode, String language) {
		this.postcode = postcode;
		this.language = language;
	}

	/**
	 * Create an instance of this builder which can then be embellished as needed.
	 *
	 * @param plz	post code
	 * @param language
	 * @return An initialized {@link TagFilterQueryBuilder photon query builder}.
	 */
	public static TagFilterQueryBuilder builder(String plz, String language) {
		return new PostcodeQueryBuilder(plz, language);
	}

	/**
	 * When this method is called, all filters are placed inside their {@link OrFilterBuilder OR} or {@link AndFilterBuilder AND} containers and the top level filter builder is
	 * built. Subsequent invocations of this method have no additional effect. Note that after this method is called, calling other methods on this class also have no effect.
	 *
	 * @see TagFilterQueryBuilder#buildQuery()
	 */
	@Override
	public QueryBuilder buildQuery() {
//		System.err.println("########## query for "+postcode);

		AndFilterBuilder fb = FilterBuilders.andFilter(
				FilterBuilders.termFilter("osm_key", "place"),
				FilterBuilders.termFilter("postcode", postcode)
		);

		//optional language filter
		if (language != null) {
			fb.add(FilterBuilders.existsFilter(String.format("name.%s.raw", language)));
		}

		return QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), fb);
	}

	@Override
	public Integer getLimit() {
		return null;
	}


	@Override
	public TagFilterQueryBuilder withLimit(Integer limit) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withLocationBias(Point point) {
		if(point == null) return this;
		//TODO location bias
//		queryBuilder.add(ScoreFunctionBuilders.scriptFunction("location-biased-score", "groovy").param("lon", point.getX()).param("lat", point.getY()));
		return this;
	}

	@Override
	public TagFilterQueryBuilder withTags(Map<String, Set<String>> tags) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withKeys(Set<String> keys) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withValues(Set<String> values) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withTagsNotValues(Map<String, Set<String>> tags) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withoutTags(Map<String, Set<String>> tagsToExclude) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withoutKeys(Set<String> keysToExclude) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withoutValues(Set<String> valuesToExclude) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withKeys(String... keys) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withValues(String... values) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withoutKeys(String... keysToExclude) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withoutValues(String... valuesToExclude) {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withStrictMatch() {
		return this;
	}

	@Override
	public TagFilterQueryBuilder withLenientMatch() {
		return this;
	}
}
