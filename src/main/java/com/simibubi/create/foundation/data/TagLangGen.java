package com.simibubi.create.foundation.data;

import org.apache.commons.lang3.StringUtils;

import com.simibubi.create.Create;
import com.tterrag.registrate.fabric.BaseLangProvider;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import net.minecraft.resources.ResourceLocation;

/**
 * fabric: creates translations for item tags. These are mostly just for EMI.
 */
public class TagLangGen {
	public static void datagen() {
		Create.REGISTRATE.addDataGenerator(ProviderType.LANG, TagLangGen::genItemTagLang);
	}

	private static void genItemTagLang(RegistrateLangProvider prov) {
		TagLangHelper common = new TagLangHelper("c", prov);
		common.subDir("armors")
				.put("boots", "Boots")
				.put("chestplates", "Chestplates")
				.put("helmets", "Helmets");
		common.subDir("dough")
				.autoRoot()
				.auto("wheat");
		common.subDir("flour")
				.autoRoot()
				.auto("wheat");
		common.subDir("ingots")
				.auto("brass")
				.auto("zinc");
		common.subDir("nuggets")
				.auto("brass")
				.auto("zinc");
		common.subDir("ores")
				.auto("zinc");
		common.subDir("plates")
				.auto("brass")
				.auto("obsidian");
		common.subDir("raw_materials")
				.auto("zinc");
		common.subDir("storage_blocks")
				.auto("andesite_alloy")
				.auto("brass")
				.auto("raw_zinc")
				.auto("zinc");
		common.auto("stripped_logs");
		common.auto("stripped_wood");

		TagLangHelper create = new TagLangHelper(Create.ID, prov);
		create.subDir("blaze_burner_fuel")
				.plural("regular")
				.plural("special");
		create.plural("casing");
		create.put("contraption_controlled", "Actors");
		create.auto("create_ingots");
		create.auto("crushed_raw_materials");
		create.plural("deployable_drink");
		create.auto("modded_stripped_logs");
		create.auto("modded_stripped_wood");
		create.auto("pressurized_air_sources");
		create.auto("sandpaper");
		create.auto("seats");
		create.auto("sleepers");
		create.subDir("stone_types")
				.ignoreDir("andesite")
				.ignoreDir("asurine")
				.ignoreDir("calcite")
				.ignoreDir("crimsite")
				.ignoreDir("deepslate")
				.ignoreDir("diorite")
				.ignoreDir("dripstone")
				.ignoreDir("granite")
				.ignoreDir("limestone")
				.ignoreDir("ochrum")
				.ignoreDir("scorchia")
				.ignoreDir("scoria")
				.ignoreDir("tuff")
				.ignoreDir("veridium");
		create.auto("toolboxes");
		create.auto("valve_handles");
		create.auto("vanilla_stripped_logs");
		create.auto("vanilla_stripped_wood");
	}

	public record TagLangHelper(String namespace, BaseLangProvider prov) {
		public TagLangHelper auto(String path) {
			ResourceLocation id = new ResourceLocation(namespace, path);
			String key = key(id);
			String name = translate(id);
			prov.add(key, name);
			return this;
		}

		public TagLangHelper put(String path, String translated) {
			ResourceLocation id = new ResourceLocation(namespace, path);
			String key = key(id);
			prov.add(key, translated);
			return this;
		}

		public TagLangHelper plural(String path) {
			ResourceLocation id = new ResourceLocation(namespace, path);
			String key = key(id);
			String name = translate(id) + 's';
			prov.add(key, name);
			return this;
		}

		public SubDirHelper subDir(String dir) {
			return new SubDirHelper(this, dir);
		}
	}

	public record SubDirHelper(TagLangHelper parent, String dir) {
		public SubDirHelper put(String path, String translated) {
			ResourceLocation id = tagId(path);
			parent.prov.add(key(id), translated);
			return this;
		}

		public SubDirHelper auto(String path) {
			return put(path, translate(tagId(path)));
		}

		public SubDirHelper ignoreDir(String path) {
			ResourceLocation id = new ResourceLocation(parent.namespace, path);
			return put(path, translate(id));
		}

		public SubDirHelper plural(String path) {
			return put(path, translate(tagId(path)) + 's');
		}

		public SubDirHelper autoRoot() {
			ResourceLocation id = new ResourceLocation(parent.namespace, dir);
			parent.prov.add(key(id), translate(id));
			return this;
		}

		public ResourceLocation tagId(String path) {
			return new ResourceLocation(parent.namespace, dir + '/' + path);
		}
	}

	// automagical utils

	public static String key(ResourceLocation tagId) {
		String namespace = tagId.getNamespace();
		String path = tagId.getPath().replace('/', '.');
		return "tag." + namespace + '.' + path;
	}

	public static String translate(ResourceLocation tagId) {
		String path = tagId.getPath();
		String[] split = path.split("/");
		switch (split.length) {
			case 1 -> { // c:stripped_wood
				return toSentence(split); // Stripped Wood
			}
			case 2 -> { // c:nuggets/iron
				String[] switched = new String[] { split[1], split[0] }; // iron, nuggets
				return toSentence(switched); // Iron Nuggets
			}
			default -> throw new IllegalArgumentException("Tags with >1 subdirectory (" + tagId + ") cannot be automatically translated");
		}
	}

	/**
	 * converts snake_case to Proper Formatting, concatenates segments
	 */
	public static String toSentence(String[] segments) {
		// segments: andesite, stone_types
		StringBuilder sentence = new StringBuilder();
		for (String segment : segments) {
			String[] split = segment.split("_"); // stone, types
			for (String word : split) {
				String capitalized = StringUtils.capitalize(word);
				sentence.append(capitalized).append(' ');
			}
		}
		return sentence.toString().trim();
	}
}
