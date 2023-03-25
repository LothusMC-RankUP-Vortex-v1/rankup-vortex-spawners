package br.net.rankup.spawners.misc;


public class TranslateMob {

	public static String traslateName(String nome) {
		switch (nome) {
		case "COW":
			return "Vaca";
		case "BLAZE":
			return "Blaze";
		case "SHEEP":
			return "Ovelha";
		case "SKELETON":
			return "Esqueleto";
		case "CREEPER":
			return "Creeper";
		case "SPIDER":
			return "Aranha";
		case "ZOMBIE":
			return "Zumbi";
		case "SLIME":
			return "Slime";
		case "GHAST":
			return "Ghast";
		case "PIG_ZOMBIE":
			return "Porco Zumbi";
		case "ENDERMAN":
			return "Enderman";
		case "CAVE_SPIDER":
			return "Aranha da Caverna";
		case "MAGMA_CUBE":
			return "Magma Cube";
		case "BAT":
			return "Morcego";
		case "WITCH":
			return "Bruxa";
		case "ENDERMITE":
			return "Endermite";
		case "GUARDIAN":
			return "Guardião";
		case "PIG":
			return "Porco";		
		case "CHICKEN":
			return "Galinha";
		case "SQUID":
			return "Lula";
		case "WOLF":
			return "Lobo";
		case "MUSHROOM_COW":
			return "Coguvaca";
		case "OCELOT":
			return "Jaguatirica";
		case "HORSE":
			return "Cavalo";
		case "RABBIT":
			return "Coelho";
		case "VILLAGER":
			return "Aldeão";
		case "IRON_GOLEM":
			return "Golem de Ferro";
		case "WITHER":
			return "Wither";
		default:
			return "";
		}
	}

	public static String traslateNameTranslated(String nome) {
		switch (nome) {
		case "Vaca":
			return "COW";
		case "Blaze":
			return "BLAZE";
		case "Ovelha":
			return "SHEEP";
		case "Esqueleto":
			return "SKELETON";
		case "Creeper":
			return "CREEPER";
		case "Aranha":
			return "SPIDER";
		case "Zumbi":
			return "ZOMBIE";
		case "Slime":
			return "SLIME";
		case "Ghast":
			return "GHAST";
		case "Porco Zumbi":
			return "PIG_ZOMBIE";
		case "Enderman":
			return "ENDERMAN";
		case "Aranha da Caverna":
			return "CAVE_SPIDER";
		case "Magma Cube":
			return "MAGMA_CUBE";
		case "Morcego":
			return "BAT";
		case "Bruxa":
			return "WITCH";
		case "Endermite":
			return "ENDERMITE";
		case "Guardião":
			return "GUARDIAN";
		case "Porco":
			return "PIG";
		case "Galinha":
			return "CHICKEN";
		case "Lula":
			return "SQUID";
		case "Lobo":
			return "WOLF";
		case "CoguVaca":
			return "MUSHROOM_COW";
		case "Jaguatirica":
			return "OCELOT";
		case "Cavalo":
			return "HORSE";
		case "Coelho":
			return "RABBIT";
		case "Aldeão":
			return "VILLAGER";
		case "Golem de Ferro":
			return "IRON_GOLEM";
		case "Wither":
			return "WITHER";
		default:
			return "";
		}
	}

	public static String getDrop(String nome) {
		switch (nome) {
		case "Jaguatirica":
			return "RAW_FISH";
		case "Creeper":
			return "SULPHUR";
		case "Vaca":
			return "LEATHER";
		case "Blaze":
			return "BLAZE_ROD";
		case "Ovelha":
			return "BROWN_MUSHROOM";
		case "Esqueleto":
			return "BONE";
		case "Aranha":
			return "SPIDER_EYE";
		case "Zumbi":
			return "ROTTEN_FLESH";
		case "Slime":
			return "SLIME_BALL";
		case "Ghast":
			return "GHAST_TEAR";
		case "Porco Zumbi":
			return "GOLD_NUGGET";
		case "Enderman":
			return "ENDER_PEARL";
		case "Aranha da Caverna":
			return "SPIDER_EYE";
		case "Lula":
			return "PRISMARINE_CRYSTALS";			
		case "Magma Cube":
			return "MAGMA_CREAM";
		case "Bruxa":
			return "POTION";
		case "Endermite":
			return "EYE_OF_ENDER";			
		case "Guardião":
			return "PRISMARINE_SHARD";
		case "Porco":
			return "PORK";
		case "Galinha":
			return "RAW_CHICKEN";
		case "Lobo":
			return "BONE";
		case "CoguVaca":
			return "RAW_BEEF";
		case "Cavalo":
			return "LEATHER";
		case "Coelho":
			return "RABBIT_FOOT";
		case "Aldeão":
			return "EMERALD";
		case "Golem de Ferro":
			return "IRON_INGOT";
		case "Wither":
			return "NETHER_STAR";		
		default:
			return "";
		}
	}

}
