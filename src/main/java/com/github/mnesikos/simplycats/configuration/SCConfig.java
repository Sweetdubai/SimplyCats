package com.github.mnesikos.simplycats.configuration;

import com.github.mnesikos.simplycats.Ref;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Ref.MODID, name = "SimplyCats/" + Ref.MODID + "-" + Ref.VERSION)
@LangKey("config." + Ref.MODID + ".title")
public class SCConfig {
    private static final String PREFIX = "config." + Ref.MODID;

    @Name("Join Message")
    @Comment("Enable or disables the initial join message with a player's cat count.")
    @LangKey(PREFIX + ".join_message")
    public static boolean JOIN_MESSAGE = false;

    @Name("Adopt-a-Dog")
    @Comment("Disabling this will remove the villager trade to get a one-time-use pet carrier containing a dog.")
    @LangKey(PREFIX + ".adopt_a_dog")
    public static boolean ADOPT_A_DOG = true;

    @Name("Cat Attack AI")
    @Comment("Disabling this will not allow cats to attack entities in their prey list, essentially a peaceful mode for cats.")
    @LangKey(PREFIX + ".attack_ai")
    public static boolean ATTACK_AI = true;

    @Name("Cats' Prey List")
    @Comment("This is a list of entities all cats will attack on sight if cat attack AI is enabled.")
    @LangKey(PREFIX + ".prey_list")
    public static String[] PREY_LIST = new String[]{"minecraft:bat", "minecraft:parrot", "minecraft:chicken",
            "minecraft:rabbit", "minecraft:silverfish", "rats:rat", "zawa:brownrat", "zawa:cockatoo", "zawa:frigate",
            "zawa:macaw", "zawa:rattlesnake", "zawa:toucan", "zawa:treefrog", "exoticbirds:woodpecker", "birdwmod:brown_booby",
            "birdwmod:eastern_bluebird", "birdwmod:eurasian_bullfinch", "birdwmod:great_grey_owl", "birdwmod:green_heron",
            "birdwmod:hoatzin", "birdwmod:killdeer", "birdwmod:kingofsaxony_bird_of_paradise", "birdwmod:northern_mockingbird",
            "birdwmod:redflanked_bluetail", "birdwmod:rednecked_nightjar", "birdwmod:stellers_eider", "birdwmod:turquoisebrowed_motmot",
            "exoticbirds:bluejay", "exoticbirds:booby", "exoticbirds:budgerigar", "exoticbirds:cardinal", "exoticbirds:duck",
            "exoticbirds:gouldianfinch", "exoticbirds:hummingbird", "exoticbirds:kingfisher", "exoticbirds:kiwi",
            "exoticbirds:kookaburra", "exoticbirds:lyrebird", "exoticbirds:magpie", "exoticbirds:parrot", "exoticbirds:pigeon",
            "exoticbirds:roadrunner", "exoticbirds:robin", "exoticbirds:toucan", "animania:hamster", "animania:frog",
            "animania:toad", "animania:buck_cottontail", "animania:doe_cottontail", "animania:kit_cottontail", "animania:buck_chinchilla",
            "animania:doe_chinchilla", "animania:kit_chinchilla", "animania:buck_dutch", "animania:doe_dutch", "animania:kit_dutch",
            "animania:buck_havana", "animania:doe_havana", "animania:kit_havana", "animania:buck_jack", "animania:doe_jack", "animania:kit_jack",
            "animania:buck_new_zealand", "animania:doe_new_zealand", "animania:kit_new_zealand", "animania:buck_rex", "animania:doe_rex",
            "animania:kit_rex", "animania:buck_lop", "animania:doe_lop", "animania:kit_lop", "animania:rooster_leghorn", "animania:rooster_orpington",
            "animania:rooster_plymouth_rock", "animania:rooster_rhode_island_red", "animania:rooster_wyandotte", "animania:hen_leghorn",
            "animania:hen_orpington", "animania:hen_plymouth_rock", "animania:hen_rhode_island_red", "animania:hen_wyandotte",
            "animania:chick_leghorn", "animania:chick_orpington", "animania:chick_plymouth_rock", "animania:chick_rhode_island_red",
            "animania:chick_wyandotte"};

    @Name("Wander Area Limit")
    @RangeDouble(min = 10.0D)
    @Comment("When a cat's home is set, this is the distance in blocks they are allowed to roam.")
    @LangKey(PREFIX + ".wander_area_limit")
    @RequiresMcRestart
    public static double WANDER_AREA_LIMIT = 400.0D;

    @Name("Tamed Cats Limit")
    @RangeInt(min = 0)
    @Comment("Sets a limit of cats each player is allowed to have tamed, setting this to 0 will disable the limit.")
    @LangKey(PREFIX + ".tamed_limit")
    public static int TAMED_LIMIT = 0;

    @Name("Cats in Area Breeding Limit")
    @Comment("This number is used to limit cat breeding; if more than this amount of cats are nearby, automatic breeding will be disabled.")
    @LangKey(PREFIX + ".breeding_limit")
    public static int BREEDING_LIMIT = 20;

    @Name("Kitten Mature Timer")
    @Comment({
            "Number of minecraft ticks before a kitten becomes an adult.",
            "Default: 168000 (7 full minecraft days)"
    })
    @LangKey(PREFIX + ".kitten_mature_timer")
    public static int KITTEN_MATURE_TIMER = 24000 * 7;

    @Name("Pregnancy Timer")
    @Comment({
            "Number of minecraft ticks before a pregnant cat will give birth.",
            "Default: 96000 (4 full minecraft days)"
    })
    @LangKey(PREFIX + ".pregnancy_timer")
    public static int PREGNANCY_TIMER = 24000 * 4;

    @Name("Heat Timer")
    @Comment({
            "Number of minecraft ticks that a cat will be in heat.",
            "Default: 48000 (2 full minecraft days)"
    })
    @LangKey(PREFIX + ".heat_timer")
    public static int HEAT_TIMER = 24000 * 2;

    @Name("Heat Cooldown")
    @Comment({
            "Number of minecraft ticks that a cat will not go into heat.",
            "Default: 384000 (16 full minecraft days)"
    })
    @LangKey(PREFIX + ".heat_cooldown")
    public static int HEAT_COOLDOWN = 24000 * 16;

    @Name("Male Cooldown")
    @Comment({
            "Number of minecraft ticks that a male cat will not try to breed after breeding once already.",
            "Default: 6000 (1/4th minecraft day)"
    })
    @LangKey(PREFIX + ".male_cooldown")
    public static int MALE_COOLDOWN = 24000 / 4;

    @Mod.EventBusSubscriber(modid = Ref.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Ref.MODID)) {
                ConfigManager.sync(Ref.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
