package dev.ebullient.convert.tools.pf2e.qute;

import static dev.ebullient.convert.StringUtil.flatJoin;
import static dev.ebullient.convert.StringUtil.join;
import static dev.ebullient.convert.StringUtil.joinWithPrefix;

import java.util.List;
import java.util.StringJoiner;

import dev.ebullient.convert.StringUtil;
import dev.ebullient.convert.qute.QuteUtil;
import dev.ebullient.convert.tools.pf2e.Pf2eTypeReader.Pf2eStat;
import io.quarkus.qute.TemplateData;

/**
 * Hit Points, Hardness, and a broken threshold for hazards and shields. Used for creatures, hazards, and shields.
 *
 * <p>
 * Hazard example with a broken threshold and note:
 * </p>
 * <blockquote><b>Hardness</b> 10, <b>HP (BT)</b> 30 (15) to destroy a channel gate</blockquote>
 *
 * <p>
 * Hazard example with a name, broken threshold, and note:
 * </p>
 * <blockquote><b>Floor Hardness</b> 10, <b>Floor HP</b> 30 (BT 15) to destroy a channel gate</blockquote>
 *
 * <p>
 * Creature example with a name and ability:
 * </p>
 * <blockquote><b>Head Hardness</b> 10, <b>Head HP</b> 30 (hydra regeneration)</blockquote>
 *
 * @param hp The HP as a {@link dev.ebullient.convert.tools.pf2e.qute.QuteDataHpHardnessBt.HpStat HpStat} (optional)
 * @param hardness Hardness as a {@link dev.ebullient.convert.tools.pf2e.Pf2eTypeReader.Pf2eStat Pf2eStat} (optional)
 * @param brokenThreshold Broken threshold as an integer (optional, not populated for creatures)
 */
@TemplateData
public record QuteDataHpHardnessBt(HpStat hp, Pf2eStat hardness, Integer brokenThreshold) implements QuteUtil {

    @Override
    public String toString() {
        return toStringWithName("");
    }

    /** Return a representation of these stats with the given name used to label each component. */
    public String toStringWithName(String name) {
        name = name.isEmpty() ? "" : name + " ";
        StringJoiner parts = new StringJoiner(", ");
        if (hardness != null) {
            parts.add("**%sHardness** %s".formatted(name, hardness));
        }
        if (hp != null && hp.value != null) {
            // If we have a BT but no name, then put the BT label next to the HP label. Otherwise, the BT label goes
            // next to the BT value.
            boolean labelBtWithHp = isPresent(brokenThreshold) && name.isEmpty();
            parts.add(join(" ",
                    (labelBtWithHp ? "**%sHP (BT)**" : "**%sHP**").formatted(name),
                    hp.value,
                    isPresent(brokenThreshold) ? (labelBtWithHp ? "(%d)" : "(BT %d)").formatted(brokenThreshold) : null,
                    hp.formattedNotes()));
        }
        return parts.toString();
    }

    /**
     * HP value and associated notes. Referencing this directly provides a default representation, e.g.
     * <blockquote>
     * 15 to destroy a head (head regrowth)
     * </blockquote>
     *
     * @param value The HP value itself
     * @param abilities Any abilities associated with the HP
     * @param notes Any notes associated with the HP.
     */
    @TemplateData
    public record HpStat(Integer value, List<String> notes, List<String> abilities) implements Pf2eStat {
        public HpStat(Integer value) {
            this(value, null);
        }

        public HpStat(Integer value, String note) {
            this(value, note == null || note.isBlank() ? List.of() : List.of(note), List.of());
        }

        @Override
        public String toString() {
            return join(" ", value, formattedNotes());
        }

        /** Returns any notes and abilities formatted as a string. */
        @Override
        public String formattedNotes() {
            return flatJoin(" ",
                    List.of(join(", ", notes)),
                    abilities.stream().map(StringUtil::parenthesize).toList());
        }
    }
}
