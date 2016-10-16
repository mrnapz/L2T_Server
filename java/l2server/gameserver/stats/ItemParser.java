/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.gameserver.stats;

import l2server.gameserver.model.L2CrystallizeReward;
import l2server.gameserver.model.L2ExtractableProduct;
import l2server.gameserver.stats.conditions.Condition;
import l2server.gameserver.stats.funcs.FuncTemplate;
import l2server.gameserver.templates.StatsSet;
import l2server.gameserver.templates.item.L2EtcItem;
import l2server.gameserver.templates.item.L2Item;
import l2server.log.Log;
import l2server.util.xml.XmlNode;

import java.lang.reflect.Constructor;
import java.util.Map.Entry;

/**
 * @author mkizub, JIV
 */
public final class ItemParser extends StatsParser
{
	private String type;
	private StatsSet set;
	private L2Item item = null;

	public ItemParser(XmlNode node)
	{
		super(node);
	}

	@Override
	protected StatsSet getStatsSet()
	{
		return this.set;
	}

	@Override
	public void parse() throws RuntimeException
	{
		this.type = this.node.getString("type");
		this.set = new StatsSet();
		for (Entry<String, String> e : this.node.getAttributes().entrySet())
		{
			this.set.set(e.getKey(), e.getValue());
		}

		makeItem();

		parseChildren();

		if (this.node.hasAttribute("rCrit") && !this.node.hasAttribute("mCritRate"))
		{
			this.node.getAttributes().put("mCritRate", this.node.getString("rCrit"));
		}
		parseTemplate(this.node, this.item);
	}

	public void parse(ItemParser original) throws RuntimeException
	{
		this.type = this.node.getString("type", original.type);

		this.set = new StatsSet();
		this.set.add(original.set);

		for (Entry<String, String> e : this.node.getAttributes().entrySet())
		{
			this.set.set(e.getKey(), e.getValue());
		}

		makeItem();

		if (original.item.getConditions() != null && !this.set.getBool("overrideCond", false))
		{
			for (Condition cond : original.item.getConditions())
			{
				this.item.attach(cond);
			}
		}

		if (original.item.getSkills() != null && !this.set.getBool("overrideSkills", false))
		{
			for (SkillHolder sh : original.item.getSkills())
			{
				this.item.attach(sh);
			}
		}

		parseChildren();

		if (original.item.getFuncs() != null && !this.set.getBool("overrideStats", false))
		{
			for (FuncTemplate func : original.item.getFuncs())
			{
				this.item.attach(func);
			}
		}

		parseTemplate(this.node, this.item);
	}

	private void parseChildren()
	{
		for (XmlNode n : this.node.getChildren())
		{
			if (n.getName().equalsIgnoreCase("cond"))
			{
				Condition condition = parseCondition(n.getFirstChild(), this.item);
				if (condition != null && n.hasAttribute("msg"))
				{
					condition.setMessage(n.getString("msg"));
				}
				else if (condition != null && n.hasAttribute("msgId"))
				{
					condition.setMessageId(Integer.decode(getValue(n.getString("msgId"))));
					if (n.hasAttribute("addName") && Integer.decode(getValue(n.getString("msgId"))) > 0)
					{
						condition.addName();
					}
				}
				this.item.attach(condition);
			}
			else if (n.getName().equalsIgnoreCase("skill"))
			{
				int skillId = n.getInt("id");
				int skillLvl = n.getInt("level");
				this.item.attach(new SkillHolder(skillId, skillLvl));
			}
			else if (n.getName().equalsIgnoreCase("crystallizeReward"))
			{
				int itemId = n.getInt("id");
				int count = n.getInt("count");
				double chance = n.getDouble("chance");
				this.item.attach(new L2CrystallizeReward(itemId, count, chance));
			}
			else if (n.getName().equalsIgnoreCase("capsuledItem") && this.item instanceof L2EtcItem)
			{
				int itemId = n.getInt("id");
				int min = n.getInt("min");
				int max = n.getInt("max");
				double chance = n.getDouble("chance");
				if (max < min)
				{
					Log.info("> Max amount < Min amount in part " + itemId + ", item " + this.item);
					continue;
				}
				((L2EtcItem) this.item).attach(new L2ExtractableProduct(itemId, min, max, chance));
			}
		}
	}

	private void makeItem() throws RuntimeException
	{
		if (this.item != null)
		{
			return; // item is already created
		}
		try
		{
			Constructor<?> c =
					Class.forName("l2server.gameserver.templates.item.L2" + this.type).getConstructor(StatsSet.class);
			this.item = (L2Item) c.newInstance(this.set);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public L2Item getItem()
	{
		return this.item;
	}
}
