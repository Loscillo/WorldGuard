// $Id$
/*
 * This file is a part of WorldGuard.
 * Copyright (c) sk89q <http://www.sk89q.com>
 * Copyright (c) the WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.sk89q.worldguard.bukkit.definitions;

import com.sk89q.rebar.config.AbstractNodeLoader;
import com.sk89q.rebar.config.ConfigurationNode;
import com.sk89q.rebar.config.LoaderBuilderException;
import com.sk89q.rebar.config.types.StaticFieldLoaderBuilder;
import com.sk89q.rebar.util.LoggerUtils;
import com.sk89q.rulelists.DefinitionException;
import com.sk89q.rulelists.RuleListUtils;
import com.sk89q.rulelists.RuleListsManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.resolvers.BlockResolver;
import com.sk89q.worldguard.bukkit.resolvers.EntityResolver;
import com.sk89q.worldguard.region.flags.DefaultFlag;
import com.sk89q.worldguard.region.flags.StateFlag;

public class FlagCriteriaLoader extends AbstractNodeLoader<FlagCriteria> {

    private final WorldGuardPlugin plugin;
    private final RuleListsManager manager;
    private final StaticFieldLoaderBuilder<StateFlag> stateFlagLoader =
            new StaticFieldLoaderBuilder<StateFlag>(DefaultFlag.class, StateFlag.class);

    public FlagCriteriaLoader(WorldGuardPlugin plugin, RuleListsManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public FlagCriteria read(ConfigurationNode node)
            throws DefinitionException {
        BlockResolver blockResolver = null;
        EntityResolver entityResolver = null;

        String entity = node.getString("entity");
        String block = node.getString("block");

        if (block != null) {
            blockResolver = manager.getResolvers().get(BlockResolver.class, block);
        }

        if (entity != null) {
            entityResolver = manager.getResolvers().get(EntityResolver.class, entity);
        }

        if (block == null && entity == null) {
            throw new LoaderBuilderException("Flag criteria needs either a block resolver or an entity resolver");
        }

        StateFlag flag = node.getOf("flag", stateFlagLoader);
        if (flag == null) {
            throw new LoaderBuilderException("A flag must be specified");
        }

        FlagCriteria criteria = new FlagCriteria(plugin, flag);
        criteria.setBlockResolver(blockResolver);
        criteria.setEntityResolver(entityResolver);

        RuleListUtils.warnUnknown(node, LoggerUtils.getLogger(getClass()),
                                  "flag", "block", "entity");

        return criteria;
    }

}