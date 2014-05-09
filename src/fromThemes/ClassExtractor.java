package fromThemes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javatools.administrative.Announce;
import javatools.datatypes.FinalSet;
import basics.Fact;
import basics.FactCollection;
import basics.RDFS;
import basics.Theme;
import basics.Theme.ThemeGroup;
import fromOtherSources.HardExtractor;
import fromOtherSources.WordnetExtractor;
import fromWikipedia.CategoryClassExtractor;
import fromWikipedia.Extractor;

/**
 * YAGO2s - ClassExtractor
 * 
 * Deduplicates all type subclass facts and puts them into the right themes.
 * 
 * This is different from the FactExtractor, because its output is useful for
 * many extractors that deliver input for the FactExtractor.
 * 
 * @author Fabian M. Suchanek
 * 
 */
public class ClassExtractor extends Extractor {

	@Override
	public Set<Theme> input() {
		HashSet<Theme> input = new HashSet<Theme>(Arrays.asList(
				HardExtractor.HARDWIREDFACTS, WordnetExtractor.WORDNETCLASSES
		// GeoNamesClassMapper.GEONAMESCLASSES
				));
		input.addAll(CategoryClassExtractor.CATEGORYCLASSES.inAllLanguages());
		return input;
	}

	/** The YAGO taxonomy */
	public static final Theme YAGOTAXONOMY = new Theme(
			"yagoTaxonomy",
			"The entire YAGO taxonomy. These are all rdfs:subClassOf facts derived from multilingual Wikipedia and from WordNet.",
			ThemeGroup.TAXONOMY);

	@Override
	public Set<Theme> output() {
		return new FinalSet<>(YAGOTAXONOMY);
	}

	@Override
	public void extract() throws Exception {
		String relation = RDFS.subclassOf;
		Announce.doing("Reading", relation);
		FactCollection facts = new FactCollection();
		for (Theme theme : input()) {
			Announce.doing("Reading", theme);
			for (Fact fact : theme.factSource()) {
				if (!relation.equals(fact.getRelation()))
					continue;
				facts.add(fact);
			}
			Announce.done();
		}
		Announce.done();
		Announce.doing("Writing", relation);
		for (Fact fact : facts)
			YAGOTAXONOMY.write(fact);
		Announce.done();
	}

	public static void main(String[] args) throws Exception {
		new ClassExtractor().extract(new File("c:/fabian/data/yago2s"), "test");
	}
}
