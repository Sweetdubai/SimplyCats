package com.github.mnesikos.simplycats.entity.core;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Tuple;

import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class Genetics {
    private Genetics() {}

    public enum Sex implements IStringSerializable {
        MALE,
        FEMALE;

        public String getName() {
            return name().toLowerCase();
        }
    }

    public enum EyeColor implements IStringSerializable {
        COPPER,
        GOLD,
        HAZEL,
        GREEN,
        BLUE,
        ODD_LEFT, // todo
        ODD_RIGHT; // am I really gonna do this idek

        EyeColor() {

        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    public enum FurLength implements GeneticType {
        SHORT("L"),
        LONG("l");

        private final String allele;

        FurLength(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static FurLength init(Random rand) {
            return rand.nextFloat() <= 0.75f ? SHORT : LONG;
        }

        public static FurLength getPhenotype(GeneticData<FurLength> furLength) {
            return furLength.contains(SHORT) ? SHORT : LONG;
        }
    }

    public enum Eumelanin implements GeneticType {
        BLACK("B"),
        CHOCOLATE("b"),
        CINNAMON("b1");

        private final String allele;

        Eumelanin(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Eumelanin init(Random rand) {
            float chance = rand.nextFloat();
            if (chance <= 0.80F)
                return BLACK; // 80% chance
            else if (chance > 0.80F && chance <= 0.96F)
                return CHOCOLATE; // 16% chance
            else
                return CINNAMON; // 4% chance
        }

        public static Eumelanin getPhenotype(GeneticData<Eumelanin> eumelanin) {
            if (eumelanin.contains(BLACK))
                return BLACK;
            else if (eumelanin.contains(CHOCOLATE))
                return CHOCOLATE;
            else
                return CINNAMON;
        }
    }

    public enum Phaeomelanin implements GeneticType {
        NOT_RED("Xo"),
        RED("XO"),
        MALE("Y"),
        TORTOISESHELL;

        private String allele;

        Phaeomelanin(String allele) {
            this.allele = allele;
        }
        Phaeomelanin() {}

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static int init(Random rand) {
            float chance1 = rand.nextFloat();
            Phaeomelanin allele1;
            if (chance1 <= 0.75f)
                allele1 = NOT_RED; // 75% chance
            else
                allele1 = RED; // 25% chance
            float chance2 = rand.nextFloat();
            Phaeomelanin allele2;
            if (allele1.equals(NOT_RED)) {
                if (chance2 <= 0.75f)
                    allele2 = NOT_RED; // 75% chance not red
                else
                    allele2 = RED; // 25% chance tortie
            } else {
                if (chance2 <= 0.75f)
                    allele2 = RED; // 75% chance red
                else
                    allele2 = NOT_RED; // 25% chance tortie
            }
            return allele1.ordinal() | ((rand.nextInt(2) == 0 ? MALE : allele2).ordinal() << 2);
        }

        public static Phaeomelanin getPhenotype(GeneticData<Phaeomelanin> phaeomelanin) {
            if ((phaeomelanin.getFirst() == RED && phaeomelanin.getSecond() == MALE) || phaeomelanin.getFirst() == RED && phaeomelanin.getSecond() == RED) return RED;
            else if ((phaeomelanin.getFirst() == NOT_RED && phaeomelanin.getSecond() == MALE) || phaeomelanin.getFirst() == NOT_RED && phaeomelanin.getSecond() == NOT_RED) return NOT_RED;
            else if (phaeomelanin.contains(RED) && phaeomelanin.contains(NOT_RED)) return TORTOISESHELL;
            throw new IllegalArgumentException("Invalid phaeomelanin: " + phaeomelanin);
        }
    }

    public enum Dilution implements GeneticType {
        NON_DILUTE("D"),
        DILUTE("d");

        private final String allele;

        Dilution(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Dilution init(Random rand) {
            return rand.nextFloat() <= 0.60f ? NON_DILUTE : DILUTE;
        }

        public static Dilution getPhenotype(GeneticData<Dilution> dilution) {
            return dilution.contains(NON_DILUTE) ? NON_DILUTE : DILUTE;
        }
    }

    public enum DiluteMod implements GeneticType {
        NORMAL("dm"),
        CARAMELIZED("Dm");

        private final String allele;

        DiluteMod(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static DiluteMod init(Random rand) {
            return rand.nextFloat() <= 0.96f ? NORMAL : CARAMELIZED;
        }

        public static DiluteMod getPhenotype(GeneticData<DiluteMod> diluteMod) {
            return diluteMod.contains(CARAMELIZED) ? CARAMELIZED : NORMAL;
        }
    }

    public enum Agouti implements GeneticType {
        SOLID("a"),
        TABBY("A");

        private final String allele;

        Agouti(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Agouti init(Random rand) {
            return rand.nextFloat() <= 0.80f ? SOLID : TABBY;
        }

        public static Agouti getPhenotype(GeneticData<Agouti> agouti) {
            return agouti.contains(TABBY) ? TABBY : SOLID;
        }
    }

    public enum Tabby implements GeneticType {
        MACKEREL("Mc"),
        CLASSIC("mc");

        private final String allele;

        Tabby(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Tabby init(Random rand) {
            return rand.nextFloat() <= 0.50f ? MACKEREL : CLASSIC;
        }

        public static Tabby getPhenotype(GeneticData<Tabby> tabby) {
            return tabby.contains(MACKEREL) ? MACKEREL : CLASSIC;
        }
    }

    public enum Spotted implements GeneticType {
        NORMAL("sp"),
        SPOTTED("Sp"),
        BROKEN;

        private String allele;

        Spotted(String allele) {
            this.allele = allele;
        }
        Spotted() {}

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Spotted init(Random rand) {
            return rand.nextFloat() <= 0.80f ? NORMAL : SPOTTED;
        }

        public static Spotted getPhenotype(GeneticData<Spotted> spotted) {
            if (spotted.getFirst() == SPOTTED && spotted.getSecond() == SPOTTED) return SPOTTED;
            else if (spotted.contains(SPOTTED) && spotted.contains(NORMAL)) return BROKEN;
            else if (spotted.getFirst() == NORMAL && spotted.getSecond() == NORMAL) return NORMAL;
            throw new IllegalArgumentException("Invalid spotted: " + spotted);
        }
    }

    public enum Ticked implements GeneticType {
        NORMAL("ta"),
        TICKED("Ta");

        private final String allele;

        Ticked(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Ticked init(Random rand) {
            return rand.nextFloat() <= 0.96F ? NORMAL : TICKED;
        }

        public static Ticked getPhenotype(GeneticData<Ticked> ticked) {
            return ticked.contains(TICKED) ? TICKED : NORMAL;
        }
    }

    // bengal modifier

    // silver/smoke

    // wide band

    // amber

    public enum Colorpoint implements GeneticType {
        NOT_POINTED("C"),
        COLORPOINT("cs"),
        SEPIA("cb"),
        MINK;

        private String allele;

        Colorpoint(String allele) {
            this.allele = allele;
        }
        Colorpoint() {}

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static Colorpoint init(Random rand) {
            float chance = rand.nextFloat();
            if (chance <= 0.80F)
                return NOT_POINTED; // 80% chance
            else if (chance > 0.80F && chance <= 0.96F)
                return COLORPOINT; // 16% chance
            else
                return SEPIA; // 4% chance
        }

        public static Colorpoint getPhenotype(GeneticData<Colorpoint> colorpoint) {
            if (colorpoint.contains(NOT_POINTED)) return NOT_POINTED;
            else if (colorpoint.getFirst() == COLORPOINT && colorpoint.getSecond() == COLORPOINT) return COLORPOINT;
            else if (colorpoint.contains(COLORPOINT) && colorpoint.contains(SEPIA)) return MINK;
            else if (colorpoint.getFirst() == SEPIA && colorpoint.getSecond() == SEPIA) return SEPIA;
            throw new IllegalArgumentException("Invalid colorpoint: " + colorpoint);
        }
    }

    public enum White implements GeneticType {
        NONE("w"),
        SPOTTING("Ws"),
        DOMINANT("Wd");

        private final String allele;

        White(String allele) {
            this.allele = allele;
        }

        public String getAllele() {
            return allele;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        public static White init(Random rand) {
            float chance = rand.nextFloat();
            if (chance <= 0.49F)
                return NONE; // 49% chance
            else if (chance > 0.49F && chance <= 0.98F)
                return SPOTTING; // 49% chance
            else
                return DOMINANT; // 2% chance
        }

        public static White getPhenotype(GeneticData<White> white) {
            if (white.contains(DOMINANT)) return DOMINANT;
            else if ((white.getFirst() == SPOTTING && white.getSecond() == SPOTTING) || (white.contains(SPOTTING) && white.contains(NONE))) return SPOTTING;
            else if (white.getFirst() == NONE && white.getSecond() == NONE) return NONE;
            throw new IllegalArgumentException("Invalid white: " + white);
        }
    }

    public interface GeneticType extends IStringSerializable {
        String getAllele();
    }

    public static class GeneticData<T extends GeneticType> extends Tuple<T, T> {
        private final Function<GeneticData<T>, T> phenotype;
        private final IntFunction<T[]> array;

        public GeneticData(T aIn, T bIn, Function<GeneticData<T>, T> phenotype, IntFunction<T[]> array) {
            super(aIn, bIn);
            this.phenotype = phenotype;
            this.array = array;
        }

        public boolean contains(T type) {
            return getFirst() == type || getSecond() == type;
        }

        public T getPhenotype() {
            return phenotype.apply(this);
        }

        public T[] getValues() {
            T[] arr = array.apply(2);
            arr[0] = getFirst();
            arr[1] = getSecond();
            return arr;
        }

        @Override
        public String toString() {
            return getFirst().getAllele() + "-" + getSecond().getAllele();
        }
    }
}
