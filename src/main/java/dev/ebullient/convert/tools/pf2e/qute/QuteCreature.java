package dev.ebullient.convert.tools.pf2e.qute;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.ebullient.convert.qute.QuteUtil;
import dev.ebullient.convert.tools.Tags;
import dev.ebullient.convert.tools.pf2e.Pf2eSources;
import io.quarkus.qute.TemplateData;

/**
 * Pf2eTools Creature attributes ({@code creature2md.txt})
 * <p>
 * Use `%%--` to mark the end of the preamble (frontmatter and
 * other leading content only appropriate to the standalone case).
 * </p>
 * <p>
 * Extension of {@link dev.ebullient.convert.tools.pf2e.qute.Pf2eQuteBase Pf2eQuteBase}
 * </p>
 */
@TemplateData
public class QuteCreature extends Pf2eQuteBase {

    /** Aliases for this note (optional) */
    public final List<String> aliases;
    /** Collection of traits (decorated links, optional) */
    public final Collection<String> traits;
    /** Short creature description (optional) */
    public final String description;
    /** Creature level (number, optional) */
    public final Integer level;
    /** Creature perception (number, optional) */
    public final Integer perception;
    /**
     * Languages as {@link dev.ebullient.convert.tools.pf2e.qute.QuteCreature.CreatureLanguages CreatureLanguages}
     */
    public final CreatureLanguages languages;
    /** Defenses (AC, saves, etc) as {@link dev.ebullient.convert.tools.pf2e.qute.QuteDataDefenses QuteDataDefenses} */
    public final QuteDataDefenses defenses;
    /**
     * Skill bonuses as {@link dev.ebullient.convert.tools.pf2e.qute.QuteCreature.CreatureSkills CreatureSkills}
     */
    public final CreatureSkills skills;

    public QuteCreature(Pf2eSources sources, List<String> text, Tags tags,
            Collection<String> traits, List<String> aliases,
            String description, Integer level, Integer perception,
            QuteDataDefenses defenses, CreatureLanguages languages, CreatureSkills skills) {
        super(sources, text, tags);
        this.traits = traits;
        this.aliases = aliases;
        this.description = description;
        this.level = level;
        this.perception = perception;
        this.languages = languages;
        this.defenses = defenses;
        this.skills = skills;
    }

    /**
     * The languages and language features known by a creature.
     *
     * <p>
     * Referencing this object directly provides a default markup which includes all data. Example:
     * {@code "Common, Sylvan; telepathy 100ft; knows any language the summoner does" }
     * </p>
     *
     * @param languages Languages known (optional)
     * @param notes Language-related notes (optional)
     * @param abilities Language-related abilities (optional)
     */
    @TemplateData
    public record CreatureLanguages(
            List<String> languages,
            List<String> notes,
            List<String> abilities) implements QuteUtil {

        @Override
        public String toString() {
            return Stream.of(
                    languages != null ? List.of(String.join(", ", languages)) : List.<String> of(),
                    abilities, notes)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .dropWhile(String::isEmpty)
                    .collect(Collectors.joining("; "));
        }
    }

    /**
     * A creature's skill information.
     *
     * <p>
     * Referencing this object directly provides a default markup which includes all data. Example:
     * {@code "Athletics +10, Cult Lore +10 (lore on their cult), Stealth +10 (+12 in forests); Some skill note" }
     * </p>
     *
     * @param skills Skill bonuses for the creature, as a list of
     *        {@link dev.ebullient.convert.tools.pf2e.qute.QuteDataSkillBonus QuteDataSkillBonus}
     * @param notes Notes for the creature's skills (list of strings, optional)
     */
    @TemplateData
    public record CreatureSkills(
            List<QuteDataSkillBonus> skills,
            List<String> notes) {

        @Override
        public String toString() {
            return skills.stream().map(QuteDataSkillBonus::toString).collect(Collectors.joining(", ")) +
                    (notes == null ? "" : " " + String.join("; ", notes));
        }
    }
}
