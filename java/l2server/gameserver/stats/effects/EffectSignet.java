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

package l2server.gameserver.stats.effects;

import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.L2Effect;
import l2server.gameserver.model.L2Skill;
import l2server.gameserver.model.actor.L2Character;
import l2server.gameserver.model.actor.instance.L2EffectPointInstance;
import l2server.gameserver.model.actor.instance.L2PcInstance;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.MagicSkillUse;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.gameserver.stats.Env;
import l2server.gameserver.stats.skills.L2SkillSignet;
import l2server.gameserver.stats.skills.L2SkillSignetCasttime;
import l2server.gameserver.templates.skills.L2AbnormalType;
import l2server.gameserver.templates.skills.L2EffectTemplate;

import java.util.ArrayList;

/**
 * @authors Forsaiken, Sami
 */
public class EffectSignet extends L2Effect
{
	private L2Skill skill;
	private L2EffectPointInstance actor;
	private boolean srcInArena;

	public EffectSignet(Env env, L2EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public L2AbnormalType getAbnormalType()
	{
		return L2AbnormalType.SIGNET_EFFECT;
	}

	/**
	 * @see l2server.gameserver.model.L2Abnormal#onStart()
	 */
	@Override
	public boolean onStart()
	{
		if (getSkill() instanceof L2SkillSignet)
		{
			this.skill = SkillTable.getInstance()
					.getInfo(((L2SkillSignet) getSkill()).effectId, ((L2SkillSignet) getSkill()).effectLevel);
		}
		else if (getSkill() instanceof L2SkillSignetCasttime)
		{
			this.skill = SkillTable.getInstance().getInfo(((L2SkillSignetCasttime) getSkill()).effectId, getLevel());
		}
		this.actor = (L2EffectPointInstance) getEffected();
		this.srcInArena =
				getEffector().isInsideZone(L2Character.ZONE_PVP) && !getEffector().isInsideZone(L2Character.ZONE_SIEGE);
		return true;
	}

	/**
	 * @see l2server.gameserver.model.L2Abnormal#onActionTime()
	 */
	@Override
	public boolean onActionTime()
	{
		if (this.skill == null)
		{
			return true;
		}

		int mpConsume = this.skill.getMpConsume();

		if (mpConsume > getEffector().getCurrentMp())
		{
			getEffector().sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP));
			return false;
		}
		else
		{
			getEffector().reduceCurrentMp(mpConsume);
		}

		boolean signetActor = calc() != 0;

		final ArrayList<L2Character> targets = new ArrayList<>();
		for (L2Character cha : this.actor.getKnownList().getKnownCharactersInRadius(getSkill().getSkillRadius()))
		{
			if (cha == null)
			{
				continue;
			}

			if (this.skill.isOffensive() && !L2Skill.checkForAreaOffensiveSkills(getEffector(), cha, this.skill, this.srcInArena))
			{
				continue;
			}

			if (cha instanceof L2PcInstance)
			{
				L2PcInstance player = (L2PcInstance) cha;
				if (!player.isInsideZone(L2Character.ZONE_PVP) && player.getPvpFlag() == 0)
				{
					continue;
				}
			}

			// there doesn't seem to be a visible effect with MagicSkillLaunched packet...
			if (!signetActor)
			{
				this.actor.broadcastPacket(new MagicSkillUse(this.actor, cha, this.skill.getId(), this.skill.getLevelHash(), 0, 0, 0));
			}
			targets.add(cha);
		}

		if (signetActor)
		{
			//_actor.broadcastPacket(new TargetSelected(this.actor.getObjectId(), this.actor.getObjectId(), this.actor.getX(), this.actor.getY(), this.actor.getZ()));
			this.actor.broadcastPacket(new MagicSkillUse(this.actor, this.skill.getId(), this.skill.getLevelHash(), 0, 0));
			//_actor.broadcastPacket(new MagicSkillLaunched(this.actor, this.skill.getId(), this.skill.getLevel(), targets.toArray(new L2Character[targets.size()])));
		}

		if (!targets.isEmpty())
		{
			if (!signetActor)
			{
				getEffector().callSkill(this.skill, targets.toArray(new L2Character[targets.size()]));
			}
			else
			{
				this.actor.callSkill(this.skill, targets.toArray(new L2Character[targets.size()]));
			}
		}
		return true;
	}

	/**
	 * @see l2server.gameserver.model.L2Abnormal#onExit()
	 */
	@Override
	public void onExit()
	{
		if (this.actor != null)
		{
			this.actor.deleteMe();
		}
	}
}
