package de.komoot.photon.mogicext.query;

import com.vividsolutions.jts.geom.Point;
import de.komoot.photon.query.TagFilterQueryBuilder;
import org.elasticsearch.index.query.*;

import java.util.ArrayList;
import java.util.List;
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
	 * Create an instance of this builder.
	 *
	 * @param plz	post code
	 * @param language
	 * @return An initialized {@link TagFilterQueryBuilder photon query builder}.
	 */
	public static TagFilterQueryBuilder builder(String plz, String language) {
		return new PostcodeQueryBuilder(plz, language);
	}

	/**
	 * build the query to be executed.
	 *
	 * @see TagFilterQueryBuilder#buildQuery()
	 */
	@Override
	public QueryBuilder buildQuery() {
		//full text search for postcode
		MatchQueryBuilder defaultMatchQueryBuilder = QueryBuilders.matchQuery("collector.default", postcode);

		//filter by osm tags
		AndFilterBuilder fb = FilterBuilders.andFilter(
				FilterBuilders.termFilter("osm_key", "place"),
				FilterBuilders.termFilter("osm_value", "postcode")
		);

		//optional language filter
		if (language != null) {
			fb.add(FilterBuilders.existsFilter(String.format("name.%s.raw", language)));
		}

		return QueryBuilders.filteredQuery(defaultMatchQueryBuilder, fb);
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
